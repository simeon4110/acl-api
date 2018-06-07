package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.Pager;
import com.sonnets.sonnet.tools.SonnetConverter;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

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

    private static final String PAGE_TITLE = "pageTitle";
    private static final String LOOKUP = "lookup";
    private static final String SONNET = "Sonnet";
    private static final String PAGER = "pager";
    private static final String PAGE = "page";
    private static final int BUTTONS_TO_SHOW = 5;

    @Autowired
    public LookupController(SonnetDetailsService sonnetDetailsService, SearchService searchService) {
        this.sonnetDetailsService = sonnetDetailsService;
        this.searchService = searchService;
    }

    /**
     * This is terrible, but I don't want to screw with passing POST requests around. Deal with it.
     *
     * @param sonnet      the sonnet object containing lookup params.
     * @param model       the model to get/add the sonnets to.
     * @param pageRequest the pageable request data.
     * @return a ModelAndView of the search form and search results.
     */
    @GetMapping(value = "/lookup", name = LOOKUP)
    public ModelAndView showSearchPage(@ModelAttribute Sonnet sonnet, @ModelAttribute ModelMap model,
                                       Pageable pageRequest) {
        Page<Sonnet> sonnets;
        Pager pager;

        try {
            sonnets = searchService.search(sonnet, pageRequest);
            // One is subtracted to fix strange offset issues.
            pager = new Pager(sonnets.getTotalPages() - 1, pageRequest.getPageNumber(), BUTTONS_TO_SHOW);
            model.addAttribute(PAGER, pager);
            model.addAttribute(PAGE, sonnets);
            model.addAttribute(PAGE_TITLE, "Search");
        } catch (NullPointerException e) {
            logger.error(e);
        }

        model.addAttribute(SONNET, sonnet);
        logger.debug("Returning search page: " + model.toString());

        return new ModelAndView(LOOKUP, model);
    }

    /**
     * Returns a sonnet in XML format.
     *
     * @param id       the id of the sonnet to return.
     * @param response the response to attach the data to.
     */
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

    /**
     * Returns a sonnet in TEI format.
     *
     * @param id       the id of the sonnet to return.
     * @param response the response to attach the data to.
     */
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

    /**
     * Takes multiple sonnets and parses them into a list of CSV values.
     *
     * @param ids      the sonnet ids to parse.
     * @param response the response to attach the data to.
     */
    @GetMapping(value = "/lookup/csv/get_csv/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public void getCSV(@PathVariable("ids") String[] ids, HttpServletResponse response) {
        logger.debug("Parsing to csv: " + ids);

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

    @GetMapping(value = "/lookup/txt/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public void getTXT(@PathVariable("ids") String[] ids, HttpServletResponse response) {
        logger.debug("Parsing to txt: " + ids);

        try {
            List<Sonnet> sonnets = new ArrayList<>();
            for (String s : ids) {
                sonnets.add(sonnetDetailsService.getSonnetByID(s));
            }

            String sonnetTXT = SonnetConverter.sonnetsToText(sonnets);

            InputStream inputStream = new ByteArrayInputStream(sonnetTXT.getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Shows the selection page for the multi selector.
     *
     * @param model the model to attach all the sonnet objects to.
     * @return the multi selector page.
     */
    @GetMapping("/lookup/csv")
    public String showSelectionPage(Model model) {
        model.addAttribute("sonnets", sonnetDetailsService.getAllSonnets());
        model.addAttribute(PAGE_TITLE, "Manual Selection");
        return "select";
    }
}
