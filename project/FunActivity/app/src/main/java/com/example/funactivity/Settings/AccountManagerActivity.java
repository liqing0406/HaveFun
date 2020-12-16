package com.example.funactivity.Settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.funactivity.My.SettingActivity;
import com.example.funactivity.R;
import com.example.funactivity.adapter.AMAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AccountManagerActivity extends AppCompatActivity {
    private AMAdapter amAdapter;
    private List<String> list;
    private SwipeMenuRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountmanager);
        //账户管理设置字体样式
        TextView accountManager = findViewById(R.id.tv_account);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZGongYHJW.TTF");
        accountManager.setTypeface(typeface);
        //初始化
        list = new ArrayList<>();
        inint();
        recyclerView = findViewById(R.id.recycler_view);
        amAdapter = new AMAdapter(AccountManagerActivity.this, R.layout.account_item_layout, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置侧滑
        recyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(AccountManagerActivity.this)
                        .setBackground(R.color.black)
                        .setImage(R.drawable.delete)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setWidth(150);
                //右侧滑
                swipeRightMenu.addMenuItem(deleteItem);
            }
        });
        recyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                //将list中position位置数据移除
                list.remove(adapterPosition);
                //重新设置adapter
                recyclerView.setAdapter(amAdapter);
            }
        });
        recyclerView.setAdapter(amAdapter);

    }

    //返回
    public void back(View view) {
        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);
    }

    public void inint() {
        for (int i = 0; i < 4; i++) {
            list.add(i + "");
        }
    }

    public void add(View view) {
        Intent i = new Intent(this, AddCountActivity.class);
        startActivity(i);
    }
}
