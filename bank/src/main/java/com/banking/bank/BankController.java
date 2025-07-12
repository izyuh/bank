package com.banking.bank;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BankController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> a) {
        String username = a.get("username");
        String password = a.get("password");

        boolean isAuthenticated = Bank.handleLogin(username, password);


        Map<String, Object> response = new HashMap<>();
        response.put("success", isAuthenticated);
        response.put("message", isAuthenticated ? "Login successful" : "Invalid username or password");
        response.put("username", username);
        response.put("balance", Bank.accounts.get(username) != null ? Bank.accounts.get(username).getBalance() : 0.0);
        return response;
    }
    // Add more endpoints for deposit, withdraw, login, etc.

    @PostMapping("/create-account")
    public Map<String, Object> createAccount(@RequestBody Map<String, String> accountDetails) {
        String username = accountDetails.get("username");
        String password = accountDetails.get("password");
        
        boolean isCreated = Bank.createAccount(username, password);
        Map<String, Object> response = new HashMap<>();
        response.put("success", isCreated);
        response.put("message", isCreated ? "Account created successfully" : "Username already exists");
        
        return response;
    }

    @PostMapping("/deposit")
    public Map<String, Object> deposit(@RequestBody Map<String, String> depositDetails) {
        String username = depositDetails.get("username");
        double amount = Double.parseDouble(depositDetails.get("amount"));

        boolean isDeposited = Bank.deposit(username,amount);

        Map<String, Object> response = new HashMap<>();

            response.put("success", isDeposited);
            response.put("message", "Deposit successful");
            response.put("balance", Bank.accounts.get(username).getBalance());

        return response;
    }

    @PostMapping("/withdraw")
    public Map<String, Object> withdraw(@RequestBody Map<String, String> withdrawDetails) {
        String username = withdrawDetails.get("username");
        double amount = Double.parseDouble(withdrawDetails.get("amount"));

        boolean isWithdrawn = Bank.withdraw(username, amount);

        Map<String, Object> response = new HashMap<>();

            response.put("success", isWithdrawn);
            response.put("message", isWithdrawn ? "Withdrawal successful" : "Insufficient funds or invalid amount");
            response.put("balance", Bank.accounts.get(username) != null ? Bank.accounts.get(username).getBalance() : 0.0);

        return response;
        
    }

    
}