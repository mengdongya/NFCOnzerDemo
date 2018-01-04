package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 *
 * 卡片
 */

public class OznerCard implements Parcelable {
    public String cardId;
    public HashMap<Integer, DataBlock> blockDatas;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardId);
        dest.writeSerializable(this.blockDatas);
    }

    public OznerCard() {
    }

    protected OznerCard(Parcel in) {
        this.cardId = in.readString();
        this.blockDatas = (HashMap<Integer, DataBlock>) in.readSerializable();
    }

    public static final Parcelable.Creator<OznerCard> CREATOR = new Parcelable.Creator<OznerCard>() {
        @Override
        public OznerCard createFromParcel(Parcel source) {
            return new OznerCard(source);
        }

        @Override
        public OznerCard[] newArray(int size) {
            return new OznerCard[size];
        }
    };
}
