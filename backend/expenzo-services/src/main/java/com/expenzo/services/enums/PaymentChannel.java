package com.expenzo.services.enums;

public enum PaymentChannel {
    CREDIT_CARD("CREDIT_CARD"), 
    DEBIT_CARD("DEBIT_CARD"), 
    BANK_ACCOUNT("BANK_ACCOUNT"), 
    WALLET("WALLET");

    private String value;

    PaymentChannel(String value) {
        this.value = value;
    }

    public PaymentChannel fromValue(String value) {
        for (PaymentChannel pc: PaymentChannel.values()) {
            if (pc.value.equals(value)) {
                return pc;
            }
        }
        return null;
    }
}
