package com.sonnets.sonnet.config;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

/**
 * Taken verbatim from http://scrumbucket.org/sanitize-your-spring-mvc-controller/
 * Cleans all form input to prevent SQL injection.
 *
 * @author Steve Sando
 */
@ControllerAdvice
public class CleanStringAdvice {
    @InitBinder
    public void bindStringCleaner(WebDataBinder webDataBinder) {
        StringCleaner stringCleaner = new StringCleaner();
        webDataBinder.registerCustomEditor(String.class, stringCleaner);
    }

    static public class StringCleaner extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            if (text == null) {
                setValue(null);
            } else {
                String safe = Jsoup.clean(text, Whitelist.simpleText());
                setValue(safe);
            }
        }

    }

}

