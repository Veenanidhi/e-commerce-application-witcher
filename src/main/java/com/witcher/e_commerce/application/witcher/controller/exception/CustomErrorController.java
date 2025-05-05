package com.witcher.e_commerce.application.witcher.controller.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error"; // Custom error path

    public String getErrorPath() {
        return PATH;
    }

    @RequestMapping(PATH)
    public String handleError() {
        // Custom error handling logic
        return "404-page";
    }




}
