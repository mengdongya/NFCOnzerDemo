package com.ozner.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import com.ozner.nfc.CardBean.AreaCodeCard;
import com.ozner.nfc.CardBean.CipherCard;
import com.ozner.nfc.CardBean.ClearCard;
import com.ozner.nfc.CardBean.DataBlock;
import com.ozner.nfc.CardBean.DeviceNumberCard;
import com.ozner.nfc.CardBean.DeviceTypeCard;
import com.ozner.nfc.CardBean.MonthCard;
import com.ozner.nfc.CardBean.OznerCard;
import com.ozner.nfc.CardBean.PersonalCard;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 */

public class OznerNfcManager {
    private static final String TAG = "OznerNfcManager";

    private final byte[] testKeyB = "717573".getBytes(Charset.forName("US-ASCII"));

    private final byte[] DefaultKeyB = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private final byte[] DefaultKeyA = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    //KeyB 可读写控制位数据
    private final byte[] KeyBReadControl = new byte[]{(byte) 0xff, (byte) 0x07, (byte) 0x80, (byte) 0x69};
    //KeyB 只写控制位数据
    private final byte[] keyBNotReadControl = new byte[]{(byte) 0x7f, (byte) 0x07, (byte) 0x88, (byte) 0x40};
    private MifareClassic mfc;
    private Tag nfcTag;
    private WeakReference<Activity> mActivity;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private NfcAdapter nfcAdapter;
    private String[][] techListsArray;
    private IOnzerNfcOpera oznerOpera;

    public interface IOnzerNfcOpera {
        void dealWork();
    }

    public OznerNfcManager(Activity activity) {
        this.mActivity = new WeakReference<Activity>(activity);
    }

    /**
     * 设置处理方法
     *
     * @param opera
     */
    public void setOznerNfcOpera(IOnzerNfcOpera opera) {
        if (opera != null) {
            oznerOpera = opera;
        }
    }

    public Tag getNfcTag() {
        return nfcTag;
    }

