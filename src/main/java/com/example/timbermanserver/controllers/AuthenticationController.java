package com.example.timbermanserver.controllers;

import com.example.timbermanserver.entities.User;
import com.example.timbermanserver.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationForm(@ModelAttribute(name = "user") User user) {
        return "registration";
    }

    @PostMapping("/registration")
    public View registerUser(@ModelAttribute(name = "user") User user) {
        userService.registerUser(user);
        logger.info("User have been successfully created, Entity: " + user.toString());
        return new RedirectView("/login");
    }

    @GetMapping("/activateUser")
    public View activateUser(
            @RequestParam(name = "u", required = true) String username,
            @RequestParam(name = "c", required = true) String code
    ) {
        if (userService.activateUser(code, username)) {
            logger.info("User have been successfully activated, username: " + username);
        } else {
            logger.warn("User tried to activate account with invalid activation code, username: " + username);
        }
        return new RedirectView("/login");
    }

}
