package com.example.timbermanserver.controllers;

import com.example.timbermanserver.entities.User;
import com.example.timbermanserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registrationForm(@ModelAttribute(name = "user") User user) {


        return "registration";
    }

    @PostMapping("/registration")
    public View registerUser(@ModelAttribute(name = "user") User user) {

        userService.registerUser(user);
        return new RedirectView("/login");
    }

    @GetMapping("/activateUser")
    public View activateUser(
            @RequestParam(name = "u", required = true) String username,
            @RequestParam(name = "c", required = true) String code
    ) {
        userService.activateUser(code, username);
        return new RedirectView("/login");
    }

}
