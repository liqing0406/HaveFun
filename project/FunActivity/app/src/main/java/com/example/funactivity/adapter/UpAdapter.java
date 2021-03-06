package com.example.funactivity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.funactivity.DetailActivity;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.UserPublishActivity;
import com.example.funactivity.util.Constant;

import java.text.SimpleDateFormat;
import java.util.List;

public class UpAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserPublishActivity> activities;
    private int itemLayoutRes;
    private String myID;

    public UpAdapter(Context mContext, List<UserPublishActivity> activities, int itemLayoutRes, String myID) {
        this.mContext = mContext;
        this.activities = activities;
        this.itemLayoutRes = itemLayoutRes;
        this.myID = myID;
    }

    @Override
    public int getCount() {
        if (null != activities) {
            return activities.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (null != activities) {
            return activities.get(position);
        } else {
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "SimpleDateFormat", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(itemLayoutRes, null);

        ImageView img = convertView.findViewById(R.id.iv_icon);
        TextView title = convertView.findViewById(R.id.tv_title);
        TextView time = convertView.findViewById(R.id.tv_time);
        TextView handup = convertView.findViewById(R.id.tv_handup);
        TextView like = convertView.findViewById(R.id.tv_like);
        LinearLayout linearLayout = convertView.findViewById(R.id.linearlayout);

        Log.e("图片路径",activities.get(position).toString());
        Glide.with(mContext)
                .load(Constant.PIC_PATH + activities.get(position).getActivity().getFrontPicture().getPictureName())
                .into(img);
        title.setText(activities.get(position).getActivity().getActivityTile());
        time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(activities.get(position).getActivity().getActivityTime()));
        handup.setText(activities.get(position).getActivity().getSignUpNum() + "");
        like.setText(activities.get(position).getActivity().getCollectNum() + "");
        return convertView;
    }
}
