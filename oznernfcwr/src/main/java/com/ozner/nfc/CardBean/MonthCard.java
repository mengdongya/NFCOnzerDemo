package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 */

public class MonthCard implements Parcelable {
    public String cardType;
    public String cardNumber;
    public String cardFaceValue;
    public String cardValue;
    public String areaCode;
    public String deviceType;
    public String secAreaCode;
    @Override
    public String toString() {
        return "block1:" + cardType + "\nblock0:" + cardNumber + "\nblock4:" + cardFaceValue + "\nblock5:" + cardValue
                + "\nblock6:" + areaCode + "\nblock8:" + deviceType+ "\nblock9:" + secAreaCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardNumber);
        dest.writeString(this.cardType);
        dest.writeString(this.cardFaceValue);
        dest.writeString(this.cardValue);
        dest.writeString(this.areaCode);
        dest.writeString(this.deviceType);
        dest.writeString(this.secAreaCode);
    }

    public MonthCard() {
    }

    protected MonthCard(Parcel in) {
        this.cardNumber = in.readString();
        this.cardType = in.readString();
        this.cardFaceValue = in.readString();
        this.cardValue = in.readString();
        this.areaCode = in.readString();
        this.deviceType = in.readString();
        this.secAreaCode = in.readString();
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
