package com.example.kmtreader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmtreader.R;
import com.example.kmtreader.model.History;
import com.example.kmtreader.model.enums.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<History> historyList;

    private final SimpleDateFormat simpleDateFormat;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
        this.simpleDateFormat = new SimpleDateFormat("HH:mm");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_history, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        boolean isSeparatorVisible = position == 0
                || !historyList.get(position - 1).getJourneyDate()
                .equals(historyList.get(position).getJourneyDate());
        setDateSeparatorVisible(holder, isSeparatorVisible);
        if (isSeparatorVisible) {
            holder.getTxtDate().setText(historyList.get(position).getJourneyDate());
        }
        holder.getImgTransactionType()
                .setImageResource(getIcon(historyList.get(position).getTransaction()));
        holder.getTxtTransactionType().setText(
                historyList.get(position).getTransaction().getTransactionType()
        );
        //TODO EMIR : 12-03-2023 SET VISIBILITY TO VISIBLE AFTER STATION NAME IS GATHERED
        holder.getTxtStationName().setVisibility(View.GONE);

        String time = simpleDateFormat.format(new Date(historyList.get(position).getTimestamp()));
        holder.getTxtTime().setText(time);

        String balanceChangeSign = historyList.get(position).getCredit() ? "-Rp%d" : "+Rp%d";
        String balanceChange = String.format(balanceChangeSign, historyList.get(position).getBalanceChange());
        holder.getTxtBalanceChange().setText(balanceChange);

        holder.getSeparatorBottom().setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    private void setDateSeparatorVisible(ViewHolder holder, boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        holder.getTxtDate().setVisibility(visibility);
        holder.getSeparatorTop().setVisibility(visibility);
    }

    private int getIcon(Transaction transactionType) {
        switch (transactionType) {
            case TICKET_BOOTH: return R.drawable.icon_ticket_counter;
            case TICKET_VENDING_MACHINE: return R.drawable.icon_ticket_machine;
            case TAP_OUT:
            default: return R.drawable.icon_turnstile;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDate;
        private final View separatorTop;
        private final ImageView imgTransactionType;
        private final TextView txtTransactionType;
        private final TextView txtStationName;
        private final TextView txtTime;
        private final TextView txtBalanceChange;
        private final View separatorBottom;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            txtDate = (TextView) view.findViewById(R.id.txt_date);
            separatorTop = view.findViewById(R.id.separator_top);
            imgTransactionType = (ImageView) view.findViewById(R.id.img_transaction_type);
            txtTransactionType = (TextView) view.findViewById(R.id.txt_transaction_type);
            txtStationName = (TextView) view.findViewById(R.id.txt_station_name);
            txtTime = (TextView) view.findViewById(R.id.txt_time);
            txtBalanceChange = (TextView) view.findViewById(R.id.txt_balance_change);
            separatorBottom = view.findViewById(R.id.separator_bottom);
        }

        public TextView getTxtDate() {
            return txtDate;
        }

        public View getSeparatorTop() {
            return separatorTop;
        }

        public ImageView getImgTransactionType() {
            return imgTransactionType;
        }

        public TextView getTxtTransactionType() {
            return txtTransactionType;
        }

        public TextView getTxtStationName() {
            return txtStationName;
        }

        public TextView getTxtTime() {
            return txtTime;
        }

        public TextView getTxtBalanceChange() {
            return txtBalanceChange;
        }

        public View getSeparatorBottom() {
            return separatorBottom;
        }
    }
}
