package com.example.kmtreader.helper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.widget.Toast;

import com.example.kmtreader.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class NfcHelper {

    public static final long SERIAL_VERSION_UID = 0L;

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
    private static final int SIXTEEN_BLOCK = 16;

    private Activity mActivity;
    private Intent mFirstIntent;
    private IntentFilter[] mIntentFilters;
    private String mLastTransaction = "";
    private PendingIntent mPendingIntent;

    private String mBalance = "";
    private String mCardNumber = "";

    private String[][] mTechList;

    //private ArrayList<History> mHistories = new ArrayList<History>();

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

    /*public ArrayList<History> getHistories() {
        IncrementalChange incrementalChange = $change;
        return (incrementalChange != null) ? (ArrayList<History>)incrementalChange.access$dispatch("getHistories.()Ljava/util/ArrayList;", new Object[] { this }) : this.mHistories;
    }*/

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
                //arrayOfByte2 = nfcF.transceive(arrayOfByte2);
                byte[] balanceResult = nfcF.transceive(balanceCommand);
                processBalance(balanceResult);

                if (cardNumberSystemCode.length != 0) {
                    byte[] cardNumberCommand = readWithoutEncryption(targetIdm, SINGLE_BLOCK, cardNumberSystemCode);
                    byte[] cardNumberResult = nfcF.transceive(cardNumberCommand);
                    processCardNumber(cardNumberResult);
                }
                if (historySystemCode.length != 0) {
                    byte[] historyCommand = readWithoutEncryption(targetIdm, 15, historySystemCode);
                    byte[] historyFinalBlockCommand = readWithoutEncryptionByBlock(targetIdm, 15, historySystemCode);
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

    private void processBalance(byte[] balanceRawByte) {
        byte[] unknownByte = Arrays.copyOfRange(balanceRawByte, 13, balanceRawByte.length);
        byte[] balanceByte = Arrays.copyOfRange(unknownByte, 0, 4);
        byte[] arrayOfByte = Arrays.copyOfRange(unknownByte, 4, 8);
        int balance = ByteBuffer.wrap(balanceByte).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int lastTransaction = ByteBuffer.wrap(arrayOfByte).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.mBalance = this.mActivity.getString(R.string.readeractivity_label_rp, balance);
        this.mLastTransaction = this.mActivity.getString(R.string.readeractivity_label_rp, lastTransaction);
    }

    private void processCardNumber(byte[] paramArrayOfbyte) {
        this.mCardNumber = (new String(Arrays.copyOfRange(paramArrayOfbyte, 13, paramArrayOfbyte.length))).trim();
    }

    private void processHistory(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        byte[] trimmedRawHistoryBlocks = Arrays.copyOfRange(paramArrayOfbyte1, 13, paramArrayOfbyte1.length);
        byte[] trimmedRawHistoryFinalBlock = Arrays.copyOfRange(paramArrayOfbyte2, 13, paramArrayOfbyte2.length);
        byte[] rawHistoryAllBlocks = new byte[trimmedRawHistoryBlocks.length + trimmedRawHistoryFinalBlock.length];
        System.arraycopy(trimmedRawHistoryBlocks, 0, rawHistoryAllBlocks, 0, trimmedRawHistoryBlocks.length);
        System.arraycopy(trimmedRawHistoryFinalBlock, 0, rawHistoryAllBlocks, trimmedRawHistoryBlocks.length, trimmedRawHistoryFinalBlock.length);
        //mHistories.clear();
        for (int i = 0; i < 256; i += 16) {
            byte[] rawTimestamp = Arrays.copyOfRange(rawHistoryAllBlocks, i, i + 4);
            byte[] rawBalanceChange = Arrays.copyOfRange(rawHistoryAllBlocks, i + 4, i + 8);
            byte[] rawTransactionCode = Arrays.copyOfRange(rawHistoryAllBlocks, i + 8, i + 10);
            byte[] rawStationCode = Arrays.copyOfRange(rawHistoryAllBlocks, i + 10, i + 11);
            byte[] rawCreditType = Arrays.copyOfRange(rawHistoryAllBlocks, i + 12, i + 13);
            /*String journeyDate = MultripUtil.getJourneyDate(rawTimestamp);
            if (str != null) {
                History history = new History();
                history.setJourneyDate(journeyDate);
                history.setTimestamp(MultripUtil.getEpochTime(rawTimestamp));
                history.setBalanceChange(MultripUtil.getBalanceChange(rawBalanceChange));
                history.setCredit(MultripUtil.getCreditType(rawCreditType));
                history.setStationCode(MultripUtil.getStationCode(rawStationCode));
                history.setTransactionCode(MultripUtil.getTransactionCode(rawTransactionCode));
                this.mHistories.add(history);
            }*/
        }
    }

    private byte[] readWithoutEncryption(byte[] idm, int blockLength, byte[] serviceCode) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
        //Read Without Encryption Command Code: 0x06
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(6);
        byteArrayOutputStream.write(idm);
        //Read Without Encryption Number of Service: 1
        byteArrayOutputStream.write(1);
        //Read Without Encryption Service Code (Based on total service above)
        byteArrayOutputStream.write(serviceCode[1]);
        byteArrayOutputStream.write(serviceCode[0]);
        byteArrayOutputStream.write(blockLength);
        for (int i = 0; i < blockLength; i++) {
            byteArrayOutputStream.write(128);
            byteArrayOutputStream.write(i);
        }
        byte[] result = byteArrayOutputStream.toByteArray();
        result[0] = (byte)result.length;
        return result;
    }

    private byte[] readWithoutEncryptionByBlock(byte[] idm, int blockLength, byte[] serviceCode) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(6);
        byteArrayOutputStream.write(idm);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(serviceCode[1]);
        byteArrayOutputStream.write(serviceCode[0]);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(128);
        byteArrayOutputStream.write(blockLength);
        byte[] result = byteArrayOutputStream.toByteArray();
        result[0] = (byte)result.length;
        return result;
    }
}
