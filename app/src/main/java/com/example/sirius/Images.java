package com.example.sirius;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Images implements Parcelable {
    Bitmap mResource;

    public Images(Bitmap mResource) {
        this.mResource = mResource;
    }

    protected Images(Parcel in) {
        mResource = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    public Bitmap getmResource() {
        return mResource;
    }

    public void setmResource(Bitmap mResource) {
        this.mResource = mResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mResource, flags);
    }
}
