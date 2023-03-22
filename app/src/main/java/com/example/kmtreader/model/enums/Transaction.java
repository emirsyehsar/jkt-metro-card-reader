package com.example.kmtreader.model.enums;

public enum Transaction {
    TICKET_BOOTH("LOKET"),
    TAP_OUT("TAP OUT"),
    TICKET_VENDING_MACHINE("TICKET VENDING MACHINE"),
    EXTERNAL_TRANSACTION("TRANSAKSI EKSTERNAL"),
    UNKNOWN("TIDAK DIKETAHUI");

    private String mTransactionType;

    public String getTransactionType() {
        return mTransactionType;
    }

    Transaction(String transactionType) {
        mTransactionType = transactionType;
    }

    public static Transaction getInternalTransaction(int transactionTypeCode) {
        switch (transactionTypeCode) {
            default:
                return UNKNOWN;
            case 257:
                return TAP_OUT;
            case 512:
                return TICKET_BOOTH;
            case 256:
                return TICKET_VENDING_MACHINE;
        }
    }
}
