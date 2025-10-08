package com.banking.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.banking.bank.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://izyuh.netlify.app"},
             allowCredentials = "true")
public class BankController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private static String hashPassword(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password;
            byte[] hashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        // Get account by username only
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();

            // Hash the input password with the stored salt
            String hashedInputPassword = hashPassword(account.getSalt(), password);

            // Compare hashed passwords
            if (hashedInputPassword.equals(account.getHashedPassword())) {
                // Login successful

                System.out.println("Login successful for user: " + username);
                System.out.println("Session ID: " + session.getId());
                System.out.println(account.getAccountNumber());

                session.setAttribute("username", username);
                session.setAttribute("accountNumber", account.getAccountNumber());


                response.put("success", true);
                response.put("message", "Login successful");
            } else {
                // Wrong password
                response.put("success", false);
                response.put("message", "Invalid username or password");
            }
        } else {
            // Username doesn't exist - but don't reveal this!
            response.put("success", false);
            response.put("message", "Invalid username or password");
        }

        return response;
    }

    @PostMapping("/create-account")
    public Map<String, Object> createAccount(@RequestBody Map<String, String> accountDetails) {
        String username = accountDetails.get("username");
        String password = accountDetails.get("password");

        System.out.println(username + " " + password);

        Map<String, Object> response = new HashMap<>();

        // Check if username already exists
        if (accountRepository.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return response;
        }

        // Generate account number
        String accountNumber = "ACC" + String.format("%06d", (int) (Math.random() * 1000000));

        // Hash password
        String salt = generateSalt();
        String hashedPassword = hashPassword(salt, password);

        System.out.println(hashedPassword + " " + salt);

        // Create new account with hashed password
        Account newAccount = new Account(username, hashedPassword, salt, accountNumber, 500.0);

        System.out.println(newAccount);

        accountRepository.save(newAccount);

        response.put("success", true);
        response.put("message", "Account created successfully");

        return response;
    }

    @PostMapping("/deposit")
    public Map<String, Object> deposit(@RequestBody Map<String, String> depositDetails, HttpSession session) {
        double amount = Double.parseDouble(depositDetails.get("amount"));

        Map<String, Object> response = new HashMap<>();

        Optional<Account> accountOpt = accountService.getAccountFromSession(session);

        if (accountOpt.isPresent() && amount > 0) {
            Account account = accountOpt.get();
            account.addBalance(amount);
            accountRepository.save(account);

            response.put("success", true);
            response.put("message", "Deposit successful");
            response.put("balance", account.getBalance());
        } else {
            response.put("success", false);
            response.put("message", "Invalid account or amount");
        }

        return response;
    }

    @PostMapping("/withdraw")
    public Map<String, Object> withdraw(@RequestBody Map<String, String> withdrawDetails, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Optional<Account> accountOpt = accountService.getAccountFromSession(session); // pass session here

        

        if (!accountOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "User not logged in or account not found");
            return response;
        }

        Account account = accountOpt.get();
        double amount = Double.parseDouble(withdrawDetails.get("amount"));
        try {
            account.withdrawBalance(amount);
            accountRepository.save(account);
            response.put("success", true);
            response.put("message", "Withdrawal successful");
            response.put("balance", account.getBalance());
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/transfer")
    public Map<String, Object> transfer(@RequestBody Map<String, String> transferDetails, HttpSession session) {
        double amount = Double.parseDouble(transferDetails.get("amount"));

        Map<String, Object> response = new HashMap<>();
        Optional<Account> fromAccountOpt = accountService.getAccountFromSession(session);
        Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(transferDetails.get("toAccountNumber"));

        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();

            try {
                fromAccount.withdrawBalance(amount);
                toAccount.addBalance(amount);

                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);

                response.put("success", true);
                response.put("message", "Transfer successful");
                response.put("balance", fromAccount.getBalance());
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
            }
        } else {
            response.put("success", false);
            response.put("message", "One or both accounts not found");
        }

        return response;
    }

    @GetMapping("/account")
    public Map<String, Object> getAccount(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Optional<Account> accountOpt = accountService.getAccountFromSession(session);
        
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            response.put("success", true);
            response.put("username", account.getUsername());
            response.put("balance", account.getBalance());
            response.put("accountNumber", account.getAccountNumber());
        } else {
            response.put("success", false);
            response.put("message", "No account found in session");
        }
        
        return response;
    }

        @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}

