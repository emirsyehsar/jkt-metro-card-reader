package com.example.kmtreader.helper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.widget.Toast;

import com.example.kmtreader.R;
import com.example.kmtreader.model.History;
import com.example.kmtreader.model.enums.Station;
import com.example.kmtreader.util.MultripUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class NfcHelper {

    private static final byte[] TSUUKIN_HISTORY_SERVICE_CODE = new byte[] { 32, 15 };
    private static final byte[] TSUUKIN_SYSTEM_CODE = new byte[] { -112, -73 };
    private static final byte[] CHIKATETSU_SYSTEM_CODE = new byte[] { -109, (byte) -141 };
    private static final byte[] TSUUKIN_CARD_NUMBER_SERVICE_CODE = { 48, 11 };
    private static final byte[] TSUUKIN_BALANCE_SERVICE_CODE = new byte[] { 16, 23 };
    private static final byte[] CHIKATETSU_BALANCE_SERVICE_CODE = new byte[] { 16, (byte) 215 };

    private static final byte READ_WITHOUT_ENCRYPTION_COMMAND = 6;

    private static final int FIFTEEN_BLOCK = 15;
    private static final int MAX_ALLOCATED_BYTE_BUFFER = 100;
    private static final int SINGLE_BLOCK = 1;
    private static final int BLOCK_SIZE = 16;
    private static final int TOTAL_BLOCK_SIZE = 256;
    private static final int RESPONSE_DATA_START_INDEX = 13;
    private static final int BLOCK_LIST_COMMAND = 128;

    private Activity mActivity;
    private Intent mFirstIntent;
    private IntentFilter[] mIntentFilters;
    private String mLastTransaction = "";
    private PendingIntent mPendingIntent;

    private String mBalance = "";
    private String mCardNumber = "";

    private String[][] mTechList;

    private ArrayList<History> mHistories = new ArrayList<>();

    public NfcHelper(Activity activity) {
        this.mActivity = activity;
        setupPendingIntent();
    }

    public void setupPendingIntent() {
        this.mFirstIntent = new Intent(this.mActivity.getApplicationContext(), this.mActivity.getClass());
        this.mFirstIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.mPendingIntent = PendingIntent.getActivity(this.mActivity.getApplicationContext(), 0, this.mFirstIntent, 0);
        this.mIntentFilters = new IntentFilter[1];
        this.mTechList = new String[][] { { NfcF.class.getName() } };
        this.mIntentFilters[0] = new IntentFilter();
        this.mIntentFilters[0].addAction("android.nfc.action.TECH_DISCOVERED");
        this.mIntentFilters[0].addCategory("android.intent.category.DEFAULT");
        try {
            this.mIntentFilters[0].addDataType("*/*");
        } catch (android.content.IntentFilter.MalformedMimeTypeException malformedMimeTypeException) {
            throw new RuntimeException("Check your mime type.");
        }
    }

    public String getBalance() {
        return mBalance;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public ArrayList<History> getHistories() {
        return mHistories;
    }

    public IntentFilter[] getIntentFilters() {
        return mIntentFilters;
    }

    public String getLastTransaction() {
        return mLastTransaction;
    }

    public PendingIntent getPendingIntent() {
        return mPendingIntent;
    }

    public String[][] getTechList() {
        return mTechList;
    }

    public void handleIntent(Intent intent) {
        String str = intent.getAction();
        if (str != null && (str.equals("android.nfc.action.TAG_DISCOVERED") || str.equals("android.nfc.action.TECH_DISCOVERED"))) {
            handleTag((Tag) intent.getParcelableExtra("android.nfc.extra.TAG"));
        }
    }

    public void handleTag(Tag paramTag) {
        NfcF nfcF = NfcF.get(paramTag);
        byte[] systemCode = nfcF.getSystemCode();
        if (systemCode[0] == CHIKATETSU_SYSTEM_CODE[0] && systemCode[1] == CHIKATETSU_SYSTEM_CODE[1]) {
            readCard(nfcF, systemCode, CHIKATETSU_BALANCE_SERVICE_CODE, new byte[0], new byte[0]);
        } else if (systemCode[0] == TSUUKIN_SYSTEM_CODE[0] && systemCode[1] == TSUUKIN_SYSTEM_CODE[1]) {
            readCard(nfcF, systemCode, TSUUKIN_BALANCE_SERVICE_CODE, TSUUKIN_CARD_NUMBER_SERVICE_CODE, TSUUKIN_HISTORY_SERVICE_CODE);
        } else {
            this.mActivity.runOnUiThread(new Runnable() {

                public void run() {
                    Toast.makeText(mActivity, mActivity.getString(R.string.readeractivity_error_notkmt), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
    }

    private void readCard(NfcF nfcF, byte[] systemCode, byte[] balanceSystemCode, byte[] cardNumberSystemCode, byte[] historySystemCode) {
        try {
            nfcF.connect();
            nfcF.setTimeout(5000);
            if (nfcF.isConnected()) {
                byte[] targetIdm = Arrays.copyOfRange(nfcF.transceive(getIdmCommand(systemCode)), 2, 10);
                byte[] balanceCommand = readWithoutEncryption(targetIdm, SINGLE_BLOCK, balanceSystemCode);
                byte[] balanceResult = nfcF.transceive(balanceCommand);
                processBalance(balanceResult);

                if (cardNumberSystemCode.length != 0) {
                    byte[] cardNumberCommand = readWithoutEncryption(targetIdm, SINGLE_BLOCK, cardNumberSystemCode);
                    byte[] cardNumberResult = nfcF.transceive(cardNumberCommand);
                    processCardNumber(cardNumberResult);
                }
                if (historySystemCode.length != 0) {
                    byte[] historyCommand = readWithoutEncryption(targetIdm, 15, historySystemCode);
                    byte[] historyFinalBlockCommand = readWithoutEncryptionByBlock(targetIdm, FIFTEEN_BLOCK, historySystemCode);
                    byte[] historyResult = nfcF.transceive(historyCommand);
                    byte[] historyFinalBlockResult = nfcF.transceive(historyFinalBlockCommand);
                    processHistory(historyResult, historyFinalBlockResult);
                }
            }
            nfcF.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
            this.mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mActivity, mActivity.getString(R.string.readeractivity_error_ioexception), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private byte[] getIdmCommand(byte[] systemCode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(MAX_ALLOCATED_BYTE_BUFFER);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(systemCode[0]);
        byteArrayOutputStream.write(systemCode[1]);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(15);
        byte[] idmCommand = byteArrayOutputStream.toByteArray();
        idmCommand[0] = (byte)systemCode.length;
        return idmCommand;
    }

    private void processBalance(byte[] balanceBlock) {
        byte[] trimmedBalanceBlock = Arrays.copyOfRange(balanceBlock, RESPONSE_DATA_START_INDEX, balanceBlock.length);
        byte[] rawBalanceByte = Arrays.copyOfRange(trimmedBalanceBlock, 0, 4);
        byte[] rawLastTransaction = Arrays.copyOfRange(trimmedBalanceBlock, 4, 8);
        int balance = ByteBuffer.wrap(rawBalanceByte).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int lastTransaction = ByteBuffer.wrap(rawLastTransaction).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.mBalance = this.mActivity.getString(R.string.readeractivity_label_rp, balance);
        this.mLastTransaction = this.mActivity.getString(R.string.readeractivity_label_rp, lastTransaction);
    }

    private void processCardNumber(byte[] paramArrayOfbyte) {
        this.mCardNumber = (new String(Arrays.copyOfRange(paramArrayOfbyte, RESPONSE_DATA_START_INDEX, paramArrayOfbyte.length))).trim();
    }

    private void processHistory(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        byte[] trimmedRawHistoryBlocks = Arrays.copyOfRange(paramArrayOfbyte1, RESPONSE_DATA_START_INDEX, paramArrayOfbyte1.length);
        byte[] trimmedRawHistoryFinalBlock = Arrays.copyOfRange(paramArrayOfbyte2, RESPONSE_DATA_START_INDEX, paramArrayOfbyte2.length);
        byte[] rawHistoryAllBlocks = new byte[trimmedRawHistoryBlocks.length + trimmedRawHistoryFinalBlock.length];
        System.arraycopy(trimmedRawHistoryBlocks, 0, rawHistoryAllBlocks, 0, trimmedRawHistoryBlocks.length);
        System.arraycopy(trimmedRawHistoryFinalBlock, 0, rawHistoryAllBlocks, trimmedRawHistoryBlocks.length, trimmedRawHistoryFinalBlock.length);
        mHistories.clear();
        Map<Integer, Station> stationMap = Station.getStationMap();
        for (int i = 0; i < TOTAL_BLOCK_SIZE; i += BLOCK_SIZE) {
            byte[] rawTimestamp = Arrays.copyOfRange(rawHistoryAllBlocks, i, i + 4);
            byte[] rawBalanceChange = Arrays.copyOfRange(rawHistoryAllBlocks, i + 4, i + 8);
            byte[] rawTransactionCode = Arrays.copyOfRange(rawHistoryAllBlocks, i + 8, i + 10);
            byte[] rawStationCode = Arrays.copyOfRange(rawHistoryAllBlocks, i + 10, i + 11);
            byte[] rawCreditType = Arrays.copyOfRange(rawHistoryAllBlocks, i + 12, i + 13);
            String journeyDate = MultripUtil.getJourneyDate(rawTimestamp);
            if (journeyDate != null) {
                History history = new History();
                history.setJourneyDate(journeyDate);
                history.setTimestamp(MultripUtil.getEpochTime(rawTimestamp));
                history.setBalanceChange(MultripUtil.getBalanceChange(rawBalanceChange));
                history.setCredit(MultripUtil.getCreditType(rawCreditType));
                history.setStation(stationMap.get(MultripUtil.getStationCode(rawStationCode)));
                history.setTransaction(MultripUtil.getTransaction(rawTransactionCode));
                this.mHistories.add(history);
            }
        }
    }

    private byte[] readWithoutEncryption(byte[] idm, int blockLength, byte[] serviceCode) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(MAX_ALLOCATED_BYTE_BUFFER);
        //Read Without Encryption Command Code: 0x06
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(READ_WITHOUT_ENCRYPTION_COMMAND);
        byteArrayOutputStream.write(idm);
        //Read Without Encryption Number of Service: 1
        byteArrayOutputStream.write(1);
        //Read Without Encryption Service Code (Based on total service above)
        byteArrayOutputStream.write(serviceCode[1]);
        byteArrayOutputStream.write(serviceCode[0]);
        byteArrayOutputStream.write(blockLength);
        for (int i = 0; i < blockLength; i++) {
            byteArrayOutputStream.write(BLOCK_LIST_COMMAND);
            byteArrayOutputStream.write(i); //Block Index
        }
        byte[] result = byteArrayOutputStream.toByteArray();
        result[0] = (byte)result.length;
        return result;
    }

    private byte[] readWithoutEncryptionByBlock(byte[] idm, int blockLength, byte[] serviceCode) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(MAX_ALLOCATED_BYTE_BUFFER);
        //Read Without Encryption Command Code: 0x06
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(READ_WITHOUT_ENCRYPTION_COMMAND);
        byteArrayOutputStream.write(idm);
        //Read Without Encryption Number of Service: 1
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(serviceCode[1]);
        byteArrayOutputStream.write(serviceCode[0]);
        byteArrayOutputStream.write(1); //Block Length
        byteArrayOutputStream.write(BLOCK_LIST_COMMAND);
        byteArrayOutputStream.write(blockLength); //Block Index
        byte[] result = byteArrayOutputStream.toByteArray();
        result[0] = (byte)result.length;
        return result;
    }
}
