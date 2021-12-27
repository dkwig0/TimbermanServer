package com.example.timbermanserver.controllers;

import com.example.timbermanserver.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthenticationController {



    @GetMapping("/registration")
    public String registrationForm(@ModelAttribute(name = "user") User user) {



        return "registration";
    }

    @PostMapping("/registration")
    public View registerUser(User user) {

        return new RedirectView("/login");
    }

}
