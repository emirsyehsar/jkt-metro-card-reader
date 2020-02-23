package com.example.kmtreader.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.kmtreader.fragment.BalanceFragment;
import com.example.kmtreader.fragment.HistoryFragment;
import com.example.kmtreader.model.Balance;

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
        if (position == 0) {
            return BalanceFragment.newInstance();
        } else {
            return HistoryFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "BALANCE";
        } else {
            return "HISTORY";
        }
    }

    @Override
    public int getCount() {
        return 2;
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

    /*public void setHistoryResult(ArrayList<History> paramArrayList) {
        IncrementalChange incrementalChange = $change;
        if (incrementalChange != null) {
            incrementalChange.access$dispatch("setHistoryResult.(Ljava/util/ArrayList;)V", new Object[] { this, paramArrayList });
            return;
        }
        this.mHistoryFragment.setHistoryResult(paramArrayList);
    }*/

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public void setReadResult(Balance balance) {
        this.mBalanceFragment.setReadResult(balance);
    }
}
