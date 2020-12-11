package com.example.funactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.UserPublishActivity;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.util.Constant;

import java.text.SimpleDateFormat;
import java.util.List;

public class UpAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserPublishActivity> activities;
    private int itemLayoutRes;

    public UpAdapter(Context mContext, List<UserPublishActivity> activities, int itemLayoutRes) {
        this.mContext = mContext;
        this.activities = activities;
        this.itemLayoutRes = itemLayoutRes;
    }

    @Override
    public int getCount() {
        if(null!=activities){
            return activities.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(null!=activities){
            return activities.get(position);
        }else {
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(itemLayoutRes,null);

        ImageView img=convertView.findViewById(R.id.iv_icon);
        TextView title=convertView.findViewById(R.id.tv_title);
        TextView time = convertView.findViewById(R.id.tv_time);
        TextView handup=convertView.findViewById(R.id.tv_handup);
        TextView like=convertView.findViewById(R.id.tv_like);

        Glide.with(mContext)
                .load(Constant.PIC_PATH + activities.get(position).getActivity().getFrontPicture().getPictureName())
                .into(img);
        title.setText(activities.get(position).getActivity().getActivityTile());
        time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(activities.get(position).getPublishTime()));
        handup.setText(activities.get(position).getActivity().getSignUpNum()+"");
        like.setText(activities.get(position).getActivity().getCollectNum()+"");


        return convertView;
    }
}