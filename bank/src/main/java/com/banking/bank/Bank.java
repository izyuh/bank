package com.banking.bank;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Random;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Bank {

    static Map<String, Account> accounts = new HashMap<>();

    public static void main(String[] args) {
        loadAccounts();
        SpringApplication.run(Bank.class, args);
    }

    // Logic for login, now returns boolean
    public static boolean handleLogin(String username, String password) {
        if (accounts.containsKey(username)) {
            Account account = accounts.get(username);
            String hashedPassword = hashPassword(password, account.getSalt());
            return account.getHashedPassword().equals(hashedPassword);
        }
        return false;
    }

    // Logic for account creation
    public static boolean createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            return false; // Username already exists
        }
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);


        int accountNum = generateAccountNumbers();

        while(accountNumberExists(accountNum)) {
            accountNum = generateAccountNumbers();
        }

        accounts.put(username, new Account(hashedPassword, salt, 500.0, accountNum));
        saveAccountsToFile();
        return true;
    }

    // Logic for deposit
    public static boolean deposit(String username, double amount) {
        if (accounts.containsKey(username) && amount > 0) {
            accounts.get(username).addBalance(amount);
            System.out.println(accounts.get(username).getBalance());
            saveAccountsToFile();
            return true;
        }
        return false;
    }

    // Logic for withdraw
    public static boolean withdraw(String username, double amount) {
        if (accounts.containsKey(username) && amount > 0 && accounts.get(username).getBalance() >= amount) {
            accounts.get(username).withdrawBalance(amount);
            saveAccountsToFile();
            return true;
        }
        return false;
    }

    public static boolean transfer(int fromAccountNum, int toAccountNum, double amount) {
        if (amount <= 0) {
            System.out.println("Transfer failed: Invalid amount.");
            return false;
        }
        
        Account fromAccount = null;
        Account toAccount = null;
        
        // Find accounts by account number
        for (Account account : accounts.values()) {
            if (account.getAccountNum() == fromAccountNum) {
                fromAccount = account;
            }
            if (account.getAccountNum() == toAccountNum) {
                toAccount = account;
            }
        }
        
        // Check if both accounts exist
        if (fromAccount == null) {
            System.out.println("Transfer failed: Source account not found.");
            return false;
        }
        if (toAccount == null) {
            System.out.println("Transfer failed: Destination account not found.");
            return false;
        }
        
        // Check if source account has sufficient funds
        if (fromAccount.getBalance() < amount) {
            System.out.println("Transfer failed: Insufficient funds.");
            return false;
        }
        
        // Perform the transfer
        fromAccount.withdrawBalance(amount);
        toAccount.addBalance(amount);
        saveAccountsToFile();
        
        System.out.println("Transfer successful: $" + amount + " transferred from account " + fromAccountNum + " to account " + toAccountNum);
        return true;
    }

    // Get balance
    public static Double getBalance(String username) {
        if (accounts.containsKey(username)) {
            return accounts.get(username).getBalance();
        }
        return null;
    }

    private static String hashPassword(String password, String salt) {
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

    private static void saveAccountsToFile() {
        System.out.println("Saving accounts to file...");
        try (java.io.PrintWriter writer = new java.io.PrintWriter("accounts.txt")) {
            for (Map.Entry<String, Account> entry : accounts.entrySet()) {
                String username = entry.getKey();
                Account acc = entry.getValue();
                writer.println(username + "," + acc.getHashedPassword() + "," + acc.getSalt() + "," + acc.getBalance() + "," + acc.getAccountNum());
            }
        } catch (Exception e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private static void loadAccounts() {
        java.io.File file = new java.io.File("accounts.txt");
        if (!file.exists())
            return;

        try (java.util.Scanner fileScanner = new java.util.Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    String salt = parts[2];
                    double balance = Double.parseDouble(parts[3]);
                    int accountNum = Integer.parseInt(parts[4]);
                    accounts.put(username, new Account(hashedPassword, salt, balance, accountNum));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    private static int generateAccountNumbers() {
         
        Random random = new Random();
        int accountNumbers = 0;;

        for (int i = 0; i < 9; i++) {
            accountNumbers = accountNumbers * 10 + random.nextInt(10);
            }
            return accountNumbers;
        }

    private static boolean accountNumberExists(int accountNum) {
        for(Account account : accounts.values()) {
            if (account.getAccountNum() == accountNum) {
                return true;
            }
        }
        return false;
    }
}