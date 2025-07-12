package com.banking.bank;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
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
            String hashedPassword = hashPassword(password);
            return accounts.get(username).getHashedPassword().equals(hashedPassword);
        }
        return false;
    }

    // Logic for account creation
    public static boolean createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            return false; // Username already exists
        }
        String hashedPassword = hashPassword(password);
        accounts.put(username, new Account(hashedPassword, 500.0));
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

    // Get balance
    public static Double getBalance(String username) {
        if (accounts.containsKey(username)) {
            return accounts.get(username).getBalance();
        }
        return null;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password;
            byte[] hashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static void saveAccountsToFile() {
        System.out.println("Saving accounts to file...");
        try (java.io.PrintWriter writer = new java.io.PrintWriter("accounts.txt")) {
            for (Map.Entry<String, Account> entry : accounts.entrySet()) {
                String username = entry.getKey();
                Account acc = entry.getValue();
                writer.println(username + "," + acc.getHashedPassword() + "," + acc.getBalance());
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
                if (parts.length == 3) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    accounts.put(username, new Account(hashedPassword, balance));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }
}