package com.example.timbermanserver.services;

import com.example.timbermanserver.entities.Role;
import com.example.timbermanserver.entities.User;
import com.example.timbermanserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return user;
    }

    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findUserByUsername(email);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return user;
    }

    public void registerUser(User user) {

        if (
                userRepository.findUserByUsername(user.getUsername()) == null
                        && userRepository.findUserByEmail(user.getEmail()) == null
        ) {

            user.setActivationCode(UUID.randomUUID().toString());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(false);
            user.setRoles(Collections.singleton(Role.USER));
            mailService.sendActivationCodeToUser(user);
            userRepository.save(user);
        }
    }

    public boolean activateUser(String activationCode, String username) {

        User user = userRepository.findUserByUsername(username);

        if (user != null && user.getActivationCode().equals(activationCode)) {
            user.setActive(true);
            userRepository.save(user);
            return true;
        }

        return false;

    }

}
