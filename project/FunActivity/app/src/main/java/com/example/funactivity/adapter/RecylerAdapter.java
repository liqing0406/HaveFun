package com.example.funactivity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.funactivity.R;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.util.Constant;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecylerAdapter extends RecyclerView.Adapter<RecylerAdapter.ViewHolder>{
    private Context context;
    private View inflater;
    private List<Activity> activities;
    private OnItemClickListener itemClickListener;//声明接口变量
    private int item;
    private OkHttpClient client;
    private Integer myID;
    public RecylerAdapter(Context context, int item, List<Activity> lists,Integer myID){
        this.context=context;
        this.item=item;
        this.activities=lists;
        this.myID=myID;
        client = new OkHttpClient();
    }
    //设置回调接口
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    //提供set方法
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context).inflate(item,parent,false);
        ViewHolder myViewHolder = new ViewHolder(inflater);
        //inflater.setOnClickListener(this);
        return myViewHolder;
    }

    public void onClick(View v) {
        if (itemClickListener!=null){
//            itemClickListener.onItemClick((Integer) v.getTag());
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //数据和控件绑定
        Glide.with(context)
                .load(Constant.PIC_PATH + activities.get(position).getFrontPicture().getPictureName())
                .into(holder.picture);
        holder.name.setText(activities.get(position).getActivityTile());
        holder.time.setText(new SimpleDateFormat("yyyy--MM-dd  HH:mm").format(activities.get(position).getActivityTime()));
        holder.address.setText(activities.get(position).getActivityLocation().toString());
        holder.money.setText(activities.get(position).getActivityCost() + "");
        holder.apply.setText(activities.get(position).getActivityContact() + "");
        holder.collect.setText(activities.get(position).getCollectNum() + "");
        ifHanUp(position,holder.handler);
        //通过为条目设置点击事件触发回调
        if (itemClickListener != null){
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //返回总条数
        return activities.size();
    }

    private void ifHanUp(int position, Handler handler) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId",activities.get(position).getActivityId()+"");
        builder.add("id",myID+"");
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

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView picture;
        TextView name;
        TextView time;
        TextView address;
        TextView money;
        TextView apply;
        TextView collect;
        TextView state;
        RelativeLayout item;
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
            picture = itemView.findViewById(R.id.iv_picture);
            name=itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);
            address = itemView.findViewById(R.id.tv_address);
            money = itemView.findViewById(R.id.tv_money);
            apply = itemView.findViewById(R.id.tv_apply);
            collect = itemView.findViewById(R.id.tv_collect);
            item = itemView.findViewById(R.id.rl_item);
            state = itemView.findViewById(R.id.tv_state);
        }
    }
}
