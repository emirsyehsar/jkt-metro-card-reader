package com.example.kmtreader.model;

public class Balance {

    private String mBalance;
    private String mCardNumber;
    private String mLastTransaction;

    public String getBalance() {
        return mBalance;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getLastTransaction() {
        return mLastTransaction;
    }

    public void setBalance(String balance) {
        this.mBalance = balance;
    }

    public void setCardNumber(String cardNumber) {
        this.mCardNumber = cardNumber;
    }

    public void setLastTransaction(String lastTransaction) {
        this.mLastTransaction = lastTransaction;
    }
}
