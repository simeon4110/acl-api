package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import com.sonnets.sonnet.persistence.dtos.base.ItemOutSimpleDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface to define store procedure methods.
 *
 * @author Josh Harkema
 */
public interface CorporaRepositoryStoredProcedures {
    int countCorporaItems(final Long corporaId);

    void setCorporaItemsCount(final Long corporaId, final int count);

    void addCorporaItem(final Long corporaId, final Long itemId, final String itemType);

    void removeCorporaItem(final Long corporaId, final Long itemId, final String itemType);

    Optional<Corpora> getCorpora(final Long corporaId);

    Optional<Set<ItemOutDto>> getCorporaItems(final Long corporaId);

    Optional<Set<ItemOutSimpleDto>> getCorporaItemsSimple(final Long corporaId);

    Optional<List> getCorporaUser(final String createdBy);
}
