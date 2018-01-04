package com.ozner.nfc;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ozner_67 on 2017/3/28.
 * 邮箱：xinde.zhang@cftcn.com
 * <p>
 * MifareClassic类型卡片读写操作
 */

public class MifareClassicOpera {
    private static final String TAG = "MifareClassicOpera";
    private final byte[] testKeyB = "717573".getBytes(Charset.forName("US-ASCII"));

    private final byte[] DefaultKeyB = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private final byte[] DefaultKeyA = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    //KeyB 可读写控制位数据
    private final byte[] KeyBReadControl = new byte[]{(byte) 0xff, (byte) 0x07, (byte) 0x80, (byte) 0x69};
    //KeyB 只写控制位数据
    private final byte[] keyBNotReadControl = new byte[]{(byte) 0x7f, (byte) 0x07, (byte) 0x88, (byte) 0x40};
    private boolean isReadKey = false;
    private MifareClassic mfc;
    private Tag nfcTag;

    public MifareClassicOpera(Tag tag, boolean isReadKey) {
        nfcTag = tag;
        mfc = MifareClassic.get(tag);
        this.isReadKey = isReadKey;
    }

    /**
     * 连接NFC
     */
    public void connect() {
        try {
            if (mfc.isConnected())
                return;
            mfc.connect();
        } catch (Exception ex) {
            Log.e(TAG, "connect_ex: " + ex.getMessage());
        }
    }

    public void close() {
        try {
            if (mfc != null && mfc.isConnected()) {
                mfc.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "close_Ex: " + ex.getMessage());
        }
    }

    /**
     * 读取数据
     *
     * @param listener
     */
//    public void readAllMifareBlock(INFCReadListener listener) {
//        MifareCard card = new MifareCard();
//
//        try {
//            card.cardId = Convert.ByteArrayToHexString(nfcTag.getId());
//            if (mfc != null) {
//                if (mfc.isConnected()) {
//                    try {
//                        List<MifareCardBlock> resultList = new ArrayList<>();
//                        //循环所有扇区
//                        int sectorCount = mfc.getSectorCount();
//                        for (int i = 0; i < sectorCount; i++) {
//                            int bindex;
//                            int bCount;
//                            if (mfc.authenticateSectorWithKeyA(i, MifareClassic.KEY_NFC_FORUM)
//                                    || mfc.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT)
//                                    || mfc.authenticateSectorWithKeyB(i, DefaultKeyB)
//                                    || mfc.authenticateSectorWithKeyB(i, testKeyB)) {
//                                bindex = mfc.sectorToBlock(i);
//                                bCount = mfc.getBlockCountInSector(i);
//                                //循环一个扇区内的所有block
//                                for (int j = 0; j < bCount; j++) {
//                                    if (!isReadKey) {
//                                        int blockInSector = bindex - bCount * i;
//                                        if (blockInSector == bCount - 1) {
//                                            bindex++;
//                                            continue;
//                                        }
//                                    }
//                                    try {
//                                        MifareCardBlock block = new MifareCardBlock();
//                                        block.blockIndex = bindex;
//                                        block.data = mfc.readBlock(bindex);
//                                        resultList.add(block);
//                                    } catch (Exception ex) {
//
//                                    }
//                                    bindex++;
//                                }
//                            }
//                        }
//                        card.blockDatas = resultList;
//                        if (listener != null) {
//                            listener.onResult(OperationState.Result_Ok, "", card);
//                        }
//                    } catch (Exception ex) {
//                        if (listener != null) {
//                            listener.onResult(OperationState.OtherException, ex.getMessage(), card);
//                        }
//                    }
//                } else {
//                    if (listener != null) {
//                        listener.onResult(OperationState.DisConnected, "NFC is disconnected", card);
//                    }
//                }
//            } else {
//                if (listener != null) {
//                    listener.onResult(OperationState.ObjectNull, "not found mifareclassic object", card);
//                }
//            }
//        } catch (Exception ex) {
//            if (listener != null) {
//                listener.onResult(OperationState.OtherException, ex.getMessage(), card);
//            }
//        }
//    }


