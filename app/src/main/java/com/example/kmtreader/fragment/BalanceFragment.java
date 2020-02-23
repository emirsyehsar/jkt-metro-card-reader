package com.example.kmtreader.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kmtreader.R;
import com.example.kmtreader.model.Balance;

public class BalanceFragment extends Fragment {

    private String mMessage;

    private TextView txtBalance;

    private TextView txtBalanceLabel;

    private TextView txtCardNumber;

    private TextView txtCardNumberLabel;

    private TextView txtLastTransaction;

    private TextView txtLastTransactionLabel;

    public BalanceFragment() {
        // Required empty public constructor
    }

    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public void setReadResult(Balance balance) {
        if (this.txtCardNumberLabel.getVisibility() == View.GONE)
            this.txtCardNumberLabel.setVisibility(View.VISIBLE);
        if (this.txtCardNumber.getVisibility() == View.GONE)
            this.txtCardNumber.setVisibility(View.VISIBLE);
        if (this.txtBalanceLabel.getVisibility() == View.GONE)
            this.txtBalanceLabel.setVisibility(View.VISIBLE);
        if (this.txtLastTransactionLabel.getVisibility() == View.GONE)
            this.txtLastTransactionLabel.setVisibility(View.VISIBLE);
        if (this.txtLastTransaction.getVisibility() == View.GONE)
            this.txtLastTransaction.setVisibility(View.VISIBLE);
        this.txtCardNumber.setText(balance.getCardNumber());
        this.txtBalance.setText(balance.getBalance());
        this.txtLastTransaction.setText(balance.getLastTransaction());
    }

    private void initView(View view) {
        String str;
        this.txtCardNumberLabel = view.findViewById(R.id.txt_card_number_label);
        this.txtCardNumber = view.findViewById(R.id.txt_card_number);
        this.txtBalanceLabel = view.findViewById(R.id.txt_balance_label);
        this.txtBalance = view.findViewById(R.id.txt_balance);
        this.txtLastTransactionLabel = view.findViewById(R.id.txt_last_transaction_label);
        this.txtLastTransaction = view.findViewById(R.id.txt_last_transaction);
        TextView textView = this.txtBalance;
        if (this.mMessage != null) {
            str = this.mMessage;
        } else {
            str = "PLACEHOLDER TEXT";
        }
        textView.setText(str);
    }
}
