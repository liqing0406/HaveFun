package com.example.funactivity.util;

import android.content.Context;
import android.widget.Toast;

import com.example.funactivity.view.Application;

public class MyToast {

    private static Toast mToast = null;
    public static Context mContext = Application.getInstance();

    public MyToast() {
    }

    public static void showText(CharSequence text) {
        if (mContext != null) {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            try {
                mToast.show();
            } catch (Throwable var2) {
                var2.printStackTrace();
            }

        }
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
