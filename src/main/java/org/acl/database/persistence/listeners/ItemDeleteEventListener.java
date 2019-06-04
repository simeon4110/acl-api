package org.acl.database.persistence.listeners;

import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Book;
import org.acl.database.persistence.models.base.Other;
import org.acl.database.persistence.models.base.Poem;
import org.acl.database.persistence.models.base.Section;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for Item delete events. Currently handles removal of orphans from the corpora_items table
 * as @ManyToAny annotations do not handle deletion of child objects when a parent is removed.
 *
 * @author Josh Harkema
 */
@Component
public class ItemDeleteEventListener implements PreDeleteEventListener {
    public static final ItemDeleteEventListener INSTANCE = new ItemDeleteEventListener();
    private static final String QUERY = "DELETE FROM corpora_items " +
            "WHERE item_type = :itemType " +
            "AND item_id = :itemId";

    /**
     * Defines extra deletion SQL for when a Book, Poem, Section, or Other object is
     * deleted.
     *
     * @param event the database event.
     * @return always false (a quirk of the abstract interface.)
     */
    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        final Object entity = event.getEntity();
        if (entity instanceof Book) {
            event.getSession().createNativeQuery(QUERY)
                    .setParameter("itemType", TypeConstants.BOOK)
                    .setParameter("itemId", ((Book) entity).getId())
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
        }
        if (entity instanceof Poem) {
            event.getSession().createNativeQuery(QUERY)
                    .setParameter("itemType", TypeConstants.POEM)
                    .setParameter("itemId", ((Poem) entity).getId())
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
        }
        if (entity instanceof Section) {
            event.getSession().createNativeQuery(QUERY)
                    .setParameter("itemType", TypeConstants.SECTION)
                    .setParameter("itemId", ((Section) entity).getId())
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
        }
        if (entity instanceof Other) {
            event.getSession().createNativeQuery(QUERY)
                    .setParameter("itemType", TypeConstants.OTHER)
                    .setParameter("itemId", ((Other) entity).getId())
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
        }
        return false;
    }
}
