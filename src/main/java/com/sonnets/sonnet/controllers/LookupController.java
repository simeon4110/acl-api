package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.converters.SonnetConverter;
import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Josh Harkema
 */
@Controller
public class LookupController {
    private final SonnetDetailsService sonnetDetailsService;
    private static final Logger logger = Logger.getLogger(LookupController.class);

    @Autowired
    public LookupController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @SuppressWarnings("SameReturnValue")
    @GetMapping("/lookup")
    public String showLookupPage(Model model) {
        model.addAttribute("sonnets", sonnetDetailsService.getAllSonnets());
        return "lookup";
    }

    @GetMapping("/lookup/xml/{id}")
    public void getXML(@PathVariable("id") String id, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("Parsing sonnet: " + "'" + id + "'" + " to xml.");
        }

        try {
            Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);
            String sonnetXML = SonnetConverter.sonnetToXML(sonnet);

            InputStream inputStream = new ByteArrayInputStream(sonnetXML.getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
