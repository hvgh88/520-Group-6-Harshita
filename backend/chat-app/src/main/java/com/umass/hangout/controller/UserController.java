package com.umass.hangout.controller;

import com.umass.hangout.entity.User;
import com.umass.hangout.entity.Group;
import com.umass.hangout.exception.UserNotFoundException;
import com.umass.hangout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<Group>> getUserGroups(@PathVariable Long userId) {
        List<Group> groups = userService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/group-ids/{userId}")
    public ResponseEntity<Set<Long>> getUserGroupIds(@PathVariable Long userId) {
        Set<Long> groupIds = userService.getUserGroupIds(userId);
        return ResponseEntity.ok(groupIds);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // Global exception handler for handling custom exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}