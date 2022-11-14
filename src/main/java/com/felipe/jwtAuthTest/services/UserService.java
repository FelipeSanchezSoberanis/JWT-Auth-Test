package com.felipe.jwtAuthTest.services;

import com.felipe.jwtAuthTest.entities.User;
import com.felipe.jwtAuthTest.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
