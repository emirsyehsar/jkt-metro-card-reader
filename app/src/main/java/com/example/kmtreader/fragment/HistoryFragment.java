package com.example.kmtreader.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kmtreader.R;
import com.example.kmtreader.adapter.HistoryAdapter;
import com.example.kmtreader.model.History;

import java.util.List;

public class HistoryFragment extends Fragment {

    private TextView txtHistoryMessage;

    private RecyclerView rvHistoryList;

    private String mMessage;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
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

    public void setHistoryResult(List<History> historyList) {
        if (historyList != null && !historyList.isEmpty()) {
            txtHistoryMessage.setVisibility(View.GONE);
            rvHistoryList.setVisibility(View.VISIBLE);

            HistoryAdapter historyAdapter = new HistoryAdapter(historyList);
            rvHistoryList.setAdapter(historyAdapter);
        } else {
            txtHistoryMessage.setVisibility(View.VISIBLE);
            rvHistoryList.setVisibility(View.GONE);
        }
    }
    public void setMessage(String message) {
        this.mMessage = message;
    }

    private void initView(View view) {
        txtHistoryMessage = (TextView) view.findViewById(R.id.txt_history_message);
        rvHistoryList = (RecyclerView) view.findViewById(R.id.rv_history_list);
        txtHistoryMessage.setVisibility(View.VISIBLE);
        rvHistoryList.setVisibility(View.GONE);

        txtHistoryMessage.setText(mMessage);
        rvHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
