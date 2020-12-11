//package com.example.funactivity.userFragment;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.GridView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.funactivity.R;
//import com.example.funactivity.adapter.UpAdapter;
//import com.example.funactivity.entity.User.UserPublishActivity;
//import com.example.funactivity.util.Constant;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class CollectFragment extends Fragment {
//    private Integer id;
//    private List<UserPublishActivity> myCollectionActivity;
//    private UpAdapter upAdapter;
//    private OkHttpClient client;
//    private Gson gson;
//    private GridView gridView;
//    private View view;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what) {
//                case 1:
//                    myCollectionActivity = (List<UserPublishActivity>) msg.obj;
//                    upAdapter = new UpAdapter(getContext(), myCollectionActivity, R.layout.up_list_item);
//                    gridView.setAdapter(upAdapter);
//                    upAdapter.notifyDataSetChanged();
//                    break;
//            }
//        }
//    };
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.view_pager_fragment, container, false);
//
//        setAttribute();
//        getMyCollectionActivity();
//        return view;
//    }
//
//    /**
//     * 获取我收藏的活动信息
//     */
//    private void getMyCollectionActivity() {
//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("id", id + "");
//        builder.add("pageNum", 10 + "");
//        builder.add("pageSize", 1 + "");
//        FormBody body = builder.build();
//        final Request request = new Request.Builder()
//                .post(body)
//                .url(Constant.BASE_URL + "activity/getCollectedActivities")
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                String result = response.body().string();
//                Log.e("collection", "" + result);
//                if (!result.equals("empty")) {
//                    Log.e("collection", "" + result);
//                    Type type = new TypeToken<List<UserPublishActivity>>() {
//                    }.getType();
//                    List<UserPublishActivity> collectionActivity = gson.fromJson(result, type);
//                    Message message = new Message();
//                    message.what = 1;
//                    message.obj = collectionActivity;
//                    handler.sendMessage(message);
//                }
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
//            }
//        });
//    }
//
//    private void setAttribute() {
//        HeOrSheActivity heOrSheActivity = (HeOrSheActivity) getActivity();
//        id = heOrSheActivity.getId();
//        gridView = view.findViewById(R.id.grid_view);
//        client = new OkHttpClient();
//        gson = new Gson();
//
//    }
//}
