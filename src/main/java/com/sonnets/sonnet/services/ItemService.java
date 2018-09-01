package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.ItemDto;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullException;
import com.sonnets.sonnet.tools.ItemKeyValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a generic service for getting lists of all objects of the Item class.
 *
 * @author Josh Harkema
 */
@Service
public class ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class);
    private final GetObjectOrThrowNullException getObjectOrNull;

    @Autowired
    public ItemService(GetObjectOrThrowNullException getObjectOrNull) {
        this.getObjectOrNull = getObjectOrNull;
    }

    /**
     * Returns a list of mixed sub-classes of the Item class.
     *
     * @param dto the items to get.
     * @return a list of items; list is empty if nothing is found.
     */
    @SuppressWarnings("unchecked")
    public List getItems(ItemDto dto) {
        LOGGER.debug("Returning items: " + dto.getPairs());
        List items = new ArrayList();
        for (ItemKeyValuePair<String, String> pair : dto.getPairs()) {
            switch (pair.getKey()) {
                case "POEM":
                    items.add(getObjectOrNull.poem(pair.getValue()));
                    break;
                case "BOOK":
                    items.add(getObjectOrNull.book(pair.getValue()));
                    break;
                case "SECT":
                    items.add(getObjectOrNull.section(pair.getValue()));
                    break;
                default:
                    break;
            }
        }
        return items;
    }
}
