package com.sunnyxibei.permission;

import android.Manifest;
import android.os.Build;

/**
 * Created by jiayuanbin on 2017-11-16.
 */

public class Permission {

    public static final String[] PHONE;
    public static final String[] STORAGE;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PHONE = new String[]{};
            STORAGE = new String[]{};
        } else {
            PHONE = new String[]{
                    Manifest.permission.READ_PHONE_STATE};
            STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }
}

