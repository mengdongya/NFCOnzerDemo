package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 */

public class DeviceNumberCard implements Parcelable {
    public String cardType;
    public String cardNumber;
    public String deviceNumber;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardType);
        dest.writeString(this.cardNumber);
        dest.writeString(this.deviceNumber);
    }

    public DeviceNumberCard() {
    }

    protected DeviceNumberCard(Parcel in) {
        this.cardType = in.readString();
        this.cardNumber = in.readString();
        this.deviceNumber = in.readString();
    }

    public static final Parcelable.Creator<DeviceNumberCard> CREATOR = new Parcelable.Creator<DeviceNumberCard>() {
        @Override
        public DeviceNumberCard createFromParcel(Parcel source) {
            return new DeviceNumberCard(source);
        }

        @Override
        public DeviceNumberCard[] newArray(int size) {
            return new DeviceNumberCard[size];
        }
    };
}
