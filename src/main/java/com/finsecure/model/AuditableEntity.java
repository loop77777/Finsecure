package com.finsecure.model;

public interface AuditableEntity {
    // Interface Constants: Automatically public static final (Metaspace bound)
    String AUDIT_SYSTEM_VERSION = "v26.3-LTS";
    double BASE_PROCESSING_FEE = 1.50;

    // Abstract methods: Automatically public abstract
    String generateAuditDigest();
    String getEntityId();
}