    public void onCreate() {
        pendingIntent = PendingIntent.getActivity(mActivity.get(), 0,
                new Intent(mActivity.get(), mActivity.get().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        techListsArray = new String[][]{new String[]{MifareClassic.class.getName(), IsoDep.class.getName()}};
        IntentFilter ndef = new IntentFilter();
        ndef.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFiltersArray = new IntentFilter[]{ndef};
        nfcAdapter = NfcAdapter.getDefaultAdapter(mActivity.get());

//        if (hasNfc() && !isNfcEnable()) {
//            openNfc();
//        }
    }


    /**
     * 是否支持NFC
     *
     * @return
     */
    public boolean hasNfc() {
        return nfcAdapter != null;
    }

    /**
     * nfc是否启用
     *
     * @return
     */
    public boolean isNfcEnable() {
        if (hasNfc()) {
            return nfcAdapter.isEnabled();
        } else {
            return false;
        }
    }

    /**
     * 打开NFC
     */
    public void openNfc() {
        if (hasNfc()) {
            Intent nfcIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
            mActivity.get().startActivity(nfcIntent);
        }
    }

    public void onReusme() throws IOException {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(mActivity.get(), pendingIntent, intentFiltersArray, techListsArray);
            if (mActivity.get().getIntent().getAction() != null) {
                if (NfcAdapter.ACTION_TECH_DISCOVERED.endsWith(mActivity.get().getIntent().getAction())) {
                    onNewIntent(mActivity.get().getIntent());
                }
            }
        }
    }

    public void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(mActivity.get());
        }
    }

    /**
     * 判断设备是否支持MifareClassic
     *
     * @param intent
     * @return
     */
    public static boolean isDeviceSupport(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag == null) {
            return false;
        }
        String[] techList = tag.getTechList();
        boolean isSupport = false;
        if (techList != null && techList.length > 0) {
            for (String tech : techList) {
                if (tech.contains("MifareClassic")) {
                    isSupport = true;
                    break;
                }
            }
        }
        return isSupport;
    }

    /**
     * 处理扫描到的tag
     *
     * @param intent
     */
    public void onNewIntent(Intent intent) throws IOException {
        if (intent != null) {
            nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mfc = MifareClassic.get(nfcTag);
            if (mfc == null) {
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        connect();
                        // TODO: 2017/4/18 处理扫描到的tag
                        if (oznerOpera != null) {
                            oznerOpera.dealWork();
                        }
                        close();
                    } catch (Exception ex) {
                        Log.e(TAG, "run_Ex: " + ex.getMessage());
                    }
                }
            }).start();

        }
    }

    /**
     * 连接
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        if (mfc.isConnected())
            return;
        mfc.connect();
    }

    /**
     * 关闭连接
     *
     * @throws IOException
     */
    private void close() throws IOException {
        if (mfc != null && mfc.isConnected()) {
            mfc.close();
        }
    }

    public interface IMonthCardListener {
        void onResult(int state, String errmsg, MonthCard monthCard);
    }

    /**
     * 读取包月卡数据
     *
     * @param listener
     */
    public void readMonthCard(final IMonthCardListener listener) {
        readBlocksData(new int[]{0, 1, 4, 5, 6, 8, 9}, false, new INfcReaderListener() {
            @Override
            public void onResult(int state, String errmsg, OznerCard card) {
                if (state == OperationState.Result_Ok) {
                    MonthCard monthCard = new MonthCard();
                    if (!card.blockDatas.isEmpty()) {
                        if (card.blockDatas.containsKey(1)) {
                            monthCard.cardType = new String(card.blockDatas.get(1).data);
                        }
                        if (card.blockDatas.containsKey(0)) {
                            monthCard.cardNumber = card.cardId;
                        }
                        if (card.blockDatas.containsKey(4)) {
                            monthCard.cardFaceValue = new String(card.blockDatas.get(4).data);
                        }
                        if (card.blockDatas.containsKey(5)) {
                            monthCard.cardValue = new String(card.blockDatas.get(5).data);
                        }
                        if (card.blockDatas.containsKey(6)) {
                            monthCard.areaCode = new String(card.blockDatas.get(6).data);
                        }
                        if (card.blockDatas.containsKey(8)) {
                            monthCard.deviceType = new String(card.blockDatas.get(8).data);
                        }

                        if (card.blockDatas.containsKey(9)) {
                            monthCard.secAreaCode = new String(card.blockDatas.get(9).data);
                        }
                    }
                    if (listener != null) {
                        listener.onResult(state, errmsg, monthCard);
                    }
                } else {
                    if (listener != null) {
                        listener.onResult(state, errmsg, null);
                    }
                }
            }
        });
    }

    /**
     * 读取所有数据
     *
     * @param listener
     */
    public void readAllBlocksData(INfcReaderListener listener) {
        int[] blocks = new int[64];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = i;
        }
        readBlocksData(blocks, true, listener);
    }

    /**
     * 读取block数据
     *
     * @param blocks    需要读取的block的索引
     * @param isReadKey 是否读取秘钥block
     * @param listener
     */
    private void readBlocksData(int[] blocks, boolean isReadKey, INfcReaderListener listener) {
        OznerCard readCard = new OznerCard();
        try {
            if (nfcTag == null) {
                if (listener != null) {
                    listener.onResult(OperationState.ObjectNull, "tag was lost", readCard);
                }
                return;
            }
            readCard.cardId = ByteArrayToHexString(nfcTag.getId());
            if (mfc == null) {
                if (listener != null) {
                    listener.onResult(OperationState.ObjectNull, "not found mifareclassic object", readCard);
                }
                return;
            }
            if (!mfc.isConnected()) {
                if (listener != null) {
                    listener.onResult(OperationState.DisConnected, "NFC is disconnected", readCard);
                }
                return;
            }

            readCard.blockDatas = new HashMap<>();
            for (int blockIndex : blocks) {
                if (!isReadKey) {
                    //密码block跳过，不读取
                    if (blockIndex % 4 == 3) {
                        continue;
                    }
                }
                int sectorIndex = mfc.blockToSector(blockIndex);
                if (mfc.authenticateSectorWithKeyB(sectorIndex, MifareClassic.KEY_DEFAULT)
                        || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyB)
                        || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyA)
                        || mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB)) {
                    try {
                        DataBlock block = new DataBlock();
                        block.blockIndex = blockIndex;
                        block.data = mfc.readBlock(blockIndex);
                        readCard.blockDatas.put(blockIndex, block);
                    } catch (Exception ex) {
                        Log.e(TAG, "readBlocksData_Ex: " + ex.getMessage());
                    }
                }
            }

            if (listener != null) {
                listener.onResult(OperationState.Result_Ok, "", readCard);
            }

        } catch (Exception ex) {
            if (listener != null) {
                listener.onResult(OperationState.OtherException, ex.getMessage(), null);
            }
        }
    }

    /**
     * 写清除卡
     *
     * @param listener
     */
    public void writeClearCard(INfcWriterListener listener) {
        ClearCard card = new ClearCard();
        card.cardType = "1";
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }

    /**
     * 写密码卡
     *
     * @param listener
     */
    public void writeCipherCard(INfcWriterListener listener) {
        CipherCard card = new CipherCard();
        card.cardType = "2";
        card.keyA = testKeyB;
        card.keyB = testKeyB;
        card.cardNumber = "0";
        writeCipherCard(OznerCardFactory.card2List(card), listener);
    }

    /**
     * 写机号卡
     *
     * @param deviceNum
     * @param listener
     */
    public void writeDeviceNumCard(final String deviceNum, INfcWriterListener listener) {
        DeviceNumberCard card = new DeviceNumberCard();
        card.cardType = "3";
        card.deviceNumber = deviceNum;
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }


    /**
     * 写测试卡
     *
     * @param listener
     */
    public void writeTestCard(INfcWriterListener listener) {
        ClearCard card = new ClearCard();
        card.cardType = "4";
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }

    /**
     * 写区域代码卡
     *
     * @param areaCode
     * @param listener
     */
    public void writeAreaCodeCard(final String areaCode, final INfcWriterListener listener) {
        AreaCodeCard card = new AreaCodeCard();
        card.cardType = "5";
        card.areaCode = areaCode;
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }


    /**
     * 写区域代码修改卡
     *
     * @param newAreaCode
     * @param listener
     */
    public void writeChangeAreaCodeCard(final String newAreaCode, final INfcWriterListener listener) {
        AreaCodeCard card = new AreaCodeCard();
        card.cardType = "9";
        card.areaCode = newAreaCode;
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }

    /**
     * 写通用机型卡
     *
     * @param areaCode
     * @param deviceType
     * @param listener
     */
    public void writeDeviceTypeCard(final String areaCode, final String deviceType, INfcWriterListener listener) {
        DeviceTypeCard deviceCard = new DeviceTypeCard();
        deviceCard.cardType = "14";
        deviceCard.areaCode = areaCode;
        deviceCard.deviceType = deviceType;
        deviceCard.cardNumber = "0";
        writeData(OznerCardFactory.card2List(deviceCard), listener);
    }

    /**
     * 写包月机型卡
     *
     * @param deviceType
     * @param listener
     */
    public void writeMonthDeviceType(final String deviceType, INfcWriterListener listener) {
        DeviceNumberCard card = new DeviceNumberCard();
        card.cardType = "19";
        card.deviceNumber = deviceType;
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }

    /**
     * 写个人卡
     *
     * @param cardFace
     * @param cardValue
     * @param areaCode
     * @param listener
     */
    public void writePersonalCard(final String cardFace, final String cardValue, final String areaCode, INfcWriterListener listener) {
        PersonalCard card = new PersonalCard();
        card.cardType = "13";
        card.cardFace = cardFace;
        card.cardValue = cardValue;
        card.areaCode = areaCode;
        card.cardNumber = "0";
        writeData(OznerCardFactory.card2List(card), listener);
    }

    /**
     * 写包月卡
     *
     * @param areaCode
     * @param deviceType
     * @param cardFace
     * @param cardValue
     * @param listener
     */
    public void writeMonthCard(final String cardFace, final String cardValue, final String areaCode, final String deviceType, final String secAreaCode, final INfcWriterListener listener) {
        MonthCard monthCard = new MonthCard();
//        card.cardNumber = "0";
        monthCard.cardType = "16";
        monthCard.cardFaceValue = cardFace;
        monthCard.cardValue = cardValue;
        monthCard.areaCode = areaCode;
        monthCard.deviceType = deviceType;
        monthCard.secAreaCode = secAreaCode;
        writeData(OznerCardFactory.card2List(monthCard), listener);
    }

    /**
     * 清除包月卡
     * 转为白卡
     *
     * @param listener
     */
    public void clearMonthCard(final INfcWriterListener listener) {
        MonthCard monthCard = new MonthCard();
        monthCard.cardType = "-1";
        monthCard.cardFaceValue = "0";
        monthCard.cardValue = "0";
        monthCard.areaCode = "0";
        monthCard.deviceType = "0";
        monthCard.secAreaCode = "0";
        monthCard.cardNumber = "0";
        writeData(OznerCardFactory.card2List(monthCard), listener);
    }

    /**
     * 写密码卡专用
     *
     * @param dataBlocks
     * @param listener
     */
    private void writeCipherCard(final List<DataBlock> dataBlocks, final INfcWriterListener listener) {
        try {
            if (mfc == null) {
                if (listener != null) {
                    listener.onResult(OperationState.ObjectNull, "tag was lost");
                }
                return;
            }
            if (!mfc.isConnected()) {
                if (listener != null) {
                    listener.onResult(OperationState.DisConnected, "disConnected");
                }
                return;
            }
            try {
                for (DataBlock block : dataBlocks) {
                    int sectorIndex = mfc.blockToSector(block.blockIndex);
                    //将秘钥修改为defaultKeyA
                    if (mfc.authenticateSectorWithKeyB(sectorIndex, MifareClassic.KEY_DEFAULT)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyA)) {
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        buffer.put(DefaultKeyA);
                        buffer.put(KeyBReadControl);
                        buffer.put(DefaultKeyA);
                        Log.e(TAG, "writeMifareData_sceret: " + Convert.ByteArrayToHexString(buffer.array()));
                        mfc.writeBlock(sectorIndex * 4 + 3, buffer.array());
                    }

                    //如果是秘钥block 则跳过
                    if (block.blockIndex % 4 == 3) {
                        continue;
                    }

                    //数据长度不足16字节的，前边补(byte)0
                    final byte[] writeData = new byte[MifareClassic.BLOCK_SIZE];
                    for (int j = 0; j < MifareClassic.BLOCK_SIZE; j++) {
                        if (j < MifareClassic.BLOCK_SIZE - block.data.length)
                            writeData[j] = (byte) 0x30;
                        else {
                            writeData[j] = block.data[j + block.data.length - MifareClassic.BLOCK_SIZE];
                        }
                    }
                    //写入block
                    if (mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_NFC_FORUM)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyB)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB)) {
                        Log.e(TAG, "写入数据: blockIndex:" + block.blockIndex);
                        Log.e(TAG, "写入数据: data:" + Convert.ByteArrayToHexString(writeData));
                        mfc.writeBlock(block.blockIndex, writeData);
                    }
                }
                if (listener != null) {
                    listener.onResult(OperationState.Result_Ok, "OK");
                }

            } catch (Exception ex) {
                if (listener != null) {
                    listener.onResult(OperationState.OtherException, ex.getMessage());
                }
            }

        } catch (Exception ex) {
            if (listener != null) {
                listener.onResult(OperationState.OtherException, ex.getMessage());
            }
        }
    }

    /**
     * 写入数据
     *
     * @param dataBlocks 需要写入的block列表
     * @param listener
     */
    private void writeData(final List<DataBlock> dataBlocks, final INfcWriterListener listener) {
        try {
            if (mfc == null) {
                if (listener != null) {
                    listener.onResult(OperationState.ObjectNull, "tag was lost");
                }
                return;
            }
            if (!mfc.isConnected()) {
                if (listener != null) {
                    listener.onResult(OperationState.DisConnected, "disConnected");
                }
                return;
            }
            try {
                for (DataBlock block : dataBlocks) {
                    int sectorIndex = mfc.blockToSector(block.blockIndex);
                    //如果秘钥是默认秘钥，修改秘钥为指定秘钥
                    if (mfc.authenticateSectorWithKeyB(sectorIndex, MifareClassic.KEY_DEFAULT)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyA)) {
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        buffer.put(DefaultKeyA);
                        buffer.put(KeyBReadControl);
                        buffer.put(testKeyB);
                        Log.e(TAG, "writeMifareData_sceret: " + Convert.ByteArrayToHexString(buffer.array()));
                        mfc.writeBlock(sectorIndex * 4 + 3, buffer.array());
                    }

                    //如果是秘钥block 则跳过
                    if (block.blockIndex % 4 == 3) {
                        continue;
                    }

                    //数据长度不足16字节的，前边补(byte)0
                    final byte[] writeData = new byte[MifareClassic.BLOCK_SIZE];
                    for (int j = 0; j < MifareClassic.BLOCK_SIZE; j++) {
                        if (j < MifareClassic.BLOCK_SIZE - block.data.length)
                            writeData[j] = (byte) 0x30;
                        else {
                            writeData[j] = block.data[j + block.data.length - MifareClassic.BLOCK_SIZE];
                        }
                    }
                    //写入block
                    if (mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_NFC_FORUM)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyB)
                            || mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB)) {
//                        Log.e(TAG, "写入数据: blockIndex:" + block.blockIndex);
//                        Log.e(TAG, "写入数据: data:" + Convert.ByteArrayToHexString(writeData));
                        mfc.writeBlock(block.blockIndex, writeData);
                    }
                }
                if (listener != null) {
                    listener.onResult(OperationState.Result_Ok, "OK");
                }

            } catch (Exception ex) {
                if (listener != null) {
                    listener.onResult(OperationState.OtherException, ex.getMessage());
                }
            }

        } catch (Exception ex) {
            if (listener != null) {
                listener.onResult(OperationState.OtherException, ex.getMessage());
            }
        }
    }


    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public interface INfcWriterListener {
        void onResult(int state, String errmsg);
    }

    public interface INfcReaderListener {
        void onResult(int state, String errmsg, OznerCard card);
    }
}
