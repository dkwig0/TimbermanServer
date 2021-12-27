package com.example.timbermanserver.services;

import com.example.timbermanserver.entities.Role;
import com.example.timbermanserver.entities.User;
import com.example.timbermanserver.repositories.UserRepository;
import com.example.timbermanserver.security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(s);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return user;

    }

    public void registerUser(String username, String password, String email) {
        String activationCode = String.format("%08x", new Random().nextInt());
        userRepository.save(new User(
                username,
                email,
                passwordEncoder.encode(password),
                false,
                activationCode,
                Collections.singleton(Role.USER)
        ));
    }

    public void activateUser(String activationCode, String username)

}
