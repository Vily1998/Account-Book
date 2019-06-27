package com.zb.daily.UI.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.hjq.toast.ToastUtils;
import com.zb.daily.BuildConfig;
import com.zb.daily.Constant;
import com.zb.daily.MyApplication;
import com.zb.daily.R;
import com.zb.daily.UI.MainActivity;
import com.zb.daily.adapter.AssetsTransferDialogAdapter;
import com.zb.daily.adapter.RecordCategoryListAdapter;
import com.zb.daily.dao.AssetsDao;
import com.zb.daily.dao.CategoryDao;
import com.zb.daily.dao.RecordDao;
import com.zb.daily.model.Assets;
import com.zb.daily.model.Category;
import com.zb.daily.model.Record;

import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import com.zb.daily.util.FileUtil;
import com.zb.daily.view.CircleImageView;
import com.zb.daily.ClipImageActivity;

import static android.app.Activity.RESULT_OK;
import static com.zb.daily.util.FileUtil.getRealFilePathFromUri;


public class IndexRecordInFragment extends Fragment {

    FragmentActivity activity;

    private  String Location;

    private EditText textMoney;

    private EditText textRemark;

    private LinearLayout dateLayout;

    private TextView dateText;

    private EditText textPlace;

    private LinearLayout assetsLayout;

    private TextView assetsText;

    private ImageView categoryImage;

    private Button saveButton;

    private AssetsTransferDialogAdapter listAdapter;
    private AssetsDao assetsDao = new AssetsDao();
    private List<Assets> assetsList;
    private Assets currentAssets;
    private Category currentCategory;

    private CategoryDao categoryDao = new CategoryDao();
    private RecordDao recordDao = new RecordDao();
    List<Category> categoryImageList;

    //默认图标
    int defaultImage = 0;
    //默认资产
    int defaultAssets = 1;
    //默认日期
    String defaultDate = "";

    //新选择的图标
    int categoryNewImage = 0;

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;

