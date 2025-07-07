package com.banking.bank;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BankController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        boolean isAuthenticated = Bank.handleLogin(username, password);

        Map<String, Object> response = new HashMap<>();
        response.put("success", isAuthenticated);
        response.put("message", isAuthenticated ? "Login successful" : "Invalid username or password");
        return response;
    }
    // Add more endpoints for deposit, withdraw, login, etc.
}