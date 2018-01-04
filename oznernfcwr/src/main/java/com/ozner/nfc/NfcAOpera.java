package com.ozner.nfc;

import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by ozner_67 on 2017/3/28.
 * 邮箱：xinde.zhang@cftcn.com
 */

public class NfcAOpera {
    private static final String TAG = "NfcAOpera";
    private Tag mTag;
    private NfcA mnfcA;

    public static NfcAOpera get(Tag tag) {
        if (tag != null) {
            return new NfcAOpera(tag);
        } else {
            return null;
        }
    }

    private NfcAOpera(Tag tag) {
        this.mTag = tag;
        this.mnfcA = NfcA.get(tag);
        Log.e(TAG, "NfcAOpera: aqta:" + Convert.ByteArrayToHexString(mnfcA.getAtqa()));
    }

    public void connect() {
        try {
            if (mnfcA != null) {
                mnfcA.connect();
            }
        } catch (Exception ex) {
            Log.e(TAG, "connect_ex: " + ex.getMessage());
        }
    }

    public void close() {
        try {
            mnfcA.close();
        } catch (Exception ex) {
            Log.e(TAG, "close_Ex: " + ex.getMessage());
        }
    }

    public boolean isConnected() {
        return mnfcA.isConnected();
    }

    public byte[] personalOption() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(mTag.getId().length + 2);
        buffer.put((byte) 0x43);
        buffer.put(mTag.getId());
        buffer.put((byte) 0x01);
        return mnfcA.transceive(buffer.array());
    }

    public boolean authenWithKeyA(int sector, byte[] key) {
        return authenticate(sector, key, true);
    }

    public boolean authenWithKeyB(int sector, byte[] key) {
        return authenticate(sector, key, false);
    }

    private boolean authenticate(int sector, byte[] key, boolean keyA) {
        byte[] cmd = new byte[12];
        if (keyA) {
            cmd[0] = 0x60;
        } else {
            cmd[1] = 0x61;
        }

        cmd[1] = (byte) (sector * 4);

        byte[] uid = mTag.getId();
        System.arraycopy(uid, uid.length - 4, cmd, 2, 4);

        System.arraycopy(key, 0, cmd, 6, 6);
        try {
            byte[] authRes = mnfcA.transceive(cmd);
            if (authRes != null) {
                Log.e(TAG, "authenticate_res(isKeyA:" + keyA + "):" + Convert.ByteArrayToHexString(authRes));
                return true;
            }
        } catch (Exception ex) {
            Log.e(TAG, "authenticate_Ex: " + ex.getMessage());
        }
        return false;
    }

    public byte[] readBlock(int blockIndex) throws IOException {
        byte[] cmd = {0x30, (byte) blockIndex};
        return mnfcA.transceive(cmd);
    }

    public byte[] fastReadBlock(int startIndex, int endIndex) throws IOException {
        byte[] cmd = {0x3a, (byte) startIndex, (byte) endIndex};
        return mnfcA.transceive(cmd);
    }


    public void writeBlock(int blockIndex, byte[] data) throws IOException {
        if (data.length != 16) {
            throw new IllegalArgumentException("must write 16-bytes");
        }

        byte[] cmd = new byte[data.length + 2];
        cmd[0] = (byte) 0xA2;//MF write command
        cmd[1] = (byte) blockIndex;
        System.arraycopy(data, 0, cmd, 2, data.length);
        Log.e(TAG, "writeBlock: 写cmd:"+Convert.ByteArrayToHexString(cmd));
        byte[] result = mnfcA.transceive(cmd);

        if (result != null) {
            Log.e(TAG, "writeBlock_result: " + Convert.ByteArrayToHexString(result));
            Log.e(TAG, "writeBlock_result: " + new String(result, "UTF-8"));
            byte[] b0res = mnfcA.transceive(new byte[]{(byte)0xb0,(byte)blockIndex});
            Log.e(TAG, "writeBlock: b0命令返回:"+Convert.ByteArrayToHexString(b0res));
        } else {
            Log.e(TAG, "writeBlock: result is null");
        }
    }
}
