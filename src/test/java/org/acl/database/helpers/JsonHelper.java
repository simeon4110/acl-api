package org.acl.database.helpers;

import com.google.gson.Gson;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Helpers for converting objects to JSON and vice-versa.
 *
 * @author Josh Harkema
 */
public class JsonHelper {
    private static Gson gson = new Gson();

    public static <T> T fromJsonResult(MvcResult result, Class<T> tClass) throws Exception {
        return gson.fromJson(result.getResponse().getContentAsString(), tClass);
    }

    public static byte[] toJson(Object object) {
        return gson.toJson(object).getBytes();
    }
}
