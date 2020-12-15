package com.example.funactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.UserEnterActivity;
import com.example.funactivity.util.Constant;

import java.text.SimpleDateFormat;
import java.util.List;

public class SignUpAdapter extends RecyclerView.Adapter<SignUpAdapter.ViewHolder>{
    private Context context;
    private View inflater;
    private List<UserEnterActivity> lists;
    private int item;
    private OnMyItemClickListener onMyItemClickListener;
    private onItemClickListener onItemClickListener;

    public SignUpAdapter(Context context, int item, List<UserEnterActivity> lists){
        this.context=context;
        this.item=item;
        this.lists=lists;
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
        //数据和控件绑定
        holder.name.setText(lists.get(position).getActivity().getActivityTile());
        holder.timeup.setText(new SimpleDateFormat("yyyy-MM-dd  HH:mm").format(lists.get(position).getEnterTime()));
        holder.time.setText(new SimpleDateFormat("yyyy--MM-dd  HH:mm").format(lists.get(position).getActivity().getActivityTime()));
        holder.address.setText(lists.get(position).getActivity().getActivityLocation().toString());
        holder.money.setText(lists.get(position).getActivity().getActivityCost());
        holder.apply.setText(lists.get(position).getActivity().getActivityContact());
        holder.button_cancel.setText("取消报名");
        Glide.with(context)
                .load(Constant.PIC_PATH+lists.get(position).getActivity().getFrontPicture().getPictureName())
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        if (lists != null){
            //返回总条数
            return lists.size();
        }
       return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        OnMyItemClickListener onMyItemClickListener;
        ImageView picture;//图片
        TextView timeup;//报名时间
        TextView name;
        TextView time;//活动时间
        TextView address;
        TextView money;
        TextView apply;
        TextView collect;
        Button button_cancel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.iv_picture);
            timeup=itemView.findViewById(R.id.tv_timeup);//报名时间
            name=itemView.findViewById(R.id.tv_name);
            time=itemView.findViewById(R.id.tv_time);//活动时间
            address=itemView.findViewById(R.id.tv_address);
            money=itemView.findViewById(R.id.tv_money);
            apply=itemView.findViewById(R.id.tv_apply);//报名人数
            collect=itemView.findViewById(R.id.tv_collect);//收藏人数
            button_cancel=itemView.findViewById(R.id.btn_cancel);//取消报名按钮
            button_cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onMyItemClickListener!=null){
                onMyItemClickListener.onMyItemClick(v,getAbsoluteAdapterPosition());
            }
        }
    }
}
