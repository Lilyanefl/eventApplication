package com.eventi.eventiApplication.controller;
import com.eventi.eventiApplication.model.UserDB;
import com.eventi.eventiApplication.security.JwtUtil;
import com.eventi.eventiApplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody UserDB user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        UserDB dbUser = userService.getUserByUsername(user.getUsername()).get();
        return jwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole().name());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDB user) {
        try {
            if (user.getRole() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Errore: Il ruolo deve essere USER o ORGANIZER");
            }

            UserDB newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore: username già esistente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la registrazione, ricontrollare username e password");
        }
    }

}