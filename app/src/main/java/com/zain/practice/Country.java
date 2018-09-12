package com.zain.practice;

import android.os.Parcel;
import android.os.Parcelable;


public class Country implements Parcelable {
    private String isoCode;
    private String dialingCode;

    public Country() {

    }

    public Country(String isoCode, String dialingCode) {
        this.isoCode = isoCode;
        this.dialingCode = dialingCode;
    }

    protected Country(Parcel in) {
        isoCode = in.readString();
        dialingCode = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String getIsoCode() {
        return isoCode;
    }

    public String getDialingCode() {
        return dialingCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(isoCode);
        dest.writeString(dialingCode);
    }
}
