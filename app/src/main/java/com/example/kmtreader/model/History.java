package com.example.kmtreader.model;

import com.example.kmtreader.model.enums.Station;
import com.example.kmtreader.model.enums.Transaction;

public class History {

    private int mBalanceChange;

    private boolean mCredit;

    private String mJourneyDate;

    private Station mStation;

    private long mTimestamp;

    private Transaction mTransaction;

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

    public Station getStation() {
        return mStation;
    }

    public void setStation(Station station) {
        mStation = station;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public Transaction getTransaction() {
        return mTransaction;
    }

    public void setTransaction(Transaction transaction) {
        mTransaction = transaction;
    }
}
