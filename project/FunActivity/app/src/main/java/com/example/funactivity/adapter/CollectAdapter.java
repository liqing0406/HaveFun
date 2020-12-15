package com.example.funactivity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.ViewHolder> {
    private Context context;
    private View inflater;
    private List<UserCollectActivity> activities;
    private int item;
    private String myID;
    private OkHttpClient client;
    //定义接口对象
    private OnMyItemClickListener onMyItemClickListener;
    private onItemClickListener onItemClickListener;

    public CollectAdapter(Context context, int item, List<UserCollectActivity> lists,String myID) {
        this.context = context;
        this.item = item;
        this.activities = lists;
        this.myID = myID;
        client = new OkHttpClient();
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
        holder.apply.setText(activities.get(position).getActivity().getSignUpNum() + "");
        holder.collect.setText(activities.get(position).getActivity().getCollectNum() + "");
        holder.button_cancel.setText("取消收藏");
        ifHanUp(position,holder.handler);
    }

    private void ifHanUp(int position, Handler handler) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId",activities.get(position).getActivity().getActivityId()+"");
        builder.add("id",myID);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL+"activity/judgeEnterActivity")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Message message = new Message();
                message.what = 1;
                message.obj = result;
                handler.sendMessage(message);
            }
        });
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

    @Override
    public int getItemCount() {
        return activities.size();
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
        TextView state;//显示也报名
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        String s = (String)msg.obj;
                        if(s.equals("true")){
                            state.setText("已报名");
                            state.setBackgroundColor(context.getColor(R.color.colorAccent));
                        }else{
                            state.setText("");
                            state.setBackgroundColor(context.getColor(R.color.zong));
                        }
                        break;
                }
            }
        };

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
            state = itemView.findViewById(R.id.tv_state);
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

