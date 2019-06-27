package com.zb.daily.UI.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;
import com.zb.daily.Constant;
import com.zb.daily.MyApplication;
import com.zb.daily.R;
import com.zb.daily.UI.MainActivity;
import com.zb.daily.UI.RecordAddActivity;
import com.zb.daily.adapter.AssetsMainListAdapter;
import com.zb.daily.adapter.RecordMainListAdapter;
import com.zb.daily.dao.RecordDao;
import com.zb.daily.model.Record;
import com.zb.daily.util.SPUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class IndexFragment extends Fragment {

    public FragmentActivity activity;

    //悬浮按钮
    private FloatingActionButton fab;
    //滑动菜单
    private DrawerLayout drawerLayout;
    //菜单按钮
    private Button menuButton;

    private Button updatebtn;

    private Button downloadbtn;

    private List<Record> recordList = new ArrayList<>();
    private RecordDao recordDao = new RecordDao();

    private TextView monthIncome;
    private TextView monthOutlay;
    private TextView monthBudget;
    private LinearLayout linearLayout;

    static RecordMainListAdapter adapter = null;

    public static RecyclerView.Adapter getRecordAdapter() {
        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //用Toolbar替换ActionBar
        setHasOptionsMenu(true);
        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        Toolbar toolbar=  appCompatActivity.findViewById(R.id.fragment_index_toolbar);
        toolbar.setTitle("");
        appCompatActivity.setSupportActionBar(toolbar);

        //菜单按钮打开滑动窗口
        menuButton = activity.findViewById(R.id.fragment_index_btn_menu);
        drawerLayout = activity.findViewById(R.id.drawer_layout);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//打开滑动菜单
            }
        });

        //悬浮按钮点击事件
        fab = activity.findViewById(R.id.fragment_index_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAddActivity.actionStart(activity);
            }
        });


        updatebtn = activity.findViewById(R.id.update_btn);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("evan", "*****************成功********************");
                upload();

            }
        });

        downloadbtn = activity.findViewById(R.id.download_btn);
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("evan", "*****************成功********************");
                download();

            }
        });

        monthBudget = activity.findViewById(R.id.tv_budget);
        monthIncome = activity.findViewById(R.id.tv_month_income);
        monthOutlay = activity.findViewById(R.id.tv_month_outlay);
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        double out = recordDao.getMonthSummary(month, 1);
        double in = recordDao.getMonthSummary(month, 2);
        monthIncome.setText(String.valueOf(in));
        monthOutlay.setText(String.valueOf(out));
        String budget = (String) SPUtil.get(activity, "month_budget", "0");
        if (budget.equals("0")){
            monthBudget.setText("设置预算");
        }else {
            monthBudget.setText(String.valueOf(Double.valueOf(budget) - out));
        }
        linearLayout = activity.findViewById(R.id.fragment_index_budget);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actionStart(activity, Constant.TO_SETTING_FRAGMENT);
            }
        });

        recordList = recordDao.findRecordList();


        //记录的list适配
        RecyclerView recyclerView = activity.findViewById(R.id.fragment_index_recordRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecordMainListAdapter(recordList);
        recyclerView.setAdapter(adapter);

        adapter.setSubClickListener(new RecordMainListAdapter.SubClickListener() {
            @Override
            public void OnTopicClickListener(String s) {
                String month = new SimpleDateFormat("yyyy-MM").format(new Date());
                double out = recordDao.getMonthSummary(month, 1);
                double in = recordDao.getMonthSummary(month, 2);
                monthIncome.setText(String.valueOf(in));
                monthOutlay.setText(String.valueOf(out));
                String budget = (String) SPUtil.get(activity, "month_budget", "0");
                if (budget.equals("0")){
                    monthBudget.setText("设置预算");
                }else {
                    monthBudget.setText(String.valueOf(Double.valueOf(budget) - out));
                }
            }
        });
    }


    private void download() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8080/update/download")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String jsonData;
                        jsonData=response.body().string();
                        Log.d("kwwl",jsonData);

                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());
                        //Log.d("kwwl","res=="+response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        if (recordDao.deleteallRecord()){
                            Log.d("kwwl","********清空成功*********");
                        }
                        try
                        {
                            /*

                            private Integer id;
                            private Double money;//钱数
                            private String date;//日期
                            private String remark;//备注
                            private Integer type;//类型：1是支出记录，2是收入记录
                            private Integer category_id;//分类id
                            private Integer  category_imageId; //分类图片
                            private String  category_name; //分类名
                            private Integer assets_id; //资产id
                            private String  assets_name;//资产名
                             */
                            jsonData=jsonData.substring(13,jsonData.length()-1);
                            jsonData=jsonData.replace("\\","");
                            //jsonData= "[{\"Money\":100,\"Date\":\"2019-06-21 00:00:00\",\"Remark\":\"\",\"Type\":1,\"Category_id\":1,\"Asset_id\":0,\"Asset_name\":\"\",\"Category_name\":\"0\",\"Id\":0,\"Category_imageId\":0},{\"Money\":50,\"Date\":\"2019-06-21 00:00:00\",\"Remark\":\"\",\"Type\":1,\"Category_id\":1,\"Asset_id\":0,\"Asset_name\":\"\",\"Category_name\":\"0\",\"Id\":0,\"Category_imageId\":0}]";
                            Log.d("kwwl",jsonData);
                            JSONArray jsonArray = new JSONArray(jsonData);
                            for (int i=0; i < jsonArray.length(); i++)    {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Log.d("kwwl","*************11111111111111*****************");
                                Integer id = jsonObject.getInt("Id");
                                Integer type = jsonObject.getInt("Type");
                                Integer category_id = jsonObject.getInt("Category_id");
                                Integer category_imageId = jsonObject.getInt("Category_imageId");
                                Integer assets_id = jsonObject.getInt("Asset_id");
                                String date = jsonObject.getString("Date");
                                String remark = jsonObject.getString("Remark");
                                String category_name = jsonObject.getString("Category_name");
                                String assets_name = jsonObject.getString("Asset_name");
                                double money = jsonObject.getDouble("Money");



                                Record temp = new Record(money, date, remark, type,
                                        category_id, category_imageId, category_name,
                                        assets_id, assets_name);
                                if (recordDao.saveRecord(temp)){
                                    Log.d("kwwl","*********插入成功*********");
                                }
                            }
                            MainActivity.actionStart(activity, Constant.TO_INDEX_FRAGMENT);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }




                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();



        AlertDialog.Builder builder  = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("确认" ) ;
        builder.setMessage("同步成功" ) ;
        builder.setPositiveButton("是" ,  null );
        builder.show();
    }

    private void upload() {

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式


        recordList = recordDao.findRecordList();
        String jsonStr = "{\"result\":[";//json数据.
        for(int i=0;i<recordList.size();i=i+1)
        {
            //Log.d("kwwl",recordList.get(i).tojson());
            if(i==0) jsonStr+=recordList.get(i).tojson();
            else jsonStr+=","+recordList.get(i).tojson();
        }
        jsonStr+="]}";
        Log.d("kwwl",jsonStr);

        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url("http://192.168.137.1:8080/update/upload")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("kwwl","***************fail*********");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    Log.d("kwwl","***************success*********");
                    Log.d("kwwl","获取数据成功了");
                    Log.d("kwwl","response.code()=="+response.code());
                    Log.d("kwwl","response.body().string()=="+response.body().string());
                }
            }
        });



        AlertDialog.Builder builder  = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("确认" ) ;
        builder.setMessage("上传成功" ) ;
        builder.setPositiveButton("是" ,  null );
        builder.show();
    }


}
