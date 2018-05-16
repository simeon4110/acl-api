package com.sonnets.sonnet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Josh Harkema
 */
@Controller
public class FrontPageController {
    @GetMapping("/")
    public String showFrontPage() {
        return "index";
    }
}
