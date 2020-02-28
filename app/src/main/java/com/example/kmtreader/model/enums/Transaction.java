package com.example.kmtreader.model.enums;

public enum Transaction {
    TICKET_BOOTH("LOKET"),
    TAP_OUT("TAP OUT"),
    TICKET_VENDING_MACHINE("TICKET VENDING MACHINE"),
    UNKNOWN("TIDAK DIKETAHUI");

    private String mTransactionType;

    Transaction(String transactionType) {
        mTransactionType = transactionType;
    }

    public static Transaction getTransaction(int transactionTypeCode) {
        switch (transactionTypeCode) {
            default:
                return UNKNOWN;
            case 272:
                return TAP_OUT;
            case 288:
                return TICKET_BOOTH;
            case 304:
                return TICKET_VENDING_MACHINE;
        }
    }
}
