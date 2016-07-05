package com.xya.JniMethod;

import android.graphics.Bitmap;

/**
 * Created by ubuntu on 15-1-21.
 */
public class ImageBlur {
    public static native void blurBitMap(Bitmap bitmap, int r);
    static {
        System.loadLibrary("blur");
    }
}