    /**
     * 读取MifareClassic卡片扇区,密码block不读取
     *
     * @param blocks   待读取的block索引集合
     * @param listener
     */
    public void readMifareBlock(int[] blocks, INFCReadListener listener) {
        MifareCard readCard = new MifareCard();

        try {
            readCard.cardId = Convert.ByteArrayToHexString(nfcTag.getId());
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

            List<MifareCardBlock> cardBlocks = new ArrayList<>();
            for (int blockIndex : blocks) {
                //密码block跳过，不读取
                if (blockIndex % 4 == 3) {
                    continue;
                }
                int sectorIndex = mfc.blockToSector(blockIndex);
                if (mfc.authenticateSectorWithKeyB(sectorIndex, MifareClassic.KEY_DEFAULT)
                        || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyB)
                        || mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB)) {
                    try {
                        MifareCardBlock block = new MifareCardBlock();
                        block.blockIndex = blockIndex;
                        block.data = mfc.readBlock(blockIndex);
                        cardBlocks.add(block);
                    } catch (Exception ex) {
                        Log.e(TAG, "readMifareBlock_Ex: " + ex.getMessage());
                    }
                }
            }

            readCard.blockDatas = cardBlocks;
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
    public void writeClearCard(INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "1".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写密码卡
     *
     * @param listener
     */
    public void writePasswordCard(INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "2".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "654321".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);

        //keyA
        MifareCardBlock block4 = new MifareCardBlock();
        block4.blockIndex = 4;
        block4.data = DefaultKeyA;
        dataBlocks.add(block4);

        //keyB
        MifareCardBlock block5 = new MifareCardBlock();
        block5.blockIndex = 5;
        block5.data = testKeyB;
        dataBlocks.add(block5);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写机号卡
     *
     * @param deviceNum
     * @param listener
     */
    public void writeDeviceNumCard(final String deviceNum, INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "3".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);

        MifareCardBlock block4 = new MifareCardBlock();
        block4.blockIndex = 4;
        block4.data = deviceNum.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block4);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写测试卡
     *
     * @param listener
     */
    public void writeTestCard(INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "4".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写区域代码卡
     *
     * @param listener
     */
    public void writeAreaCodeCard(final String areaCode, INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        //卡类型
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "5".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        //卡号
        MifareCardBlock block4 = new MifareCardBlock();
        block4.blockIndex = 4;
        block4.data = areaCode.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写区域代码修改卡
     *
     * @param listener
     */
    public void writeChangeAreaCodeCard(final String newAreaCode, INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        //卡类型
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "9".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        //区域代码
        MifareCardBlock block4 = new MifareCardBlock();
        block4.blockIndex = 4;
        block4.data = newAreaCode.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写机型卡
     *
     * @param areaCode
     * @param deviceType
     * @param listener
     */
    public void writeDeviceTypeCard(final String areaCode, final String deviceType, INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        //卡类型
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "14".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        //区域代码
        MifareCardBlock block6 = new MifareCardBlock();
        block6.blockIndex = 6;
        block6.data = areaCode.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block6);

        //机型
        MifareCardBlock block8 = new MifareCardBlock();
        block8.blockIndex = 8;
        block8.data = deviceType.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block8);
        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    /**
     * 写包月卡
     *
     * @param areaCode      区域代码
     * @param deviceType    机型，整数
     * @param cardFaceValue
     * @param cardValue
     * @param listener
     */
    public void writeMonthCard(final String areaCode, final String deviceType, final String cardFaceValue, final String cardValue, final INFCWriteListener listener) {
        List<MifareCardBlock> dataBlocks = new ArrayList<>();
        //包月卡类型
        MifareCardBlock block1 = new MifareCardBlock();
        block1.blockIndex = 1;
        block1.data = "16".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block1);
        //卡号
        MifareCardBlock block2 = new MifareCardBlock();
        block2.blockIndex = 2;
        block2.data = "1233456".getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block2);
        //卡面值
        MifareCardBlock block4 = new MifareCardBlock();
        block4.blockIndex = 4;
        block4.data = cardFaceValue.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block4);
        //包月值
        MifareCardBlock block5 = new MifareCardBlock();
        block5.blockIndex = 5;
        block5.data = cardValue.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block5);
        //区域代码
        MifareCardBlock block6 = new MifareCardBlock();
        block6.blockIndex = 6;
//        block6.data = "939991".getBytes(Charset.forName("US-ASCII"));
        block6.data = areaCode.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block6);

        //机型
        MifareCardBlock block8 = new MifareCardBlock();
        block8.blockIndex = 8;
