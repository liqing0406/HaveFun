package com.example.funactivity.util;

import android.content.Context;
import android.widget.ImageView;

public class BitmapUtil {

    public static void setImageViewByImagLoading(Context mContext, String Path, ImageView imageView) {
        String imagePath;
        if (Path.trim().startsWith("https://") || Path.trim().startsWith("http://") || Path.trim().startsWith("file://")) {
            imagePath = Path.trim();
        } else {
            imagePath = "file://" + Path.trim();
        }
        PicassoUtils.setAvatarImg(mContext, imagePath, imageView);
    }
}
