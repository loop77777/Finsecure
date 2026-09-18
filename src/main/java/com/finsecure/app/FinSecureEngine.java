package com.finsecure.app;

import com.finsecure.exception.InvalidPayloadException;
import com.finsecure.exception.LedgerCapacityExceededException;
import com.finsecure.model.BankAccount;
import com.finsecure.model.CreditCard;
import com.finsecure.model.PaymentInstrument;
import com.finsecure.model.TransactionRecord;
import com.finsecure.model.TransactionType;
import com.finsecure.service.SettlementBatchProcessor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FinSecureEngine {

    // Overloaded Methods: Widening beats Boxing
    public static void dispatchNotification(long customerId) {
        System.out.println("Invoked: dispatchNotification(long) -> Primitive Widening won!");
    }

    public static void dispatchNotification(Long customerId) {
        System.out.println("Invoked: dispatchNotification(Long) -> Autoboxing won!");
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       FINSECURE CORE SETTLEMENT ENGINE - INITIALIZING RUNTIME ARCHITECTURE     ");
        System.out.println("================================================================================\n");

        // 1. UNINITIALIZED VARIABLES: Heap defaults vs Local Stack rules
        System.out.println(">>> 1. UNINITIALIZED VARIABLES: Heap Instance Defaults vs. Local Stack");
        TransactionRecord uninitRecord = new TransactionRecord();
        System.out.println("Uninitialized Instance int field (defaulted)     : " + uninitRecord.getTransactionIndex());
        System.out.println("Uninitialized Instance double field (defaulted)  : " + uninitRecord.getTransactionAmount());
        System.out.println("Uninitialized Instance boolean field (defaulted) : " + uninitRecord.isSuspicious());
        System.out.println("Uninitialized Instance Reference (defaulted)     : " + uninitRecord.getTransactionId());

        int localStackVal;
        // System.out.println(localStackVal); // COMPILER ERROR: must be initialized
        localStackVal = 100;
        System.out.println("Explicitly assigned local stack primitive        : " + localStackVal);

        // 2. DATES, LOCALES, AND NUMBER/CURRENCY FORMATTING
        System.out.println("\n>>> 2. LOCALIZATION, DATES, AND CURRENCIES");
        ZonedDateTime nyTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd-MMM-yyyy HH:mm:ss z");
        System.out.println("Settlement Wall-Clock (New York): " + nyTime.format(dtf));

        BigDecimal settlementAmount = new BigDecimal("1549200.75");
        NumberFormat usFormat = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat deFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        NumberFormat inFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        System.out.println("Localized Gross (US)     : " + usFormat.format(settlementAmount));
        System.out.println("Localized Gross (Germany): " + deFormat.format(settlementAmount));
        System.out.println("Localized Gross (India)  : " + inFormat.format(settlementAmount));

        // 3. WRAPPERS, BOXING, AND INTEGER CACHE (-128 to 127)
        System.out.println("\n>>> 3. WRAPPERS, BOXING, AND INTERNAL POOLING");
        Integer cached1 = Integer.valueOf(127);
        Integer cached2 = Integer.valueOf(127);
        System.out.println("Integer.valueOf(127) reference match (Cached)    : " + (cached1 == cached2)); // true

        Integer outside1 = Integer.valueOf(256);
        Integer outside2 = Integer.valueOf(256);
        System.out.println("Integer.valueOf(256) reference match (Heap-split): " + (outside1 == outside2)); // false
        System.out.println("Integer.valueOf(256) logical equals() match      : " + outside1.equals(outside2)); // true

        // 4. OVERLOADING PRECEDENCE: Widening beats Boxing
        System.out.println("\n>>> 4. METHOD OVERLOADING RESOLUTION HIERARCHY");
        int testId = 4096;
        dispatchNotification(testId);              // Widening: matches long
        dispatchNotification(Long.valueOf(4096L)); // Direct object: matches Long

        // 5. STRICT PASS-BY-VALUE VERIFICATION
        System.out.println("\n>>> 5. STRICT PASS-BY-VALUE VERIFICATION");
        SettlementBatchProcessor processor = new SettlementBatchProcessor();
        BankAccount corporateAcct = new BankAccount("CORP-7701", "Acme Holdings", 50000.0);
        int localInt = 10;

        System.out.println("Caller variables before method invocation:");
        System.out.println("  localInt = " + localInt + " | Account Balance = $" + corporateAcct.getBalance());

        processor.demonstratePassByValue(localInt, corporateAcct);

        System.out.println("Caller variables after method invocation:");
        System.out.println("  localInt (Stack unchanged)        = " + localInt);
        System.out.println("  corporateAcct (Heap state mutated)= $" + corporateAcct.getBalance());
        System.out.println("  corporateAcct Holder (Not shadow) = " + corporateAcct.getAccountHolder());

        // 6. POLYMORPHISM & REFERENCE CASTING
        System.out.println("\n>>> 6. POLYMORPHISM & REFERENCE VARIABLE CASTING");
        PaymentInstrument instrument = new CreditCard("INST-88", "Jane Doe", "4000-1234-5678-9999", 15000.0);
        instrument.processDebit(2400.0); // Polymorphic dispatch

        // Safe Downcast (Java 8 compatible)
        if (instrument instanceof CreditCard) {
            CreditCard card = (CreditCard) instrument;
            card.executeContactlessNfcSwipe();
        }

        // 7. PATTERN MATCHING & PAYLOAD EXTRACTION
        System.out.println("\n>>> 7. PATTERN MATCHING & PAYLOAD EXTRACTION");
        try {
            processor.parseAndVerifyTelemetry("TXN-SEC8809;VAL=145000.50;CUR=USD");
            processor.parseAndVerifyTelemetry("TXN-INVALID-STREAM-CHUNK"); // Triggers checked exception
        } catch (InvalidPayloadException ex) {
            System.err.println("CAUGHT EXPECTED EXCEPTION: " + ex.getMessage());
        }

        // 8. BATCH PIPELINE, BUFFER OVERFLOW & STAGING ARRAYS
        System.out.println("\n>>> 8. BATCH PIPELINE, BUFFER OVERFLOW & STAGING ARRAYS");
        TransactionRecord tx1 = new TransactionRecord("TXN-101", 15000.0, TransactionType.DOMESTIC_TRANSFER, corporateAcct);
        TransactionRecord tx2 = new TransactionRecord("TXN-102", 320000.0, TransactionType.CROSS_BORDER, corporateAcct);
        TransactionRecord tx3 = new TransactionRecord("TXN-103", 500000.0, TransactionType.SECURITIES_SETTLEMENT, corporateAcct);

        processor.stageTransaction(tx1);
        processor.stageTransaction(tx2);
        processor.stageTransaction(tx3);

        // Verification of Buffer Overflow (Unchecked exception)
        try {
            TransactionRecord tx4 = new TransactionRecord("TXN-104", 1000.0, TransactionType.DOMESTIC_TRANSFER);
            processor.stageTransaction(tx4);
        } catch (LedgerCapacityExceededException ex) {
            System.err.println("CAUGHT EXPECTED EXCEPTION: " + ex.getMessage());
        }

        processor.processBatchQueue();

        // 9. AUDITING ARCHITECTURE (StringBuilder vs StringBuffer)
        System.out.println("\n>>> 9. AUDITING ARCHITECTURE (StringBuilder vs StringBuffer)");
        String singleThreadDigest = processor.compileAuditTrail(tx2);
        System.out.println("Fast Digest (StringBuilder): " + singleThreadDigest);

        StringBuffer sharedLog = new StringBuffer();
        processor.appendThreadSafeLog(sharedLog, singleThreadDigest);
        processor.appendThreadSafeLog(sharedLog, "Corporate Account Balance: $" + corporateAcct.getBalance());
        System.out.print("Thread-Safe Log Output (StringBuffer):\n" + sharedLog);

        // 10. STATIC METRICS INVOCATION
        SettlementBatchProcessor.printSystemTotals();

        System.out.println("\n================================================================================");
        System.out.println("        CASE STUDY COMPLETE: ALL DOMAIN SPECIFICATIONS AUDITED AND PASSED       ");
        System.out.println("================================================================================");
    }
}