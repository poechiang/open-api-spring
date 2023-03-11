package me.jeffrey.open.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String index(ModelMap map) {

        Date d = new Date();

        map.addAttribute("title", "Welcome");
        map.addAttribute("author", "Jeffrey");
        map.addAttribute("date",d);
        return "welcome";
    }
}
