package com.hyphenate.easeui.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.GetUserInfo;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.User.User;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.userDetail.HeOrSheActivity;

import java.util.Timer;
import java.util.TimerTask;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    static Timer timer = new Timer();
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(final Context context, String username, ImageView imageView){
        GetUserInfo getUserInfo = new GetUserInfo();
        getUserInfo.getUser(username);
        User user = null;
        while (user==null){
            user = getUserInfo.getUser();
        }
        Glide.with(context).load(Constant.PIC_PATH+user.getHeadPortrait()).into(imageView);
        final String id = user.getId()+"";
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("点击了头像","dsdsd");
                Intent intent = new Intent();
                intent.setClass(context, HeOrSheActivity.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
            }
        });
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNickname() != null){
        		textView.setText(user.getNickname());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    
}
