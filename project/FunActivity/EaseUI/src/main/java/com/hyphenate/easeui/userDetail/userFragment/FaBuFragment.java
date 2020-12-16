package com.hyphenate.easeui.userDetail.userFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.User.UserPublishActivity;
import com.hyphenate.easeui.adapter.UpAdapter;
import com.hyphenate.easeui.userDetail.HeOrSheActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.utils.Constant;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FaBuFragment extends Fragment {
    private String id;
    private List<UserPublishActivity> myFaBuActivity;
    private UpAdapter upAdapter;
    private OkHttpClient client;
    private Gson gson;
    private GridView gridView;
    private View view;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    myFaBuActivity = (List<UserPublishActivity>)msg.obj;
                    upAdapter = new UpAdapter(getContext(),myFaBuActivity, R.layout.up_list_item,id);
                    gridView.setAdapter(upAdapter);
                    upAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_pager_fragment,container,false);

        setAttribute();
        getMyFaBuActivity();

        return view;
    }

    /**
     * 获取我发布的活动信息
     */
    private void getMyFaBuActivity() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id",id+"");
        builder.add("pageNum",10+"");
        builder.add("pageSize",1+"");
        FormBody body = builder.build();
        final Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL+"activity/getPublishActivities")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                if(!result.equals("empty")){
                    Type type = new TypeToken<List<UserPublishActivity>>(){}.getType();
                    List<UserPublishActivity> faBuActivity = gson.fromJson(result,type);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = faBuActivity;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void setAttribute() {
        HeOrSheActivity heOrSheActivity = (HeOrSheActivity) getActivity();
        id = heOrSheActivity.getId();
        gridView = view.findViewById(R.id.grid_view);
        client = new OkHttpClient();
        gson = new Gson();

    }
}
