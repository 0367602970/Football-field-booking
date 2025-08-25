package vti.group10.football_booking.controller;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import vti.group10.football_booking.model.User;
import vti.group10.football_booking.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String getMethodName() {
        return "hello";
    }
    

    @PostMapping("api/users")
    public User createUser(@RequestBody User user) {
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        return this.userService.handleCreateUser(user);
    }
    
}
