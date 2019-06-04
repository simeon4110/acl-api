package org.acl.database.persistence.dtos.web;

import org.acl.database.helpers.ItemKeyValuePair;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for dealing with Item sub classes and the corpora.
 *
 * @author Josh Harkema
 */
public class CorporaItemsDto {
    @NotNull
    private Long id;
    private List<ItemKeyValuePair<String, Long>> ids;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemKeyValuePair<String, Long>> getIds() {
        return ids;
    }

    public void setIds(List<ItemKeyValuePair<String, Long>> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "CorporaItemsDto{" +
                "id=" + id +
                ", ids=" + ids +
                '}';
    }
}
