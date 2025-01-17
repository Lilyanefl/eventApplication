package com.eventi.eventiApplication.service;

import com.eventi.eventiApplication.model.UserDB;
import com.eventi.eventiApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDB createUser(UserDB user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public List<UserDB> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserDB> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
