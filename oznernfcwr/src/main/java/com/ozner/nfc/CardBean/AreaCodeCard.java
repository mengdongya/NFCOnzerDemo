package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 *
 * 修改区域代码卡
 */

public class AreaCodeCard implements Parcelable {
    public String cardType;
    public String cardNumber;
    public String areaCode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardType);
        dest.writeString(this.cardNumber);
        dest.writeString(this.areaCode);
    }

    public AreaCodeCard() {
    }

    protected AreaCodeCard(Parcel in) {
        this.cardType = in.readString();
        this.cardNumber = in.readString();
        this.areaCode = in.readString();
    }

    public static final Parcelable.Creator<AreaCodeCard> CREATOR = new Parcelable.Creator<AreaCodeCard>() {
        @Override
        public AreaCodeCard createFromParcel(Parcel source) {
            return new AreaCodeCard(source);
        }

        @Override
        public AreaCodeCard[] newArray(int size) {
            return new AreaCodeCard[size];
        }
    };
}
