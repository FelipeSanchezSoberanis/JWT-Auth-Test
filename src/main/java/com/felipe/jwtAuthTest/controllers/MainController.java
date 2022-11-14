package com.felipe.jwtAuthTest.controllers;

import com.felipe.jwtAuthTest.config.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return new ResponseEntity<>(passwordEncoder.encode("password"), HttpStatus.OK);
    }

    @GetMapping("/private")
    public ResponseEntity<UserDetailsImpl> privateEndpoint() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}
