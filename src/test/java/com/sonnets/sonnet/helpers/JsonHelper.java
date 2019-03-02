package com.sonnets.sonnet.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Helpers for converting objects to JSON and vice-versa.
 *
 * @author Josh Harkema
 */
public class JsonHelper {
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJsonResult(MvcResult result, Class<T> tClass) throws Exception {
        return mapper.readValue(result.getResponse().getContentAsString(), tClass);
    }

    public static byte[] toJson(Object object) throws Exception {
        return mapper.writeValueAsString(object).getBytes();
    }
}
