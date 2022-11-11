package com.felipe.jwtAuthTest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/private")
    public ResponseEntity<String> privateEndpoint() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
