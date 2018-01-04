package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 *
 * 密码卡
 */

public class CipherCard implements Parcelable {
    public String cardType;
    public String cardNumber;
    public byte[] keyA;
    public byte[] keyB;

    public CipherCard() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardType);
        dest.writeString(this.cardNumber);
        dest.writeByteArray(this.keyA);
        dest.writeByteArray(this.keyB);
    }

    protected CipherCard(Parcel in) {
        this.cardType = in.readString();
        this.cardNumber = in.readString();
        this.keyA = in.createByteArray();
        this.keyB = in.createByteArray();
    }

    public static final Creator<CipherCard> CREATOR = new Creator<CipherCard>() {
        @Override
        public CipherCard createFromParcel(Parcel source) {
            return new CipherCard(source);
        }

        @Override
        public CipherCard[] newArray(int size) {
            return new CipherCard[size];
        }
    };
}
