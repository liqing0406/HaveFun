package com.example.funactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.funactivity.R;

import java.util.List;

public class AMAdapter extends RecyclerView.Adapter<AMAdapter.ViewHolder> {
    private Context context;
    private View inflater;
    private List<String> lists;
    private int item;
    public AMAdapter(Context context, int item, List<String> lists){
        this.context=context;
        this.item=item;
        this.lists=lists;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context).inflate(item,parent,false);
        ViewHolder myViewHolder = new ViewHolder(inflater);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(lists.get(position));
        RequestOptions requestOptions=new RequestOptions()
                .circleCrop();
       Glide.with(inflater).load(R.drawable.laozhou).apply(requestOptions).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tv_name);
            //头像圆形处理
            imageView=itemView.findViewById(R.id.iv_touxiang);


        }
    }
}
