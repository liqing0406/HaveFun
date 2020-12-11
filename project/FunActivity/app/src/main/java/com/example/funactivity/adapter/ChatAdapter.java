package com.example.funactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.entity.message.Messages;
import com.example.funactivity.util.Constant;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context context;
    private View inflater;
    private List<Map<Messages,Integer>> lists;
    private int item;
    private OnItemClickListener itemClickListener;//声明接口变量
    private int id;//用户id
    public ChatAdapter(Context context,int item,List<Map<Messages,Integer>> lists,int id){
        this.context=context;
        this.item=item;
        this.lists=lists;
        this.id = id;
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
        ViewHolder myViewHolder=new ViewHolder(inflater);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOptions requestOptions=new RequestOptions()
                .circleCrop();
        for (Map<Messages,Integer> m:lists){
            for (Messages msg:m.keySet()){
                Object o = m.get(msg);
                if (msg.getSendUser().getId() == id){
                    holder.name.setText(msg.getReceiveUser().getUserName());
                    Glide.with(inflater).
                            load(Constant.BASE_URL + msg.getReceiveUser().getHeadPortrait()).
                            apply(requestOptions).
                            into(holder.image);
                }else{
                    holder.name.setText(msg.getSendUser().getUserName());
                    Glide.with(inflater).
                            load(Constant.BASE_URL + msg.getSendUser().getHeadPortrait()).
                            apply(requestOptions).
                            into(holder.image);
                }
                holder.lastMsg.setText(msg.getMessage());
                holder.time.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(msg.getTime()));
                if (o.toString().equals("0")){
                    holder.unread.setVisibility(View.INVISIBLE);
                }else {
                    holder.unreadMsgNum.setText(o.toString());
                }

            }

        }

//        long date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(lists.get(position).getTime());
//        long nowdate =
//        if (date)


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
        if (lists!=null){
            return lists.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout item;
        TextView name;//发消息的人
        ImageView image;//头像
        TextView time;//发送消息的时间
        TextView lastMsg;//最新消息
        TextView unreadMsgNum;//未读消息数
        RelativeLayout unread;//未读消息
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.ll_item);
            name=itemView.findViewById(R.id.name);
            image=itemView.findViewById(R.id.img);
            time = itemView.findViewById(R.id.time);
            lastMsg = itemView.findViewById(R.id.last_msg);
            unreadMsgNum = itemView.findViewById(R.id.unread_msg_num);
            unread = itemView.findViewById(R.id.rl_unread);
        }
    }
}
