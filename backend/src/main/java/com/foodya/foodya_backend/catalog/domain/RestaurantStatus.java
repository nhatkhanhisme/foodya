package com.foodya.foodya_backend.catalog.domain;

public enum RestaurantStatus {
    PENDING,    // Awaiting admin approval — not visible to customers
    APPROVED,   // Active — visible and orderable
    REJECTED,   // Declined — owner notified with reason, may resubmit
    SUSPENDED   // Admin-suspended for policy violation — hidden from customers
}
