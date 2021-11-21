package com.example.timbermanserver;

import com.example.timbermanserver.entities.Role;
import com.example.timbermanserver.entities.User;
import com.example.timbermanserver.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public List<User> all() {

        return userRepository.findAll();

    }

    @GetMapping("/new")
    public List<User> neW() {

        userRepository.save(new User(
                "test",
                "test",
                passwordEncoder.encode("test"),
                true,
                "test",
                Collections.singleton(Role.ADMIN)
        ));

        return userRepository.findAll();

    }

    @GetMapping("/clear")
    public List<User> gamePage() {

        userRepository.deleteAll();

        return userRepository.findAll();

    }

    @GetMapping(value = "/localization", produces = "application/json")
    public @ResponseBody Object getLocalization(@RequestParam(name = "code", required = false) String code) throws IOException {
        try {
            System.out.println();
            return IOUtils.toString(getClass().getResource(
                    "/i18n/localization" + (code == null ? "" : "_" + code) + ".json"
            ), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return IOUtils.toString(getClass().getResource("/i18n/localization.json"), StandardCharsets.UTF_8);
        }
    }

}
