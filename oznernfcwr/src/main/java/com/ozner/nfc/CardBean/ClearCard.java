package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 *
 * 清除卡，测试卡
 */

public class ClearCard implements Parcelable {
    public String cardType;
    public String cardNumber;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardType);
        dest.writeString(this.cardNumber);
    }

    public ClearCard() {
    }

    protected ClearCard(Parcel in) {
        this.cardType = in.readString();
        this.cardNumber = in.readString();
    }

    public static final Parcelable.Creator<ClearCard> CREATOR = new Parcelable.Creator<ClearCard>() {
        @Override
        public ClearCard createFromParcel(Parcel source) {
            return new ClearCard(source);
        }

        @Override
        public ClearCard[] newArray(int size) {
            return new ClearCard[size];
        }
    };
}