//        block8.data = "16".getBytes(Charset.forName("US-ASCII"));
        block8.data = deviceType.getBytes(Charset.forName("US-ASCII"));
        dataBlocks.add(block8);

        if (dataBlocks.size() > 0)
            writeMifareData(dataBlocks, listener);
    }

    public interface IMonthCardListener {
        void onResult(MonthCard monthCard);
    }

    /**
     * 读取包月卡信息
     *
     * @param listener
     */
    public void readMonthCard(final IMonthCardListener listener) {
        readMifareBlock(new int[]{1, 2, 4, 5, 6, 8}, new INFCReadListener() {
            @Override
            public void onResult(int state, String errmsg, MifareCard card) {
                if (state == OperationState.Result_Ok) {
                    if (card != null && card.blockDatas.size() > 1) {
                        MonthCard monthCard = new MonthCard();
                        for (MifareClassicOpera.MifareCardBlock block : card.blockDatas) {
//                            Log.e(TAG, "onResult: blockData:" + new String(block.data));
                            if (block.blockIndex == 1) {
                                monthCard.cardType = new String(block.data);
                            }
                            if (block.blockIndex == 2) {
                                monthCard.cardNumber = new String(block.data);
                            }
                            if (block.blockIndex == 4) {
                                monthCard.cardFaceValue = new String(block.data);
                            }
                            if (block.blockIndex == 5) {
                                monthCard.cardValue = new String(block.data);
                            }
                            if (block.blockIndex == 6) {
                                monthCard.areaId = new String(block.data);
                            }
                            if (block.blockIndex == 8) {
                                monthCard.deviceType = new String(block.data);
                            }
                        }
                        if (listener != null) {
                            listener.onResult(monthCard);
                        }
                        Log.e(TAG, "onResult: FaceValue:" + monthCard.cardFaceValue + " , cardValue:" + monthCard.cardValue);
                    }
                }
            }
        });
    }


    /**
     * 写卡
     *
     * @param dataBlocks
     * @param listener
     */
    private void writeMifareData(final List<MifareCardBlock> dataBlocks, final INFCWriteListener listener) {

        try {
            if (mfc != null) {
                if (mfc.isConnected()) {
                    try {
                        for (int i = 0; i < dataBlocks.size(); i++) {
                            MifareCardBlock dataBlock = dataBlocks.get(i);
                            //秘钥block,是默认秘钥的话，设置为指定秘钥
                            int sectorIndex = mfc.blockToSector(dataBlock.blockIndex);
//                            Log.e(TAG, "writeMifareData_sectorIndex: " + sectorIndex);
//                            Log.e(TAG, "checkA: " + mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_NFC_FORUM));
//                            Log.e(TAG, "checkA_: " + mfc.authenticateSectorWithKeyA(sectorIndex, DefaultKeyA));
//                            Log.e(TAG, "checkB: " + mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyB));
//                            Log.e(TAG, "checkB: " + mfc.authenticateSectorWithKeyB(sectorIndex, MifareClassic.KEY_DEFAULT));
//                            Log.e(TAG, "checkB_: " + mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB));
                            //只在KeyB为默认秘钥的时候写入指定秘钥
                            if (mfc.authenticateSectorWithKeyB(sectorIndex, MifareClassic.KEY_DEFAULT)) {
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                buffer.put(DefaultKeyA);
                                //不可读控制位参数
//                                buffer.put(new byte[]{(byte) 0x7f, (byte) 0x07, (byte) 0x88, (byte) 0x40});
                                //可读控制位参数
                                buffer.put(keyBNotReadControl);
                                buffer.put(testKeyB);
                                Log.e(TAG, "writeMifareData_sceret: " + Convert.ByteArrayToHexString(buffer.array()));
                                mfc.writeBlock(sectorIndex * 4 + 3, buffer.array());
                            }

                            //如果是秘钥block 则跳过
                            if (dataBlock.blockIndex % 4 == 3) {
                                Log.e(TAG, "writeMifareData: 下一个block");
                                continue;
                            }

                            final byte[] write = new byte[MifareClassic.BLOCK_SIZE];//每一块最大存储字节数
                            //后边补0
//                                    for (int j = 0; j < MifareClassic.BLOCK_SIZE; j++) {
//                                        if (j < dataBlock.data.length) {
//                                            write[j] = dataBlock.data[j];
//                                        }
//                                        else {
//                                            write[j] = 0;
//                                        }
//                                    }
                            //前边补0
                            for (int j = 0; j < MifareClassic.BLOCK_SIZE; j++) {
                                if (j < MifareClassic.BLOCK_SIZE - dataBlock.data.length)
                                    write[j] = (byte) 0x30;
                                else {
                                    write[j] = dataBlock.data[j + dataBlock.data.length - MifareClassic.BLOCK_SIZE];
                                }
                            }

                            if (mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_NFC_FORUM)
                                    || mfc.authenticateSectorWithKeyB(sectorIndex, DefaultKeyB)
                                    || mfc.authenticateSectorWithKeyB(sectorIndex, testKeyB)) {
                                Log.e(TAG, "写入数据: blockIndex:" + dataBlock.blockIndex);
                                Log.e(TAG, "写入数据: data:" + Convert.ByteArrayToHexString(write));
                                mfc.writeBlock(dataBlock.blockIndex, write);
                            }
                        }
                        if (listener != null) {
                            listener.onResult(OperationState.Result_Ok, "");
                        }
                    } catch (Exception ex) {
                        if (listener != null) {
                            listener.onResult(OperationState.OtherException, ex.getMessage());
                        }
                    }
                } else {
                    mfc.connect();
                    if (!mfc.isConnected())
                        if (listener != null) {
                            listener.onResult(OperationState.DisConnected, "NFC is disconnected");
                        }
                }
            } else {
                if (listener != null) {
                    listener.onResult(OperationState.ObjectNull, "not found mifareclassic object");
                }
            }
        } catch (Exception ex) {
            if (listener != null) {
                listener.onResult(OperationState.OtherException, ex.getMessage());
            }
        }
    }

    public static class MonthCard implements Parcelable {
        public String cardType;
        public String cardNumber;
        public String cardFaceValue;
        public String cardValue;
        public String areaId;
        public String deviceType;

        @Override
        public String toString() {
            return "block1:" + cardType + "\nblock2:" + cardNumber + "\nblock4:" + cardFaceValue + "\nblock5:" + cardValue
                    + "\nblock6:" + areaId + "\nblock8:" + deviceType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cardType);
            dest.writeString(this.cardNumber);
            dest.writeString(this.cardFaceValue);
            dest.writeString(this.cardValue);
            dest.writeString(this.areaId);
            dest.writeString(this.deviceType);
        }

        public MonthCard() {
        }

        protected MonthCard(Parcel in) {
            this.cardType = in.readString();
            this.cardNumber = in.readString();
            this.cardFaceValue = in.readString();
            this.cardValue = in.readString();
            this.areaId = in.readString();
            this.deviceType = in.readString();
        }

        public static final Parcelable.Creator<MonthCard> CREATOR = new Parcelable.Creator<MonthCard>() {
            @Override
            public MonthCard createFromParcel(Parcel source) {
                return new MonthCard(source);
            }

            @Override
            public MonthCard[] newArray(int size) {
                return new MonthCard[size];
            }
        };
    }

    /**
     * Created by ozner_67 on 2017/3/28.
     * 邮箱：xinde.zhang@cftcn.com
     */

    public static class MifareCardBlock implements Parcelable {
        public int blockIndex;
        public byte[] data;

        public MifareCardBlock() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.blockIndex);
            dest.writeByteArray(this.data);
        }

        protected MifareCardBlock(Parcel in) {
            this.blockIndex = in.readInt();
            this.data = in.createByteArray();
        }

        public static final Creator<MifareCardBlock> CREATOR = new Creator<MifareCardBlock>() {
            @Override
            public MifareCardBlock createFromParcel(Parcel source) {
                return new MifareCardBlock(source);
            }

            @Override
            public MifareCardBlock[] newArray(int size) {
                return new MifareCardBlock[size];
            }
        };
    }

    /**
     * Created by ozner_67 on 2017/3/28.
     * 邮箱：xinde.zhang@cftcn.com
     */

    public static class MifareCard implements Parcelable {
        public String cardId;
        public List<MifareCardBlock> blockDatas;

        public MifareCard() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cardId);
            dest.writeTypedList(this.blockDatas);
        }

        protected MifareCard(Parcel in) {
            this.cardId = in.readString();
            this.blockDatas = in.createTypedArrayList(MifareCardBlock.CREATOR);
        }

        public static final Creator<MifareCard> CREATOR = new Creator<MifareCard>() {
            @Override
            public MifareCard createFromParcel(Parcel source) {
                return new MifareCard(source);
            }

            @Override
            public MifareCard[] newArray(int size) {
                return new MifareCard[size];
            }
        };
    }

    /**
     * Created by ozner_67 on 2017/3/28.
     * 邮箱：xinde.zhang@cftcn.com
     */

    public interface INFCWriteListener {
        void onResult(int state, String errmsg);
    }

    /**
     * Created by ozner_67 on 2017/3/28.
     * 邮箱：xinde.zhang@cftcn.com
     */

    public interface INFCReadListener {
        void onResult(int state, String errmsg, MifareCard card);
    }
}
