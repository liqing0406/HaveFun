package com.example.funactivity.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.funactivity.R;
import com.squareup.picasso.Picasso;

public class PicassoUtils {
    public static void setAvatarImg(Context context, String url, ImageView img) {
        Picasso.with(context)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .fit()
                .config(Bitmap.Config.RGB_565)
                .into(img);
    }
}
