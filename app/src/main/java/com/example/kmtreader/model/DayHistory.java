package com.example.kmtreader.model;

import java.util.ArrayList;

public class DayHistory {

    private ArrayList<History> mHistories;

    private String mHistoryDate;

    public DayHistory(String historyDate) {
        this.mHistoryDate = historyDate;
        this.mHistories = new ArrayList<>();
    }

    public void addHistory(History history) {
        this.mHistories.add(history);
    }

    public ArrayList<History> getHistories() {
        return mHistories;
    }

    public void setHistories(ArrayList<History> histories) {
        this.mHistories.clear();
        this.mHistories = histories;
    }

    public String getHistoryDate() {
        return mHistoryDate;
    }

    public void setHistoryDate(String historyDate) {
        this.mHistoryDate = historyDate;
    }
}
