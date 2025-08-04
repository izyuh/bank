package com.banking.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.banking.bank.repository.AccountRepository;
import java.util.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@CrossOrigin(origins = "https://kaleidoscopic-starship-602186.netlify.app")
@RestController
@RequestMapping("/api")
public class BankController {

    @Autowired
    private AccountRepository accountRepository;

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
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
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
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("username", username);
                response.put("balance", account.getBalance());
                response.put("accountNumber", account.getAccountNumber());
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
    public Map<String, Object> deposit(@RequestBody Map<String, String> depositDetails) {
        String username = depositDetails.get("username");
        double amount = Double.parseDouble(depositDetails.get("amount"));

        Map<String, Object> response = new HashMap<>();

        Optional<Account> accountOpt = accountRepository.findByUsername(username);
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
    public Map<String, Object> withdraw(@RequestBody Map<String, String> withdrawDetails) {
        String username = withdrawDetails.get("username");
        double amount = Double.parseDouble(withdrawDetails.get("amount"));

        Map<String, Object> response = new HashMap<>();

        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            try {
                account.withdrawBalance(amount);
                accountRepository.save(account);

                response.put("success", true);
                response.put("message", "Withdrawal successful");
                response.put("balance", account.getBalance());
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                response.put("balance", account.getBalance());
            }
        } else {
            response.put("success", false);
            response.put("message", "Account not found");
        }

        return response;
    }

    @PostMapping("/transfer")
    public Map<String, Object> transfer(@RequestBody Map<String, String> transferDetails) {
        String fromAccountNumber = transferDetails.get("fromAccountNum");
        String toAccountNumber = transferDetails.get("toAccountNum");
        double amount = Double.parseDouble(transferDetails.get("amount"));

        Map<String, Object> response = new HashMap<>();

        Optional<Account> fromAccountOpt = accountRepository.findByAccountNumber(fromAccountNumber);
        Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(toAccountNumber);

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
                response.put("balance", fromAccount.getBalance());
            }
        } else {
            response.put("success", false);
            response.put("message", "One or both accounts not found");
        }

        return response;
    }
}