package com.jaha.web.emaul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public @ResponseBody String healthCheck() {

        // try {
        // Thread.sleep(10000);
        // } catch (InterruptedException e) {
        // logger.error("{}", e);
        // }

        return "ok";
    }
}
