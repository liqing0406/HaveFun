package com.example.funactivity.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.add.AddActivity;
import com.example.funactivity.entity.User.User;
import com.google.gson.Gson;

public class AddFragment extends Fragment {
    private View view;
    private TextView release;
    private LinearLayout riding;//骑行运动
    private LinearLayout mountain;//山地运动
    private LinearLayout run;//跑步运动
    private LinearLayout swim;//水上运动
    private LinearLayout ball;//球类运动
    private LinearLayout other;//其他类运动

    private User user;//用户

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载布局页面
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add,
                    container,
                    false);
        }
        initView();
        return view;
    }

    private void initView() {
        release = view.findViewById(R.id.tv_release);
        riding = view.findViewById(R.id.ll_riding);
        mountain = view.findViewById(R.id.ll_mountain);
        run = view.findViewById(R.id.ll_run);
        swim = view.findViewById(R.id.ll_swim);
        ball = view.findViewById(R.id.ll_ball);
        other = view.findViewById(R.id.ll_other);
        Main2Activity main2Activity= (Main2Activity) getActivity();
        user=main2Activity.getUser();
        //发布活动设置字体
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "FZGongYHJW.TTF");
        release.setTypeface(typeface);
        riding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRiding();
            }
        });
        mountain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMountain();
            }
        });
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRun();
            }
        });
        ball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBall();
            }
        });
        swim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSwim();
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOther();
            }
        });
    }
    private void getRiding() {
        Intent intent1 = new Intent();
        intent1.putExtra("type","骑行运动");
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent1.putExtras(bundle);
        intent1.setClass(view.getContext(), AddActivity.class);
        startActivity(intent1);
    }

    private void getMountain(){
        Intent intent2 = new Intent();
        intent2.putExtra("type","山地运动");
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent2.putExtras(bundle);
        intent2.setClass(view.getContext(), AddActivity.class);
        startActivity(intent2);
    }

    private void getRun() {
        Intent intent3 = new Intent();
        intent3.putExtra("type","跑步运动");
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent3.putExtras(bundle);
        intent3.setClass(view.getContext(), AddActivity.class);
        startActivity(intent3);
    }

    private void getSwim() {
        Intent intent4 = new Intent();
        intent4.putExtra("type","水面运动");
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent4.putExtras(bundle);
        intent4.setClass(view.getContext(), AddActivity.class);
        startActivity(intent4);
    }

    private void getBall() {

        Intent intent5 = new Intent();
        intent5.putExtra("type","球类运动");
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent5.putExtras(bundle);
//        String json=gson.toJson(user);
//        intent5.putExtra("userJson",json);
        intent5.setClass(view.getContext(), AddActivity.class);
        startActivity(intent5);
    }

    private void getOther() {
        Intent intent6 = new Intent();
        intent6.putExtra("type","其他");
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        intent6.putExtras(bundle);
        intent6.setClass(view.getContext(), AddActivity.class);
        startActivity(intent6);
    }

}
