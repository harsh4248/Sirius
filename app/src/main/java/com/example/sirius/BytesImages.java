package com.example.sirius;

import android.os.Parcel;
import android.os.Parcelable;

public class BytesImages implements Parcelable {

    byte[] mResource;

    public BytesImages(byte[] mResource) {
        this.mResource = mResource;
    }

    protected BytesImages(Parcel in) {
        mResource = in.createByteArray();
    }

    public static final Creator<BytesImages> CREATOR = new Creator<BytesImages>() {
        @Override
        public BytesImages createFromParcel(Parcel in) {
            return new BytesImages(in);
        }

        @Override
        public BytesImages[] newArray(int size) {
            return new BytesImages[size];
        }
    };

    public byte[] getmResource() {
        return mResource;
    }

    public void setmResource(byte[] mResource) {
        this.mResource = mResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(mResource);
    }
}
