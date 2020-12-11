package com.example.funactivity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.UserCollectActivity;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.util.Constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.ViewHolder> {
    private Context context;
    private View inflater;
    private List<UserCollectActivity> activities;
    private int item;
    //定义接口对象
    private OnMyItemClickListener onMyItemClickListener;
    private onItemClickListener onItemClickListener;

    public CollectAdapter(Context context, int item, List<UserCollectActivity> lists) {
        this.context = context;
        this.item = item;
        this.activities = lists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        inflater = LayoutInflater.from(context).inflate(item, parent, false);
        viewHolder = new ViewHolder(inflater);
        viewHolder.onMyItemClickListener = onMyItemClickListener;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v);
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //收藏时间未获取
        Glide.with(context)
                //.load(Constant.BASE_URL+"img/img1.jpg")
                .load(Constant.PIC_PATH+activities.get(position).getActivity().getFrontPicture().getPictureName())
                .into(holder.picture);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        holder.name.setText(activities.get(position).getActivity().getActivityTile());
        holder.time.setText(dateFormat.format(activities.get(position).getActivity().getActivityTime()));
        holder.address.setText(activities.get(position).getActivity().getActivityLocation().toString());
        holder.money.setText(activities.get(position).getActivity().getActivityCost() + "");
        holder.apply.setText(activities.get(position).getActivity().getActivityContact() + "");
        holder.collect.setText(activities.get(position).getActivity().getCollectNum() + "");
        holder.button_cancel.setText("取消收藏");


    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public interface OnMyItemClickListener {
        void onMyItemClick(View view, int position);
    }
    public interface onItemClickListener{
        void onItemClick(View view);
    }
    public void setOnItemClickListener(OnMyItemClickListener onItemClickListener) {
        this.onMyItemClickListener = onItemClickListener;
    }
    public void setOn(onItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnMyItemClickListener onMyItemClickListener;
        TextView timeup;//收藏时间
        ImageView picture;
        TextView name;
        TextView time;//活动时间
        TextView address;
        TextView money;
        TextView apply;
        TextView collect;
        Button button_cancel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picture=itemView.findViewById(R.id.iv_picture);//图片
            timeup = itemView.findViewById(R.id.tv_timeup);//收藏时间
            name = itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);//活动时间
            address = itemView.findViewById(R.id.tv_address);
            money = itemView.findViewById(R.id.tv_money);
            apply = itemView.findViewById(R.id.tv_apply);
            collect = itemView.findViewById(R.id.tv_collect);
            button_cancel = itemView.findViewById(R.id.btn_cancel);//取消收藏
            button_cancel.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onMyItemClickListener != null) {
                onMyItemClickListener.onMyItemClick(v, getAbsoluteAdapterPosition());
            }
        }


    }

}
