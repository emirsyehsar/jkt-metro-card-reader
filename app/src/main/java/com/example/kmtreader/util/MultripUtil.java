package com.example.kmtreader.util;

import androidx.annotation.Nullable;

import com.example.kmtreader.model.enums.Transaction;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MultripUtil {
    private static final byte CREDIT_TYPE = 1;

    private static final long EPOCH_2000 = 946684800L;

    public static int byteArrayToInt(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).getInt();
    }

    public static int getBalanceChange(byte[] rawBalanceChange) {
        return byteArrayToInt(rawBalanceChange);
    }

    public static boolean getCreditType(byte[] rawCreditType) {
        return rawCreditType[0] != CREDIT_TYPE;
    }

    public static long getEpochTime(byte[] rawEpochTime) {
        long epochTime = 0L;
        for (int i = 0; i < rawEpochTime.length; i++)
            epochTime = (epochTime << 8L) + (rawEpochTime[i] & 0xFF);
        return epochTime + EPOCH_2000;
    }

    @Nullable
    public static String getJourneyDate(byte[] rawJourneyDate) {
        long l = 0L;
        for (int i = 0; i < rawJourneyDate.length; i++)
            l = (l << 8L) + (rawJourneyDate[i] & 0xFF);
        l += EPOCH_2000;
        if (l == EPOCH_2000)
            return null;
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        Date date = new Date(TimeUnit.SECONDS.toMillis(l));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    public static String getJourneyTime(long timestamp) {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        Date date = new Date(TimeUnit.SECONDS.toMillis(timestamp));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    public static String getJourneyTime(byte[] paramArrayOfbyte) {
        long l = 0L;
        for (int i = 0; i < paramArrayOfbyte.length; i++)
            l = (l << 8L) + (paramArrayOfbyte[i] & 0xFF);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        Date date = new Date(TimeUnit.SECONDS.toMillis(l + EPOCH_2000));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    public static int getStationCode(byte[] rawStationCode) {
        return byteArrayToInt(rawStationCode);
    }

    public static int getTransactionCode(byte[] rawStationCode) {
        return byteArrayToInt(rawStationCode);
    }

    public static Transaction getTransaction(byte[] rawStationCode) {
        return Transaction.getTransaction(getTransactionCode(rawStationCode));
    }
}
