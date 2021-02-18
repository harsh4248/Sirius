package com.example.sirius;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class ImagesData implements Parcelable {

    /*File mResource;

    public ImagesData(File mResource) {
        this.mResource = mResource;
    }

    protected ImagesData(Parcel in) {
    }

    public static final Creator<ImagesData> CREATOR = new Creator<ImagesData>() {
        @Override
        public ImagesData createFromParcel(Parcel in) {
            return new ImagesData(in);
        }

        @Override
        public ImagesData[] newArray(int size) {
            return new ImagesData[size];
        }
    };

    public File getmResource() {
        return mResource;
    }

    public void setmResource(File mResource) {
        this.mResource = mResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeParcelable((Parcelable) mResource,flags);
        dest.write
    }*/
    String mResource;

    public ImagesData(String mResource) {
        this.mResource = mResource;
    }

    protected ImagesData(Parcel in) {
        mResource = in.readString();
    }

    public static final Creator<ImagesData> CREATOR = new Creator<ImagesData>() {
        @Override
        public ImagesData createFromParcel(Parcel in) {
            return new ImagesData(in);
        }

        @Override
        public ImagesData[] newArray(int size) {
            return new ImagesData[size];
        }
    };

    public String getmResource() {
        return mResource;
    }

    public void setmResource(String mResource) {
        this.mResource = mResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mResource);
    }
}
