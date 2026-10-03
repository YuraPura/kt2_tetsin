package com.kt.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/users")
public class UsersApplication {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, "--server.port=8081");
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = users.get(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}

class User {
    private Long id; private String name; private String email;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
