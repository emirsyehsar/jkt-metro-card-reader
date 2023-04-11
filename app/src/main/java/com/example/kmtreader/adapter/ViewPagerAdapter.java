package com.example.kmtreader.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.kmtreader.fragment.BalanceFragment;
import com.example.kmtreader.fragment.HistoryFragment;
import com.example.kmtreader.model.Balance;
import com.example.kmtreader.model.History;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int BALANCE_POSITION = 0;
    private static final int TAB_COUNT = 2;

    private final CharSequence[] TITLES = new CharSequence[] { "Saldo", "Riwayat" };

    private BalanceFragment mBalanceFragment;
    private HistoryFragment mHistoryFragment;

    private String mMessage;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == BALANCE_POSITION) {
            return BalanceFragment.newInstance();
        } else {
            return HistoryFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(viewGroup, position);
        if (position == 0) {
            this.mBalanceFragment = (BalanceFragment) fragment;
            this.mBalanceFragment.setMessage(this.mMessage);
            return fragment;
        }
        this.mHistoryFragment = (HistoryFragment) fragment;
        this.mHistoryFragment.setMessage(this.mMessage);
        return fragment;
    }

    public void setHistoryResult(List<History> historyList) {
        //TODO EMIR : 28-2-2020 PLEASE REMOVE THIS AFTER HISTORY UI IS READY. FOR DEBUG PURPOSE ONLY
        //ArrayList<History> anotherHistoryList = historyList;
        this.mHistoryFragment.setHistoryResult(historyList);
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public void setReadResult(Balance balance) {
        this.mBalanceFragment.setReadResult(balance);
    }
}
