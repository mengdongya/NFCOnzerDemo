package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 *
 * 卡片block
 */

public class DataBlock implements Parcelable {
    public int blockIndex;
    public byte[] data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.blockIndex);
        dest.writeByteArray(this.data);
    }

    public DataBlock() {
    }

    protected DataBlock(Parcel in) {
        this.blockIndex = in.readInt();
        this.data = in.createByteArray();
    }

    public static final Parcelable.Creator<DataBlock> CREATOR = new Parcelable.Creator<DataBlock>() {
        @Override
        public DataBlock createFromParcel(Parcel source) {
            return new DataBlock(source);
        }

        @Override
        public DataBlock[] newArray(int size) {
            return new DataBlock[size];
        }
    };
}
