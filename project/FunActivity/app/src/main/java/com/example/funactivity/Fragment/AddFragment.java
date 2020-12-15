package com.example.funactivity.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.add.AddActivity;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.entity.User.UserDetail;
import com.example.funactivity.util.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private PopupWindow popupWindow;
    private View popView;
    private EditText et_name;
    private EditText et_identity;
    private Button button;
    private String userName;
    private String userIdentity;
    private final int RESULT_CODE=100;
    private final int REQUEST_CODE=1;
    private String type;
    private OkHttpClient client=new OkHttpClient();
    private String userJson="lq";
    private Main2Activity main2Activity;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    userJson = (String)msg.obj;
                    user = JSON.parseObject(userJson, User.class);
                    initData();
                    main2Activity.setUser(user);
                    Intent intent= new Intent();
                    intent.putExtra("type", type);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    intent.setClass(view.getContext(), AddActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

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
        initData();
        return view;
    }

    public void initData(){
        //获取服务器数据
        UserDetail detail=user.getUserDetail();
        userIdentity=detail.getResidentIdCard();
        userName=detail.getRealName();
        Log.e("userDetail",userIdentity);
    }
    //popupWindow
    public  void popupWindow(){
        //完善个人资料
        //创建以及初始化
        popupWindow=new PopupWindow(getContext());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                lp.alpha=1.0f;
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getActivity().getWindow().setAttributes(lp);
            }
        });
        WindowManager.LayoutParams lp= getActivity().getWindow().getAttributes();
        lp.alpha=0.3f;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
       getActivity().getWindow().setAttributes(lp);




    }

    private void initView() {
        LayoutInflater linearLayout=getActivity().getLayoutInflater();
        popView=linearLayout.inflate(R.layout.personal_data_popupwindow,null);
        release = view.findViewById(R.id.tv_release);
        riding = view.findViewById(R.id.ll_riding);
        mountain = view.findViewById(R.id.ll_mountain);
        run = view.findViewById(R.id.ll_run);
        swim = view.findViewById(R.id.ll_swim);
        ball = view.findViewById(R.id.ll_ball);
        other = view.findViewById(R.id.ll_other);
        et_identity=popView.findViewById(R.id.et_identity);
        et_name=popView.findViewById(R.id.et_name);
        button=popView.findViewById(R.id.bt_submit);
        main2Activity= (Main2Activity) getActivity();
        user=main2Activity.getUser();
        //发布活动设置字体
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "FZGongYHJW.TTF");
        release.setTypeface(typeface);
        riding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopWindow("骑行运动" );

            }
        });
        mountain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopWindow("山地运动" );
            }
        });
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopWindow("跑步运动" );
            }
        });
        ball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopWindow("球类运动" );

            }
        });
        swim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopWindow("水面运动" );
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopWindow("其他" );
            }
        });
    }

    public void getPopWindow(String t){
        type = t;
        if (userIdentity.equals("未认证")||userName.length()==0) {
            popupWindow();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = et_name.getText().toString();
                    Log.e("name", name + "dfg");
                    String identity = et_identity.getText().toString();
                    int length = identity.length();
                    if (name.equals("") || identity.equals("")) {
                        Toast toast = Toast.makeText(getActivity(), "请将内容填写完整", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        if (length != 18) {
                            Toast toast = Toast.makeText(getActivity(), "身份证号输入错误", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            popupWindow.dismiss();
                            //将信息传递给服务器
                            connectWithService(identity,name);


                        }
                    }
                }
            });
        }else {
            Intent intent= new Intent();
            intent.putExtra("type", type);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtras(bundle);
            intent.setClass(view.getContext(), AddActivity.class);
            startActivity(intent);
        }
        }
        private  void connectWithService(String identity,String name){
            OkHttpClient okHttpClient=new OkHttpClient();
            int id=user.getId();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("id",id+"");
            builder.add("residentIdCard", identity);
            builder.add("realName", name);
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .post(body)
                    .url(Constant.BASE_URL + "user/idCardAuthentication")
                    .build();
            Call call=okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.body().string().equals("true")){
                        login();
                        Log.e("userJson",userJson+"");


                    }
                }
            });
        }

    //向服务端请求登录
    private void login() {
        //提交键值对格式数据
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum",user.getPhoneNum());
//        builder.add("num",num.getText().toString());
        builder.add("password", user.getPassword());
        FormBody body = builder.build();
        //创建请求对象
        Request request = new Request.Builder()
                .post(body)//请求方式为post
//                .url(Constant.BASE_URL+"LoginServlet")
                .url(Constant.BASE_URL + "user/login")
                .build();
        //创建call对象
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //服务端返回登录成功，发布事件，完成界面跳转
                String back = response.body().string();
                Message m = new Message();
                m.what=1;
                m.obj=back;
                handler.sendMessage(m);
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==REQUEST_CODE&&resultCode==RESULT_CODE){
//        }
//    }

}


