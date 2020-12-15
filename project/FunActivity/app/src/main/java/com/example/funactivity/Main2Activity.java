package com.example.funactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.Fragment.AddFragment;
import com.example.funactivity.Fragment.AllFragment;
import com.example.funactivity.Fragment.MainFragment;
import com.example.funactivity.Fragment.MyFragment;
import com.example.funactivity.Fragment.NewsFragment;
import com.example.funactivity.entity.User.User;

public class Main2Activity extends AppCompatActivity {
    private User user;
    private Fragment currentFragment = new Fragment();
    private AddFragment addFragment;
    private AllFragment allFragment;
    private MainFragment mainFragment;
    private MyFragment myFragment;
    private NewsFragment newsFragment;
    private FragmentManager manager = getSupportFragmentManager();
    private ImageView mainImg;
    private ImageView allImg;
    private ImageView addImg;
    private ImageView newsImg;
    private ImageView mineImg;
    private TextView mainTv;
    private TextView allTv;
    private TextView addTv;
    private TextView newsTv;
    private TextView mineTv;
    private String code;//表示跳转过来的页面;100:主页面 200:我的页面 300:聊天页面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (savedInstanceState != null) {
            addFragment = (AddFragment) manager.findFragmentByTag("addFragment");
            allFragment = (AllFragment) manager.findFragmentByTag("allFragment");
            mainFragment = (MainFragment) manager.findFragmentByTag("mainFragment");
            myFragment = (MyFragment) manager.findFragmentByTag("myFragment");
            newsFragment = (NewsFragment) manager.findFragmentByTag("newsFragment");
        }
        //实例化五个Fragment
        addFragment = new AddFragment();
        allFragment = new AllFragment();
        mainFragment = new MainFragment();
        myFragment = new MyFragment();
        newsFragment = new NewsFragment();
        //获取上个页面传来的num和code
        //获取上个页面传来的user
        Intent intent = getIntent();
        user = JSON.parseObject(intent.getStringExtra("user"), User.class);
        Log.e("user",""+user.getHeadPortrait());
        code = intent.getStringExtra("code");
        //初始化控件
        initView();
        //改变当前mainFragment导航栏颜色
        mainImg.setImageResource(R.drawable.main_y);
        mainTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));

        switch (code) {
            case "100"://主页面
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                mainImg.setImageResource(R.drawable.main_y);
                mainTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(mainFragment);
                break;
            case "200"://我的页面
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                mineImg.setImageResource(R.drawable.my_y);
                mineTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(myFragment);
                break;
            case "300"://聊天页面
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                newsImg.setImageResource(R.drawable.news_y);
                newsTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(newsFragment);
                break;
        }
    }

    //获取控件（并设置图片文字资源，但是切换的时候很多都不需要重新设置，有点浪费资源，你觉得可以改就改改，我不改了）不改
    private void initView() {
        mainImg = findViewById(R.id.img_main);
        mainImg.setImageResource(R.drawable.main);
        allImg = findViewById(R.id.img_all);
        allImg.setImageResource(R.drawable.all);
        addImg = findViewById(R.id.img_add);
        addImg.setImageResource(R.drawable.add);
        newsImg = findViewById(R.id.img_news);
        newsImg.setImageResource(R.drawable.news);
        mineImg = findViewById(R.id.img_mine);
        mineImg.setImageResource(R.drawable.my);
        mainTv = findViewById(R.id.tv_main);
        mainTv.setTextColor(ContextCompat.getColor(this, R.color.black));
        allTv = findViewById(R.id.tv_all);
        allTv.setTextColor(ContextCompat.getColor(this, R.color.black));
        addTv = findViewById(R.id.tv_add);
        addTv.setTextColor(ContextCompat.getColor(this, R.color.black));
        newsTv = findViewById(R.id.tv_news);
        newsTv.setTextColor(ContextCompat.getColor(this, R.color.black));
        mineTv = findViewById(R.id.tv_mime);
        mineTv.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    //布局文件设置的Onclick点击事件方法
    public void textClicked(View view) {
        switch (view.getId()) {
            case R.id.spec_main:
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                mainImg.setImageResource(R.drawable.main_y);
                mainTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(mainFragment);
                break;
            case R.id.spec_all:
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                allImg.setImageResource(R.drawable.all_y);
                allTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(allFragment);
                break;
            case R.id.spec_add:
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                addImg.setImageResource(R.drawable.add_y);
                addTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(addFragment);
                break;
            case R.id.spec_news:
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                newsImg.setImageResource(R.drawable.news_y);
                newsTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(newsFragment);
                break;
            case R.id.spec_mine:
                //将所有导航栏重置为灰色
                initView();
                //给点击的导航栏设置颜色
                mineImg.setImageResource(R.drawable.my_y);
                mineTv.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                //切换页面
                changeTab(myFragment);
                break;
        }
    }

    public User getUser() {
        return user;
    }


    private void changeTab(Fragment fragment) {
        //开启事务
        FragmentTransaction transaction = manager.beginTransaction();
        //判断是否是切换页面
        if (currentFragment != fragment) {
            if (!fragment.isAdded()) {
                //判断是不是没添加
                transaction.add(R.id.tab_content, fragment);
            }
            transaction.hide(currentFragment);
            transaction.show(fragment);
            //事务提交，切换页面
            transaction.commit();
            //当前页面
            currentFragment = fragment;
        }
    }

    public void setUser(User user) {
        this.user=user;
    }
}