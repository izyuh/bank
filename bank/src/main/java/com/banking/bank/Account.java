package com.banking.bank;

public class Account {
    private String hashedPassword;
    private String salt;
    private double balance;

    public Account(String hashedPassword, String salt, double balance) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.balance = balance;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        if (amount > 0) {
            this.balance += amount;
        } else {
            System.out.println("Cannot add a negative amount to the balance.");
        }
    }

    public void withdrawBalance(double amount) {
        if (amount > 0 && amount <= balance) {
            this.balance -= amount;
        } else {
            System.out.println("Cannot withdraw more that your balance of " + balance);
        }
    }

}