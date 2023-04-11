package com.example.kmtreader.activity

import android.content.pm.ActivityInfo
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.kmtreader.R
import com.example.kmtreader.adapter.ViewPagerAdapter
import com.example.kmtreader.helper.NfcHelper
import com.example.kmtreader.model.Balance
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private lateinit var mNfcAdapter: NfcAdapter
    private lateinit var mNfcHelper: NfcHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupView()
        setupNfc()
        checkNfc()
    }

    override fun onPause() {
        mNfcAdapter.disableForegroundDispatch(this)
        mNfcAdapter.disableReaderMode(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter.enableForegroundDispatch(
            this,
            mNfcHelper.pendingIntent,
            mNfcHelper.intentFilters,
            mNfcHelper.techList
        )
        mNfcAdapter.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onTagDiscovered(tag: Tag?) {
        try {
            mNfcHelper.handleTag(tag)
            var balance = Balance()
            balance.cardNumber = mNfcHelper.cardNumber
            balance.balance = mNfcHelper.balance
            balance.lastTransaction = mNfcHelper.lastTransaction
            runOnUiThread {
                viewPagerAdapter.setReadResult(balance)
                viewPagerAdapter.setHistoryResult(mNfcHelper.histories)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkNfc() {
        if (!mNfcAdapter.isEnabled) {
            viewPagerAdapter.setMessage(getString(R.string.readeractivity_error_nfcturnedoff))
            return
        }
        viewPagerAdapter.setMessage(getString(R.string.readeractivity_label_tapyourcard))
    }

    private fun setupNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            showNoNfcError()
        }
        mNfcHelper = NfcHelper(this)
    }

    private fun setupView() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.visibility = View.VISIBLE
    }

    private fun showNoNfcError() {
        Toast.makeText(
            this,
            getString(R.string.readeractivity_error_nonfc),
            Toast.LENGTH_LONG
        )
            .show()
        finish()
    }
}