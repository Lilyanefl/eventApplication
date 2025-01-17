package com.eventi.eventiApplication.controller;

import com.eventi.eventiApplication.model.UserDB;
import com.eventi.eventiApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDB> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{username}")
    public Optional<UserDB> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
}