    //头像2
    private ImageView headImage2;
    //调用照相机返回图片文件
    private File tempFile;
    // 1: qq, 2: weixin
    private int type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        Bundle bundle = getArguments();
        Location = bundle.getString("data");
        View view = inflater.inflate(R.layout.fragment_index_record_in, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textMoney = activity.findViewById(R.id.fragment_index_record_in_money_text);
        textRemark = activity.findViewById(R.id.fragment_index_record_in_remark_text);
        dateLayout = activity.findViewById(R.id.fragment_index_record_in_date);
        dateText = activity.findViewById(R.id.fragment_index_record_in_date_text);
        textPlace =activity.findViewById(R.id.fragment_index_record_in_place_text);
        assetsLayout = activity.findViewById(R.id.fragment_index_record_in_assets);
        assetsText = activity.findViewById(R.id.fragment_index_record_in_assets_text);
        categoryImage = activity.findViewById(R.id.fragment_index_record_in_category_image);
        saveButton = activity.findViewById(R.id.fragment_index_record_in_btn_save);

        headImage2 = (ImageView) activity.findViewById(R.id.head_image2);

        RelativeLayout weixinLayout = (RelativeLayout) activity.findViewById(R.id.weixinLayout);
        weixinLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.print("**********************************");
                type = 2;
                uploadHeadImage();
            }

        });

        textPlace.setText(Location);

        assetsList = assetsDao.findAssetsList();
        listAdapter = new AssetsTransferDialogAdapter(MyApplication.getContext(), assetsList);
        categoryImageList = categoryDao.findCategoryListByType(2);

        defaultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        currentCategory = categoryImageList.get(0);
        currentAssets = assetsList.get(defaultAssets);

        defaultImage = currentCategory.getImageId();
        categoryNewImage = currentCategory.getImageId();

        assetsText.setText(currentAssets.getName());
        categoryImage.setImageResource(defaultImage);
        dateText.setText(defaultDate);

        //设置图标list的适配器
        RecyclerView categoryImageRecyclerView = activity.findViewById(R.id.fragment_index_record_in_add_image_recyclerView);
        StaggeredGridLayoutManager categoryImageLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        categoryImageRecyclerView.setLayoutManager(categoryImageLayoutManager);
        RecordCategoryListAdapter categoryImageAdapter = new RecordCategoryListAdapter(categoryImageList, defaultImage);
        //adapter与Activity的数据交互
        categoryImageAdapter.setSubClickListener(new RecordCategoryListAdapter.SubClickListener() {
            @Override
            public void OnTopicClickListener(View v, int position) {
                currentCategory = categoryImageList.get(position);
                categoryImage.setImageResource(currentCategory.getImageId());
                categoryNewImage = currentCategory.getImageId();
            }
        });
        categoryImageRecyclerView.setAdapter(categoryImageAdapter);

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        assetsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetsAdapterDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = textMoney.getText().toString().trim();
                if (money.isEmpty()){
                    ToastUtils.show("金额不能为空");
                    return;
                }
                String remark = textRemark.getText().toString().trim();

                Record temp = new Record(Double.valueOf(money), defaultDate, remark, 2,
                        currentCategory.getId(), currentCategory.getImageId(), currentCategory.getName(),
                        currentAssets.getId(), currentAssets.getName());
                if (recordDao.saveRecord(temp)){
                    if (currentAssets.getType() == 1){
                        assetsDao.addBalance(currentAssets, money);
                    }else {
                        assetsDao.removeBalance(currentAssets, money);
                    }
                    ToastUtils.show("保存成功");
                    showChooseDialog();
                }
            }
        });
    }

    //日期弹出框
    private void showDateDialog() {
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_assets_transfer_dialog_date, null);
        final DatePicker dateTime = view.findViewById(R.id.activity_assets_transfer_dialog_date_pick);
        dateTime.updateDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int month = dateTime.getMonth() + 1;
                String monthStr = "";
                if (month < 10){
                    monthStr = "0" + month;
                }else {
                    monthStr = "" + month;
                }

                int day = dateTime.getDayOfMonth();
                String dayStr = "";
                if (day < 10){
                    dayStr = "0" + day;
                }else {
                    dayStr = "" + day;
                }

                String time = "" + dateTime.getYear() + "-" + monthStr + "-" + dayStr;
                defaultDate = time;
                dateText.setText(time);
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        //自动弹出键盘问题解决
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //账户弹出框
    private void assetsAdapterDialog() {
        AlertDialog.Builder listDialog = new AlertDialog.Builder(activity);
        listDialog.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                currentAssets = assetsList.get(which);
                assetsText.setText(currentAssets.getName());
            }
        });
        listDialog.show();
    }

    //添加完记录的选择框
    private void showChooseDialog(){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您要继续记录么？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textMoney.setText("");
                        textRemark.setText("");
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        MainActivity.actionStart(activity, Constant.TO_INDEX_FRAGMENT);
                        activity.finish();
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 上传头像
     */
    private void uploadHeadImage() {

        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        View parent = LayoutInflater.from(this.getActivity()).inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = 0.5f;
        activity.getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                activity.getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gotoCamera();
                //权限判断
                if(ContextCompat.checkSelfPermission(
                        activity,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                {ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        1);
                }


                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("evan", "*****************申请WRITE_EXTERNAL_STORAGE权限********************");
                    //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到调用系统相机
                    gotoCamera();
                }
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gotoPhoto();
                //权限判断
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("evan", "*****************申请READ_EXTERNAL_STORAGE权限********************");
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到相册
                    gotoPhoto();
                }
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    /**
     * 外部存储权限申请返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoCamera();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoPhoto();
            }
        }
    }


    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Log.d("evan", "*****************打开图库********************");
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID , tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(this.getActivity(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    if (type == 2) {
                       headImage2.setImageBitmap(bitMap);
                    }
                    //此处后面可以将bitMap转为二进制上传后台网络
                    //......

                }
                break;
        }
    }


    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }



















}
