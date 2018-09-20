package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.CorporaRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullPointer;
import com.sonnets.sonnet.tools.ItemKeyValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Handles CRUD for Corpora.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SqlResolve")
@Service
@Transactional()
public class CorporaService {
    private static final Logger LOGGER = Logger.getLogger(CorporaService.class);
    private final CorporaRepository corporaRepository;
    private final GetObjectOrThrowNullPointer getObjectOrNull;
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public CorporaService(CorporaRepository corporaRepository, GetObjectOrThrowNullPointer getObjectOrNull,
                          EntityManager em) {
        this.corporaRepository = corporaRepository;
        this.getObjectOrNull = getObjectOrNull;
        this.em = em;
    }

    /**
     * Add a new corpora to the db.
     *
     * @param corporaDto the data for the new corpora.
     * @return OK if the corpora is added.
     */
    public ResponseEntity<Void> createCorpora(CorporaDto corporaDto) {
        LOGGER.debug("Creating corpera: " + corporaDto);
        Corpora newCorpora = new Corpora();
        newCorpora.setName(corporaDto.getName());
        newCorpora.setDescription(corporaDto.getDescription());
        corporaRepository.save(newCorpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get a single corpora by db id.
     *
     * @param corporaId the id of the corpora to get.
     * @return the corpora or null if not found.
     */
    public Corpora getSingle(String corporaId) {
        LOGGER.debug("Getting corpora by id:" + corporaId);
        return ((Corpora) em.createNativeQuery("SELECT [id], [created_by], [created_date], [last_modified_by], " +
                        "[last_modified_date], [description], [name], [total_items] FROM [corpora] WHERE id = :id",
                "CorporaMap")
                .setParameter("id", Long.valueOf(corporaId))
                .getSingleResult());
    }

    public ResponseEntity<Void> addSingleItem(String type, String corporaId, String itemId) {
        LOGGER.debug("Adding single item: ");
        Query query;
        switch (type) {
            case "POETRY":
                query = em.createNativeQuery("IF NOT EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                        "LIKE 'POEM' AND item_id = :itemId) " +
                        "INSERT INTO [corpora_items] (corpora_id, item_type, item_id) " +
                        "VALUES (:corporaId, 'POEM', :itemId)")
                        .setParameter("corporaId", Long.valueOf(corporaId))
                        .setParameter("itemId", Long.valueOf(itemId));
                break;
            case "PROSE":
                query = em.createNativeQuery("IF NOT EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                        "LIKE 'BOOK' AND item_id = :itemId) " +
                        "INSERT INTO [corpora_items] (corpora_id, item_type, item_id) " +
                        "VALUES (:corporaId, 'BOOK', :itemId)")
                        .setParameter("corporaId", Long.valueOf(corporaId))
                        .setParameter("itemId", Long.valueOf(itemId));
                break;
            case "SECTION":
                query = em.createNativeQuery("IF NOT EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                        "LIKE 'SECT' AND item_id = :itemId) " +
                        "INSERT INTO [corpora_items] (corpora_id, item_type, item_id) " +
                        "VALUES (:corporaId, 'SECT', :itemId)")
                        .setParameter("corporaId", Long.valueOf(corporaId))
                        .setParameter("itemId", Long.valueOf(itemId));
                break;
            default:
                throw new RuntimeException(String.format("Item type: '%s' does not exist.", type));
        }
        query.executeUpdate();
        int count = (int) em.createNativeQuery("SELECT COUNT(*) FROM [corpora_items] WHERE corpora_id = :corporaId")
                .setParameter("corporaId", Long.valueOf(corporaId))
                .getSingleResult();

        em.createNativeQuery("UPDATE  [corpora] SET total_items = :totalItems WHERE id = :corporaId")
                .setParameter("totalItems", count)
                .setParameter("corporaId", corporaId)
                .executeUpdate();

