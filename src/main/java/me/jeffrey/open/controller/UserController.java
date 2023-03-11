package me.jeffrey.open.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @RequestMapping
    public String getUsers(){
        return "all users is here ...";
    }
}
