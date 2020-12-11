package com.example.funactivity.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.My.CollectActivity;
import com.example.funactivity.My.QianmingActivity;
import com.example.funactivity.My.SettingActivity;
import com.example.funactivity.My.SignUpActivity;
import com.example.funactivity.My.WatchImgActivity;
import com.example.funactivity.R;
import com.example.funactivity.adapter.UpAdapter;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.entity.User.UserDetail;
import com.example.funactivity.entity.User.UserPublishActivity;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.util.Constant;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class MyFragment extends Fragment {
    private View view;
    private TextView name;//用户名
    private TextView activityum;//参加活动次数
    private CircularImageView img;//头像
    private TextView personalSignature;//个性签名
    private ImageView editPersonalSignature;//修改个性签名
    private RelativeLayout up;//已报名
    private RelativeLayout like;//已收藏
    private RelativeLayout settings;//设置
    private GridView release;//我的发布
    private User user;
    private UpAdapter upAdapter;
    private List<UserPublishActivity> activities = new ArrayList<>();
    private PopupWindow popupWindow;//弹窗
    private Uri imageUri;
    private OkHttpClient client;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private int pageNum = 1;//当前页
    private int pageSize = 20;//页大小
    private SmartRefreshLayout srl;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //刷新adapter
                    upAdapter.notifyDataSetChanged();
                    //结束上拉动画
                    srl.finishLoadMore();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载内容页面布局
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my,
                    container,
                    false);
            EventBus.getDefault().register(this);
        }
        Main2Activity activity = (Main2Activity) getActivity();
        user = activity.getUser();
        initView();
        return view;
    }

    private void getData() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", user.getId() + "");
        builder.add("pageNum", pageNum + "");
        builder.add("pageSize", pageSize + "");
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/getPublishActivities")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取已发布活动列表数据
                String actjson = response.body().string();
                if ("empty".equals(actjson)) {
                    Looper.prepare();
                    Toast.makeText(MyFragment.this.getContext(), "没有更多内容了！", Toast.LENGTH_SHORT).show();
                    srl.finishLoadMoreWithNoMoreData();
                    Looper.loop();
                } else {
                    List<UserPublishActivity> activitiesList = JSON.parseArray(actjson, UserPublishActivity.class);
                    activities.addAll(activitiesList);//将新获取的数据加入之前的数据集合
                    MyFragment.this.pageNum++;//设置页码＋1
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initView();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        name = view.findViewById(R.id.tv_name);
        activityum = view.findViewById(R.id.tv_activitynum);
        img = view.findViewById(R.id.iv_image);
        personalSignature = view.findViewById(R.id.tv_qianming);
        editPersonalSignature = view.findViewById(R.id.iv_editqianming);
        up = view.findViewById(R.id.btn_up);
        like = view.findViewById(R.id.btn_like);
        settings = view.findViewById(R.id.btn_settings);
        release = view.findViewById(R.id.gv_release);
        name.setText(user.getUserName());
        client = new OkHttpClient();
        srl = view.findViewById(R.id.srl);
        srl.setEnableRefresh(false);//禁用下拉刷新
        activityum.setText(user.getUserDetail().getNumOfActivityForUser() + "");
        personalSignature.setText(user.getUserDetail().getPersonalSignature());
        //加载头像glide
        Glide.with(this)
                .load(Constant.PIC_PATH + user.getHeadPortrait())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(img);
        //更换头像
        img.setOnClickListener(v -> showPopupWindow());
        //修改个性签名
        editPersonalSignature.setOnClickListener(v -> changePersonalSignature());
        //已报名
        up.setOnClickListener(v -> getSignUp());
        //已收藏
        like.setOnClickListener(v -> getCollect());
        //设置
        settings.setOnClickListener(v -> getSetting());
        //我的发布
        upAdapter = new UpAdapter(getActivity(), activities, R.layout.up_list_item);
        release.setAdapter(upAdapter);
        if (pageNum == 1){
            getData();//获取我的发布
        }
        srl.setOnLoadMoreListener(refreshLayout -> getData());
    }

    private void showPopupWindow() {
        View popView = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null);
        //创建popupwindow对象 设置宽高
        popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置各个控件的点击响应
        popupWindow.setContentView(popView);
        Button watch = popView.findViewById(R.id.btn_watch);
        final Button take = popView.findViewById(R.id.btn_take_photo);
        Button choose = popView.findViewById(R.id.btn_choose_photo);
        Button cancel = popView.findViewById(R.id.btn_cancel);

        //查看大图
        watch.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), WatchImgActivity.class);
            i.putExtra("path", user.getHeadPortrait());
            startActivity(i);
        });
        //拍照
        take.setOnClickListener(v -> {
            takePhoto();
            popupWindow.dismiss();
        });

        //从相册选择图片
        choose.setOnClickListener(v -> {
            choosePhoto();
            popupWindow.dismiss();
        });

        cancel.setOnClickListener(v -> popupWindow.dismiss());

        //显示popupWindow
        View rootview = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my, null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    //设置
    private void getSetting() {
        Intent intent5 = new Intent();
        intent5.setClass(view.getContext(), SettingActivity.class);
        startActivity(intent5);
    }

    //已收藏
    private void getCollect() {
        Intent intent4 = new Intent();
        intent4.putExtra("id", user.getId() + "");
        intent4.setClass(view.getContext(), CollectActivity.class);
        startActivity(intent4);
    }

    //已报名
    private void getSignUp() {
        Intent intent3 = new Intent();
        intent3.putExtra("id",user.getId()+"");
        intent3.setClass(view.getContext(), SignUpActivity.class);
        startActivity(intent3);
    }

    //设置个性签名
    private void changePersonalSignature() {
        Intent intent2 = new Intent();
        intent2.putExtra("id",user.getId()+"");
        intent2.putExtra("personalSignature",user.getUserDetail().getPersonalSignature());
        intent2.setClass(view.getContext(), QianmingActivity.class);
        startActivity(intent2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataUI(String msg) {

            Log.e("新签名",msg);
            UserDetail detail = new UserDetail();
            detail.setPersonalSignature(msg);
            user.setUserDetail(detail);
            personalSignature.setText(msg);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 1.选择拍照
     */
    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            requestPermissions(new String[]{Manifest.permission.CAMERA},2);
        }else{
            // 创建File(路径,文件名字)对象,用于储存拍照后的图片
            // getExternalCacheDir获取SDCard/Android/data/你的应用包名/cache/目录,一般存放临时缓存数据
            // getExternalFilesDir()获取SDCard/Android/data/你的应用的包名/files/ 目录,一般放一些长时间保存的数据
            File outputImage = new File(getContext().getExternalCacheDir(), "output_image.jpg");
            try {
                //判断outputImage是否文件存在
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
             * Android 7.0开始，直接读取本地Uri被认为是不安全的,
             * 高于等于Android 7.0需要通过特定的内容容器FileProvider的getUriForFile()将File对象转换成封装的Uri对象
             * getUriForFile()需要三个参数,第一个Context对象,第二个任意且唯一的字符串(自己随便取)但必须保持与你注册里表一致,第三个就是创建的File对象
             * 别忘了在AndroidManifest.xml进行内容容器FileProvider的注册,以及SD卡访问权限
             * */
            imageUri = FileProvider.getUriForFile(getContext(), "net.onest.funactivity.fileprovider", outputImage);
            //启动照相机
            /*通过隐式Intent启动,并且给上个界面返回一个结果码TAKE_PHOTO=1
             * 传递时最好传路径,直接传File,内存太大,并且Intent传值是有内存限制大小的*/
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//直接使用系统相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }

    }


    /**
     * 选择相册
     */
    public void choosePhoto() {
        //ContextCompat.checkSelfPermission检查用户是否授权了某个权限
        //ActivityCompat.requestPermissions动态申请权限，请求权限后，系统会弹出请求权限的Dialog
        //回调 onRequestPermissionsResult方法
        //判断是否申请到权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            //打开相册
            openAlbum();
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        //通过隐式Intent启动,并且给上个界面返回一个结果码CHOOSE_PHOTO
        //android.intent.action.GET_CONTENT是调用系统程序用的，setType指定以哪种形式打开
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                //如果权限申请成功
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "相册打开失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                takePhoto();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO: // 照相
                if (resultCode == RESULT_OK) {
                    startCrop(imageUri);//照相完毕裁剪处理
                }
                break;
            case CHOOSE_PHOTO: // 相册
                if (resultCode == RESULT_OK) {
                    startCrop(data.getData());
                }
                break;
            case UCrop.REQUEST_CROP: // 裁剪后的效果
                if (resultCode == RESULT_OK) {
                    //获取裁剪完成后图片对应的一个序列化的Uri对象
                    Uri resultUri = UCrop.getOutput(data);
//                    ContentResolver contentResolver = getContext().getContentResolver();
//                    Cursor cursor = contentResolver.query(resultUri,null,
//                            null,null,null);
//                    if (cursor.moveToFirst()) {
//                        String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
//                        Glide.with(this)
//                                .load(imagePath)
//                                .into(img);
//                       // uploadImg(imagePath);
//                    }
                    try {
                        //将图片显示在头像上
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(resultUri));
                        img.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.RESULT_ERROR: // 错误裁剪的结果
                if (resultCode == RESULT_OK) {
                    final Throwable cropError = UCrop.getError(data);
                    handleCropError(cropError);
                }
                break;
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param cropError
     */
    private void handleCropError(Throwable cropError) {
        //删除临时文件
        deleteTempPhotoFile();
        if (cropError != null) {
            Toast.makeText(getContext(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(getContext().getExternalCacheDir(), "output_image.jpg");
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    /**
     * 图片裁剪
     *
     * @param uri
     */
    private void startCrop(Uri uri) {
        //裁剪后图片保存在文件夹中，获取存放缓存数据的路径
        Uri destinationUri = Uri.fromFile(new File(getContext().getExternalCacheDir(), "uCrop.jpg"));
        //使用UCrop类的静态方法传入两个URI实例化一个裁剪工具类，两个参数分别是裁剪前的图片URI和裁剪后的URI
        UCrop uCrop = UCrop.of(uri, destinationUri);
        uCrop.withAspectRatio(1, 1);//设置裁剪框的宽高比例
        //实例化UCrop的样式选择器
        UCrop.Options options = new UCrop.Options();
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(getContext(), R.color.colorPurple));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(getContext(), R.color.colorPurple));
        // 设置标题栏文字
        options.setToolbarTitle("移动和缩放");
        // 设置裁剪网格线的宽度(如果网格设置不显示，则没效果)
        options.setCropGridStrokeWidth(2);
        // 设置最大缩放比例
        options.setMaxScaleMultiplier(100);
        // 隐藏下边控制栏
        options.setHideBottomControls(false);
        // 设置是否显示裁剪网格
        options.setShowCropGrid(true);
        // 设置是否为圆形裁剪框
        options.setCircleDimmedLayer(true);
        //设置是否显示裁剪边框（true为方形边框）
        options.setShowCropFrame(false);
        uCrop.withOptions(options);
        uCrop.start(getContext(),MyFragment.this);
    }
    public void uploadImg(String filePath){
        File file = new File(filePath);
        //2.创建请求体对象
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/octet-stream"),
                file);
        //3.创建请求对象
        Request request = new Request.Builder()
                .post(requestBody)//使用post请求方法
                .url(Constant.BASE_URL + "UploadFileServlet")
                .build();
        //4.创建Call对象
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("上传文件结果",response.body().string());
            }
        });
    }

}
