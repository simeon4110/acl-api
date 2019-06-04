package org.acl.database.persistence.dtos.base;

import org.acl.database.helpers.ItemKeyValuePair;

import java.util.List;

public class ItemDto {
    private List<ItemKeyValuePair<String, String>> pairs;

    public List<ItemKeyValuePair<String, String>> getPairs() {
        return pairs;
    }

    public void setPairs(List<ItemKeyValuePair<String, String>> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "pairs=" + pairs +
                '}';
    }
}
