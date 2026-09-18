package com.finsecure.model;

/**
 * Demonstrates Abstraction and Data Hiding.
 */
public abstract class PaymentInstrument {
    private final String instrumentId;
    private final String holderName;
    private boolean active;

    public PaymentInstrument(String instrumentId, String holderName) {
        this.instrumentId = instrumentId;
        this.holderName = holderName;
        this.active = true;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getHolderName() {
        return holderName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract boolean processDebit(double amount);
}

