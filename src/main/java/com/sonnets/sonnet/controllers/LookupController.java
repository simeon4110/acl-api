package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.converters.Pager;
import com.sonnets.sonnet.converters.SonnetConverter;
import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDto;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
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
import org.springframework.web.bind.annotation.PostMapping;
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

    // Define constants
    private static final String LOOKUP = "lookup";
    private static final String SONNET = "Sonnet";
    private static final String EDIT = "edit";
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
    public ModelAndView showSearchPage(@ModelAttribute Sonnet sonnet, @ModelAttribute ModelMap model, Pageable pageRequest) {
        Page<Sonnet> sonnets = null;
        Pager pager = null;

        try {
            sonnets = searchService.search(sonnet, pageRequest);
            pager = new Pager(sonnets.getTotalPages(), pageRequest.getPageNumber(), BUTTONS_TO_SHOW);
        } catch (NullPointerException e) {
            logger.error(e);
        }

        model.addAttribute(SONNET, sonnet);
        model.addAttribute(PAGER, pager);
        model.addAttribute(PAGE, sonnets);
        logger.debug("Returning search page: " + model.toString());

        return new ModelAndView(LOOKUP, model);
    }

    /**
     * Edit a sonnet by ID.
     *
     * @param id    the id of the sonnet to edit.
     * @param model the model with/out the sonnet object.
     * @return a html page with the sonnet data populated for editing.
     */
    @GetMapping("/lookup/edit/{id}")
    public String editSonnet(@PathVariable("id") String id, Model model) {
        logger.debug("Editing sonnet: " + id);
        Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);
        SonnetDto sonnetDto = new SonnetDto(sonnet);
        model.addAttribute(SONNET, sonnetDto);

        return EDIT;
    }

    /**
     * Parse the new edited data.
     * @param sonnet the sonnet's new data.
     * @param model the model with/out the sonnet object.
     * @return an html page with the NEW sonnet data populated for editing.
     */
    @PostMapping("/lookup/edit/{id}")
    public String postEditSonnet(@ModelAttribute SonnetDto sonnet, Model model) {
        logger.debug("Posting new sonnet details for id: " + sonnet.getId());
        Sonnet newSonnet = sonnetDetailsService.updateSonnet(sonnet);
        sonnet = new SonnetDto(newSonnet);
        model.addAttribute(SONNET, sonnet);

        return EDIT;
    }

    /**
     * Returns a sonnet in XML format.
     * @param id the id of the sonnet to return.
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
     * @param id the id of the sonnet to return.
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
     * @param ids the sonnet ids to parse.
     * @param response the response to attach the data to.
     */
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

    /**
     * Shows the selection page for the multi selector.
     * @param model the model to attach all the sonnet objects to.
     * @return the multi selector page.
     */
    @GetMapping("/lookup/csv")
    public String showSelectionPage(Model model) {
        model.addAttribute("sonnets", sonnetDetailsService.getAllSonnets());
        return "select";
    }
}
