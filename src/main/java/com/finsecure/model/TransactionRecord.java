package com.finsecure.model;

/**
 * Full JavaBeans standard entity with all 8 primitive types, literals,
 * static/instance initialization blocks, and constructor chaining.
 */
public class TransactionRecord implements AuditableEntity {
    // All 8 Primitive types with distinct literal formats:
    private byte rawPayloadFlag = 0x7E;                     // Hexadecimal literal (126)
    private short routingPrefix = 0b0101_1010;              // Binary literal (Value: 90)
    private int transactionIndex;                           // Default 0
    private long epochTimestampNs = 1_726_000_000_000_000L; // Underscore long literal
    private float riskThreshold = 0.05f;                    // Float literal
    private double transactionAmount;                       // Default 0.0
    private char currencySymbol = '$';                      // Char literal
    private boolean isSuspicious;                           // Default false

    // Object References (Heap)
    private String transactionId;                           // Default null
    private TransactionType type;
    private BankAccount targetAccount;                      // Composition (Has-A)

    // Static Initialization Block (SIB)
    static {
        System.out.println("[SIB] TransactionRecord class metadata loaded into JVM Metaspace.");
    }

    // Instance Initialization Block (IIB)
    {
        this.isSuspicious = false;
        this.rawPayloadFlag = 0x7F;
    }

    // Default No-Arg Constructor (JavaBeans standard)
    public TransactionRecord() {
        super();
    }

    // Overloaded Constructor 1: Basic (Chained)
    public TransactionRecord(String transactionId, double transactionAmount, TransactionType type) {
        this(transactionId, transactionAmount, type, null);
    }

    // Overloaded Constructor 2: Full Specification Target
    public TransactionRecord(String transactionId, double transactionAmount,
                             TransactionType type, BankAccount targetAccount) {
        this.transactionId = transactionId;
        this.transactionAmount = transactionAmount;
        this.type = type;
        this.targetAccount = targetAccount;
    }

    // JavaBeans Standard Accessors & Mutators
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public double getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(double transactionAmount) { this.transactionAmount = transactionAmount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public BankAccount getTargetAccount() { return targetAccount; }
    public void setTargetAccount(BankAccount targetAccount) { this.targetAccount = targetAccount; }

    public boolean isSuspicious() { return isSuspicious; }
    public void setSuspicious(boolean suspicious) { isSuspicious = suspicious; }

    public int getTransactionIndex() { return transactionIndex; }
    public void setTransactionIndex(int transactionIndex) { this.transactionIndex = transactionIndex; }

    public byte getRawPayloadFlag() { return rawPayloadFlag; }
    public short getRoutingPrefix() { return routingPrefix; }
    public long getEpochTimestampNs() { return epochTimestampNs; }
    public float getRiskThreshold() { return riskThreshold; }
    public char getCurrencySymbol() { return currencySymbol; }

    @Override
    public String getEntityId() { return transactionId; }

    @Override
    public String generateAuditDigest() {
        return "TX_AUDIT[" + transactionId + "|AMT=" + transactionAmount + "|TYPE=" + type + "]";
    }
}