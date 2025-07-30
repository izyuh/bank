package com.banking.bank;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column
    @NotBlank(message = "Password is required")
    private String salt;

    @Column
    @NotBlank(message = "Hashed password is required")
    private String hashedPassword;

    @Column(nullable = false)
    private double balance = 0.0;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    // Default constructor (required by JPA)
    public Account() {
    }

    // Constructor
    public Account(String username, String hashedPassword, String salt, String accountNumber, double balance) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    // Business methods 
    public void addBalance(double amount) {
        if (amount > 0) {
            this.balance += amount;
        } else {
            throw new IllegalArgumentException("Cannot add a negative amount to the balance.");
        }
    }

    public void withdrawBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Cannot withdraw more than your balance of $" + balance);
        }
        this.balance -= amount;
    }
}
