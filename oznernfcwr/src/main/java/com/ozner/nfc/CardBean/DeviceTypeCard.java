package com.ozner.nfc.CardBean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 * 机型卡
 */

public class DeviceTypeCard implements Parcelable {
    public String cardType;
    public String cardNumber;
    public String areaCode;
    public String deviceType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardType);
        dest.writeString(this.cardNumber);
        dest.writeString(this.areaCode);
        dest.writeString(this.deviceType);
    }

    public DeviceTypeCard() {
    }

    protected DeviceTypeCard(Parcel in) {
        this.cardType = in.readString();
        this.cardNumber = in.readString();
        this.areaCode = in.readString();
        this.deviceType = in.readString();
    }

    public static final Parcelable.Creator<DeviceTypeCard> CREATOR = new Parcelable.Creator<DeviceTypeCard>() {
        @Override
        public DeviceTypeCard createFromParcel(Parcel source) {
            return new DeviceTypeCard(source);
        }

        @Override
        public DeviceTypeCard[] newArray(int size) {
            return new DeviceTypeCard[size];
        }
    };
}
