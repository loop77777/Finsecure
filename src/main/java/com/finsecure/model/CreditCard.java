package com.finsecure.model;

/**
 * Demonstrates Inheritance (Is-A PaymentInstrument) and Overriding.
 */
public class CreditCard extends PaymentInstrument {
    private final String cardNumber;
    private final double creditLimit;
    private double currentBalance;

    public CreditCard(String instrumentId, String holderName, String cardNumber, double creditLimit) {
        super(instrumentId, holderName);
        this.cardNumber = cardNumber;
        this.creditLimit = creditLimit;
        this.currentBalance = 0.0;
    }

    @Override
    public boolean processDebit(double amount) {
        if (currentBalance + amount <= creditLimit) {
            currentBalance += amount;
            System.out.printf("  [CreditCard] Debit of $%.2f approved. Headroom remaining: $%.2f%n",
                    amount, (creditLimit - currentBalance));
            return true;
        }
        System.out.println("  [CreditCard] Debit declined: Insufficient credit limit!");
        return false;
    }

    // Subclass-specific method (Target for explicit downcasting)
    public void executeContactlessNfcSwipe() {
        String lastFour = (cardNumber.length() >= 4)
                ? cardNumber.substring(cardNumber.length() - 4)
                : "XXXX";
        System.out.println("  [NFC EMV SWIPE] Terminal contacted card ending in " + lastFour + ".");
    }

    public double getCurrentBalance() {
        return currentBalance;
    }
}