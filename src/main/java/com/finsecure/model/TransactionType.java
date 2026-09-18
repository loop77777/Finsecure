package com.finsecure.model;

public enum TransactionType {
    DOMESTIC_TRANSFER(1.0, "Standard Domestic RTGS Settlement"),
    CROSS_BORDER(2.5, "SWIFT International Wire Clearing"),
    SECURITIES_SETTLEMENT(4.0, "High-Value Capital Markets Disbursement");

    private final double tariffMultiplier;
    private final String description;

    TransactionType(double tariffMultiplier, String description) {
        this.tariffMultiplier = tariffMultiplier;
        this.description = description;
    }

    public double getTariffMultiplier() {
        return tariffMultiplier;
    }

    public String getDescription() {
        return description;
    }
}