package com.eventi.eventiApplication.controller;

import com.eventi.eventiApplication.model.UserDB;
import com.eventi.eventiApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDB>> getAllUsers() {
        List<UserDB> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDB> getUserByUsername(@PathVariable String username) {
        UserDB user = userService.getUserByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Utente non trovato"));
        return ResponseEntity.ok(user);
    }
}
