package com.finsecure.service;

import com.finsecure.exception.InvalidPayloadException;
import com.finsecure.exception.LedgerCapacityExceededException;
import com.finsecure.model.BankAccount;
import com.finsecure.model.TransactionRecord;
import com.finsecure.model.TransactionType;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettlementBatchProcessor {
    // Array declaration & construction: Fixed buffer capacity
    private static final int QUEUE_CAPACITY = 3;
    private final TransactionRecord[] batchSlots = new TransactionRecord[QUEUE_CAPACITY];
    private int queueHead = 0;

    // 2D Array: Storage routing cross-matrix
    private final String[][] settlementMatrix;

    // Static variables: Global accumulators shared across instances
    private static double totalSettledVolume = 0.0;
    private static int processedCount = 0;

    public SettlementBatchProcessor() {
        this.settlementMatrix = new String[2][2];
        this.settlementMatrix[0][0] = "US-EAST-VAULT-1";
        this.settlementMatrix[0][1] = "US-WEST-VAULT-2";
        this.settlementMatrix[1][0] = "EU-CENTRAL-VAULT-1";
        this.settlementMatrix[1][1] = "APAC-SG-VAULT-1";
    }

    // --- PROOF OF PASS-BY-VALUE MECHANICS ---
    public void demonstratePassByValue(int stackCounter, BankAccount heapAccount) {
        // 1. Primitive mutation affects only the local stack copy
        stackCounter = stackCounter + 500;

        // 2. Object field mutation mutates the heap object via pointer copy
        heapAccount.deposit(1000.0);

        // 3. Pointer reassignment alters only the local stack variable
        heapAccount = new BankAccount("SHADOW-999", "Shadow Account", 0.0);
    }

    // --- ARRAY BUFFER STAGING ---
    public void stageTransaction(TransactionRecord record) throws LedgerCapacityExceededException {
        for (int i = 0; i < batchSlots.length; i++) {
            if (batchSlots[i] == null) {
                record.setTransactionIndex(i + 1);
                batchSlots[i] = record;
                queueHead++;
                System.out.println("-> Staged: " + record.getTransactionId() + " into Buffer Slot [" + i + "]");
                return;
            }
        }
        throw new LedgerCapacityExceededException("Buffer Overflow: Hardware queue capacity ("
                + QUEUE_CAPACITY + ") exceeded!");
    }

    // --- OPERATORS, FLOW CONTROL, BRANCHING, & LOOPS ---
    public void processBatchQueue() {
        System.out.println("\n--- Processing Staged Settlement Queue ---");
        int idx = 0;

        while (idx < batchSlots.length) {
            TransactionRecord current = batchSlots[idx];

            if (current == null) {
                idx++;
                continue; // Skip empty slots
            }

            double tariffRate;
            switch (current.getType()) {
                case CROSS_BORDER:
                    tariffRate = 25.00 * current.getType().getTariffMultiplier();
                    break;
                case SECURITIES_SETTLEMENT:
                    tariffRate = 100.00 * current.getType().getTariffMultiplier();
                    break;
                case DOMESTIC_TRANSFER:
                default:
                    tariffRate = 5.00;
                    break;
            }

            double gross = current.getTransactionAmount();
            gross += tariffRate; // Compound assignment

            // Ternary Operator
            boolean highVolume = (gross >= 100_000.0);
            double complianceFee = highVolume ? 50.00 : 10.00;
            gross += complianceFee;

            // Short-circuit logical operators
            if (gross > 250_000.0 && current.getType() == TransactionType.CROSS_BORDER) {
                current.setSuspicious(true);
                System.out.println("  [ALERT] Flagged Transaction " + current.getTransactionId() + " for AML compliance audit!");
            }

            // Settlement against composed bank account
            if (current.getTargetAccount() != null) {
                current.getTargetAccount().deposit(current.getTransactionAmount());
            }

            totalSettledVolume += gross;
            processedCount++;

            // Clear array element for GC eligibility
            batchSlots[idx] = null;
            idx++;
        }
    }

    // --- STRINGBUILDER VS STRINGBUFFER AUDITING ---
    public String compileAuditTrail(TransactionRecord tx) {
        StringBuilder sb = new StringBuilder(64);
        sb.append("AUDIT::TX_ID=").append(tx.getTransactionId())
                .append("|AMT=").append(tx.getTransactionAmount())
                .append("|SUSPICIOUS=").append(tx.isSuspicious());
        return sb.toString();
    }

    public void appendThreadSafeLog(StringBuffer sharedBuffer, String entry) {
        sharedBuffer.append("[").append(LocalDate.now()).append("] ").append(entry).append("\n");
    }

    // --- REGEX TOKENIZING & PATTERN MATCHING ---
    public void parseAndVerifyTelemetry(String telemetryStream) throws InvalidPayloadException {
        String regex = "TXN-(?<txCode>[A-Za-z0-9]+);VAL=(?<val>\\d+(\\.\\d{1,2})?);CUR=(?<cur>[A-Z]{3})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(telemetryStream);

        if (matcher.find()) {
            String code = matcher.group("txCode");
            String valueStr = matcher.group("val");
            String currency = matcher.group("cur");

            double parsedVal = Double.parseDouble(valueStr);
            System.out.printf("  [Regex Matcher] Ingested Packet -> Code: %s | Value: %.2f | Currency: %s%n",
                    code, parsedVal, currency);
        } else {
            throw new InvalidPayloadException("Malformed telemetry stream: " + telemetryStream);
        }
    }

    public static void printSystemTotals() {
        System.out.println("\n--- Global Settlement Telemetry ---");
        System.out.println("Total Transactions Cleared: " + processedCount);
        System.out.printf("Total Settled Gross Volume: $%,.2f%n", totalSettledVolume);
    }
}