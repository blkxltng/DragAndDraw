package com.blkxltng.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by firej on 12/6/2017.
 */

public class Box implements Parcelable {
    private PointF mOrigin;
    private PointF mCurrent;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    //Parceelable methods
    private Box (Parcel parcel) {
        mOrigin.readFromParcel(parcel);
        mCurrent.readFromParcel(parcel);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        mOrigin.writeToParcel(parcel, i);
        mCurrent.writeToParcel(parcel, i);
    }

    public static final Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel parcel) {
            Box b = new Box(parcel);
            return b;
        }

        @Override
        public Box[] newArray(int i) {
            return new Box[i];
        }
    };
}