        em.joinTransaction();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Add items to a corpora.
     *
     * @param dto the dto with the items to add.
     * @return OK if the items are added.
     */
    public ResponseEntity<Void> addItems(CorporaItemsDto dto) {
        LOGGER.debug("Adding items: " + dto.getIds());
        for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
            Query query;
            switch (pair.getKey()) {
                case "POETRY":
                    query = em.createNativeQuery("IF NOT EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                            "LIKE 'POEM' AND item_id = :itemId) " +
                            "INSERT INTO [corpora_items] (corpora_id, item_type, item_id) " +
                            "VALUES (:corporaId, 'POEM', :itemId)")
                            .setParameter("corporaId", dto.getId())
                            .setParameter("itemId", pair.getValue());
                    break;
                case "PROSE":
                    query = em.createNativeQuery("IF NOT EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                            "LIKE 'BOOK' AND item_id = :itemId) " +
                            "INSERT INTO [corpora_items] (corpora_id, item_type, item_id) " +
                            "VALUES (:corporaId, 'BOOK', :itemId)")
                            .setParameter("corporaId", dto.getId())
                            .setParameter("itemId", pair.getValue());
                    break;
                case "SECTION":
                    query = em.createNativeQuery("IF NOT EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                            "LIKE 'SECT' AND item_id = :itemId) " +
                            "INSERT INTO [corpora_items] (corpora_id, item_type, item_id) " +
                            "VALUES (:corporaId, 'SECT', :itemId)")
                            .setParameter("corporaId", dto.getId())
                            .setParameter("itemId", pair.getValue());
                    break;
                default:
                    throw new RuntimeException(String.format("Item type: '%s' does not exist.", pair.getKey()));
            }
            query.executeUpdate();
            em.joinTransaction();
        }

        int count = (int) em.createNativeQuery("SELECT COUNT(*) FROM [corpora_items] WHERE corpora_id = :corporaId")
                .setParameter("corporaId", dto.getId())
                .getSingleResult();

        em.createNativeQuery("UPDATE  [corpora] SET total_items = :totalItems WHERE id = :corporaId")
                .setParameter("totalItems", count)
                .setParameter("corporaId", dto.getId())
                .executeUpdate();
        em.joinTransaction();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Remove items from a corpora. DO NOT FUCK WITH THE QUERIES.
     *
     * @param dto the dto with the items to remove.
     * @return OK if the items are removed.
     */
    @Transactional
    public ResponseEntity<Void> removeItems(CorporaItemsDto dto) {
        LOGGER.debug("Removing items: " + dto.getIds());

        for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
            Query query;
            switch (pair.getKey()) {
                case "POETRY":
                    query = em.createNativeQuery("IF EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                            "LIKE 'POEM' AND item_id = :itemId) " +
                            "DELETE FROM [corpora_items] WHERE item_type LIKE 'POEM' AND item_id = :itemId")
                            .setParameter("itemId", pair.getValue());
                    break;
                case "PROSE":
                    query = em.createNativeQuery("IF EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                            "LIKE 'POEM' AND item_id = :itemId) " +
                            "DELETE FROM [corpora_items] WHERE item_type LIKE 'BOOK' AND item_id = :itemId")
                            .setParameter("itemId", pair.getValue());
                    break;
                case "SECTION":
                    query = em.createNativeQuery("IF EXISTS (SELECT * FROM [corpora_items] WHERE item_type " +
                            "LIKE 'POEM' AND item_id = :itemId) " +
                            "DELETE FROM [corpora_items] WHERE item_type LIKE 'SECT' AND item_id = :itemId")
                            .setParameter("itemId", pair.getValue());
                    break;
                default:
                    throw new RuntimeException(String.format("Item type: '%s' does not exist.", pair.getKey()));
            }
            query.executeUpdate();
            em.joinTransaction();
        }

        int count = em.createNativeQuery("SELECT corpora_id FROM [corpora_items] " +
                "WHERE corpora_id = :corporaId")
                .setParameter("corporaId", dto.getId())
                .getResultList().size();

        Query query = em.createNativeQuery("UPDATE [corpora]" +
                "SET total_items = :itemCount " +
                "WHERE id = :corporaId")
                .setParameter("itemCount", count)
                .setParameter("corporaId", dto.getId());
        query.executeUpdate();
        em.joinTransaction();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify an existing corpora.
     *
     * @param corporaId   the id of the corpora to modify.
     * @param name        the new/old name of the corpora.
     * @param description the new/old description of the corpora.
     * @return OK if the corpora is modified.
     */
    public ResponseEntity<Void> modify(String corporaId, String name, String description) {
        LOGGER.debug("Changing name of " + corporaId + " to " + name);
        Corpora corpora = getCorporaOrNull(corporaId);
        assert corpora != null;
        corpora.setName(name);
        corpora.setDescription(description);
        corporaRepository.save(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a corpora.
     *
     * @param corperaId the id of the corpora to delete.
     * @return OK if the corpora is deleted.
     */
    public ResponseEntity<Void> delete(String corperaId) {
        LOGGER.debug("\nDeleting corpera: " + corperaId);
        Corpora corpora = getCorporaOrNull(corperaId);
        assert corpora != null;
        corporaRepository.delete(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get all corpora by a given user.
     *
     * @param principal the principal of the request.
     * @return a list of corpora (empty if nothing is found.)
     */
    public List getUserCorpora(Principal principal) {
        LOGGER.debug("Returning corpera for user: " + principal.getName());
        return em.createNativeQuery("SELECT [id], [created_by], [created_date], [description], [name], " +
                "[total_items] FROM [dbo].[corpora] WHERE [created_by] = ?1")
                .setParameter(1, principal.getName()).getResultList();
    }


    public Set<ItemOutDto> getCorporaItems(String id) {
        LOGGER.debug("Getting corpora items: " + id);

        List result = em.createNativeQuery("SELECT i.corpora_id, i.item_id, i.item_type,\n" +
                "\tCOALESCE (p.created_by, s.created_by, b.created_by) AS created_by,\n" +
                "\tCOALESCE (p.created_date, s.created_date, b.created_date) AS created_date,\n" +
                "\tCOALESCE (p.last_modified_by, s.last_modified_by, b.last_modified_by) AS last_modified_by,\n" +
                "\tCOALESCE (p.last_modified_date, s.last_modified_date, b.last_modified_date) AS last_modified_date,\n" +
                "\tCOALESCE (p.category, s.category, b.category) AS category,\n" +
                "\tCOALESCE (p.description, s.description, b.description) AS description,\n" +
                "\tCOALESCE (p.period, s.period, b.period) AS period,\n" +
                "\tCOALESCE (p.publication_stmt, s.publication_stmt, b.publication_stmt) AS publication_stmt,\n" +
                "\tCOALESCE (p.publication_year, s.publication_year, b.publication_year) AS publication_year,\n" +
                "\tCOALESCE (p.source_desc, s.source_desc, b.source_desc) AS source_desc,\n" +
                "\tCOALESCE (p.title, s.title, b.title) AS title,\n" +
                "\tCOALESCE (p.user_annotation, s.user_annotation, b.user_annotation) AS user_annotation,\n" +
                "\tCOALESCE (p.annotation, s.annotation) AS annotation,\n" +
                "\tCOALESCE (p.confirmed, s.confirmed) AS confirmed,\n" +
                "\tCOALESCE (p.confirmed_at, s.confirmed_at) AS confirmed_at,\n" +
                "\tCOALESCE (p.confirmed_by, s.confirmed_by) AS confirmed_by,\n" +
                "\tCOALESCE (p.pending_revision, s.pending_revision) AS pending_revision,\n" +
                "\ts.parent_id,\n" +
                "\tb.type,\n" +
                "\ts.text,\n" +
                "\tauthor.first_name,\n" +
                "\tauthor.last_name,\n" +
                "\tbook.title book_tit,\n" +
                "\tCASE WHEN i.item_type LIKE 'POEM' THEN\n" +
                "\t\tSUBSTRING(\n" +
                "\t\t\t(\n" +
                "\t\t\t\tSELECT ' ' + poem_text.text + '\\n' AS [text()]\n" +
                "\t\t\t\tFROM [dbo].[poem_text] poem_text\n" +
                "\t\t\t\tWHERE poem_text.poem_id = p.id\n" +
                "\t\t\t\tFOR XML PATH('')\n" +
                "\t\t\t), 2, 1000) END AS poem_text\n" +
                "FROM [dbo].[corpora_items] i \n" +
                "\tLEFT JOIN dbo.poem p ON (i.item_id = p.id AND i.item_type = 'POEM')\n" +
                "\tLEFT JOIN dbo.section s ON (i.item_id = s.id AND i.item_type = 'SECT')\n" +
                "\tLEFT JOIN dbo.book b ON (i.item_id = b.id AND i.item_type = 'BOOK')\n" +
                "\tLEFT JOIN dbo.author ON (p.author_id = author.id OR s.author_id = author.id OR b.author_id = author.id)\n" +
                "\tLEFT JOIN dbo.book ON (s.parent_id = book.id) " +
                "WHERE corpora_id = :corporaId", "itemMap")
                .setParameter("corporaId", id).getResultList();

        return new HashSet<>(result);
    }

    /**
     * Helper method that returns null if a corpora is not found.
     *
     * @param corporaId the id of the corpora to get.
     * @return the corpora or null
     */
    private Corpora getCorporaOrNull(final String corporaId) {
        long parsedId;
        try {
            parsedId = Long.parseLong(corporaId);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        Optional<Corpora> corporaOptional = corporaRepository.findById(parsedId);
        return corporaOptional.orElseThrow(NullPointerException::new);
    }
}
