package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.converters.SonnetConverter;
import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDto;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This controller handles all the search functionality, the xml conversion functionality, and the csv conversion
 * functionality. This should probably be in more than one class.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SameReturnValue")
@Controller
public class LookupController {
    private final SonnetDetailsService sonnetDetailsService;
    private final SearchService searchService;
    private static final Logger logger = Logger.getLogger(LookupController.class);

    private static final String LOOKUP = "lookup";
    private static final String SONNET = "Sonnet";
    private static final String EDIT = "edit";

    @Autowired
    public LookupController(SonnetDetailsService sonnetDetailsService, SearchService searchService) {
        this.sonnetDetailsService = sonnetDetailsService;
        this.searchService = searchService;
    }

    @GetMapping("/lookup")
    public String showSearchPage(Model model) {
        model.addAttribute(SONNET, new Sonnet());
        return LOOKUP;
    }

    @PostMapping("/lookup")
    public String search(@ModelAttribute Sonnet sonnet, Model model) {
        List sonnets = null;
        logger.debug(sonnet.toString());

        try {
            sonnets = searchService.search(sonnet);
        } catch (Exception e) {
            logger.error(e);
        }

        model.addAttribute(SONNET, new Sonnet());
        model.addAttribute("sonnets", sonnets);
        return LOOKUP;
    }

    @GetMapping("/lookup/edit/{id}")
    public String editSonnet(@PathVariable("id") String id, Model model) {
        logger.debug("Editing sonnet: " + id);
        Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);
        SonnetDto sonnetDto = new SonnetDto(sonnet);
        model.addAttribute(SONNET, sonnetDto);

        return EDIT;
    }

    @PostMapping("/lookup/edit/{id}")
    public String postEditSonnet(@ModelAttribute SonnetDto sonnet, Model model) {
        logger.debug("Posting new sonnet details for id: " + sonnet.getId());
        Sonnet newSonnet = sonnetDetailsService.updateSonnet(sonnet);
        sonnet = new SonnetDto(newSonnet);
        model.addAttribute(SONNET, sonnet);

        return EDIT;
    }

    @GetMapping("/lookup/delete/{id}")
    public String deleteSonnet(@PathVariable("id") String id, Model model) {
        sonnetDetailsService.deleteSonnet(id);
        model.addAttribute("Sonnet", new Sonnet());

        return LOOKUP;
    }

    @GetMapping(value = "/lookup/xml/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public void getXML(@PathVariable("id") String id, HttpServletResponse response) {
        logger.debug("Parsing sonnet to XML: " + id);

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

    @GetMapping(value = "/lookup/tei/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public void getTEI(@PathVariable("id") String id, HttpServletResponse response) {
        logger.debug("Parsing sonnet to TEI: " + id);
        try {
            Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);
            String sonnetTEI = SonnetConverter.sonnetToTEI(sonnet);

            InputStream inputStream = new ByteArrayInputStream(sonnetTEI.getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @GetMapping(value = "/lookup/csv/get_csv/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public void getCSV(@PathVariable("ids") String[] ids, HttpServletResponse response) {
        logger.debug(ids);

        if (logger.isDebugEnabled()) {
            logger.debug("Parsing sonnet ids: " + Arrays.toString(ids));
        }

        try {
            List<Sonnet> sonnets = new ArrayList<>();
            for (String s : ids) {
                sonnets.add(sonnetDetailsService.getSonnetByID(s));
            }

            String sonnetCSV = SonnetConverter.sonnetsToCSV(sonnets);

            InputStream inputStream = new ByteArrayInputStream(sonnetCSV.getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @GetMapping("/lookup/csv")
    public String showSelectionPage(Model model) {
        model.addAttribute("sonnets", sonnetDetailsService.getAllSonnets());
        return "select";
    }
}
