package com.example.kmtreader.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kmtreader.R;
import com.example.kmtreader.adapter.ViewPagerAdapter;
import com.example.kmtreader.model.Balance;
import com.example.kmtreader.helper.NfcHelper;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback{

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private NfcAdapter mNfcAdapter;
    private NfcHelper mNfcHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupView();
        setupNfc();
        checkNfc();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action != null && (action.equals("android.nfc.action.TAG_DISCOVERED") || action.equals("android.nfc.action.TECH_DISCOVERED"))) {
            Balance balance = new Balance();
            balance.setCardNumber(this.mNfcHelper.getCardNumber());
            balance.setBalance(this.mNfcHelper.getBalance());
            balance.setLastTransaction(this.mNfcHelper.getLastTransaction());
            this.viewPagerAdapter.setReadResult(balance);
            this.viewPagerAdapter.setHistoryResult(this.mNfcHelper.getHistories());
        }
    }

    @Override
    public void onPause() {
        this.mNfcAdapter.disableForegroundDispatch(this);
        this.mNfcAdapter.disableReaderMode(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mNfcAdapter.enableForegroundDispatch(this, this.mNfcHelper.getPendingIntent(), this.mNfcHelper.getIntentFilters(), this.mNfcHelper.getTechList());
        this.mNfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
    }

    public void onTagDiscovered(Tag tag) {
        try {
            this.mNfcHelper.handleTag(tag);
            final Balance balance = new Balance();
            balance.setCardNumber(this.mNfcHelper.getCardNumber());
            balance.setBalance(this.mNfcHelper.getBalance());
            balance.setLastTransaction(this.mNfcHelper.getLastTransaction());
            runOnUiThread(() -> {
                viewPagerAdapter.setReadResult(balance);
                //viewPagerAdapter.setHistoryResult(this.mNfcHelper.getHistories());
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void checkNfc() {
        if (!this.mNfcAdapter.isEnabled()) {
            viewPagerAdapter.setMessage(getString(R.string.readeractivity_error_nfcturnedoff));
            return;
        }
        viewPagerAdapter.setMessage(getString(R.string.readeractivity_label_tapyourcard));
    }

    private void setupNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (this.mNfcAdapter == null) {
            Toast.makeText(this, getString(R.string.readeractivity_error_nonfc), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        this.mNfcHelper = new NfcHelper(this);
    }

    private void setupView() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }
}
