package com.sonnets.sonnet.persistence.dtos.web;

import com.sonnets.sonnet.helpers.ItemKeyValuePair;

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
    private List<ItemKeyValuePair<String, String>> ids;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemKeyValuePair<String, String>> getIds() {
        return ids;
    }

    public void setIds(List<ItemKeyValuePair<String, String>> ids) {
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
