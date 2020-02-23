package com.example.kmtreader.model;

public class History {

    private int mBalanceChange;

    private boolean mCredit;

    private String mJourneyDate;

    private int mStationCode;

    private long mTimestamp;

    private int mTransactionCode;

    public int getBalanceChange() {
        return mBalanceChange;
    }

    public void setBalanceChange(int balanceChange) {
        this.mBalanceChange = balanceChange;
    }

    public boolean isCredit() {
        return mCredit;
    }

    public void setCredit(boolean credit) {
        this.mCredit = credit;
    }

    public String getJourneyDate() {
        return mJourneyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.mJourneyDate = journeyDate;
    }

    public int getStationCode() {
        return mStationCode;
    }

    public void setStationCode(int stationCode) {
        this.mStationCode = stationCode;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public int getTransactionCode() {
        return mTransactionCode;
    }

    public void setTransactionCode(int transactionCode) {
        this.mTransactionCode = transactionCode;
    }
}
