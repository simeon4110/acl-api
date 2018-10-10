package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.models.web.Corpora;

import java.util.List;
import java.util.Optional;

/**
 * Interface to define store procedure methods.
 *
 * @author Josh Harkema
 */
public interface CorporaRepositoryStoredProcedures {

    void addCorporaItem(final Long corporaId, final Long itemId, final String itemType);

    void removeCorporaItem(final Long corporaId, final Long itemId, final String itemType);

    Optional<Corpora> getCorpora(final Long corporaId);

    String getCorporaItems(final Long corporaId);

    String getCorporaItemsSimple(final Long corporaId);

    Optional<List> getCorporaUser(final String createdBy);
}
