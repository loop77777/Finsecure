package com.finsecure.model;

/**
 * Encapsulation and Interface Implementation.
 */
public class BankAccount implements AuditableEntity {
    private final String accountNumber;
    private final String accountHolder;
    private double balance;

    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = (initialBalance >= 0.0) ? initialBalance : 0.0;
    }

    public synchronized void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public synchronized void withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    @Override
    public String getEntityId() {
        return accountNumber;
    }

    @Override
    public String generateAuditDigest() {
        return "ACCT_DIGEST[" + accountNumber + "|HOLDER=" + accountHolder + "|BAL=" + balance + "]";
    }
}