package com.example.kmtreader.model.enums

enum class Transaction(val transactionType: String) {
    TICKET_BOOTH("LOKET"),
    TAP_OUT("TAP OUT"),
    TICKET_VENDING_MACHINE("TICKET VENDING MACHINE"),
    EXTERNAL_TRANSACTION("TRANSAKSI EKSTERNAL"),
    UNKNOWN("TIDAK DIKETAHUI");

    companion object {

        @JvmStatic
        fun getInternalTransaction(transactionTypeCode: Int): Transaction {
            return when (transactionTypeCode) {
                257 -> TAP_OUT
                512 -> TICKET_BOOTH
                256 -> TICKET_VENDING_MACHINE
                else -> UNKNOWN
            }
        }
    }
}