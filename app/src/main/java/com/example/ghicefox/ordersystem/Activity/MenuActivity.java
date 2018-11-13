package com.example.ghicefox.ordersystem.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ghicefox.ordersystem.Models.MenuItem;
import com.example.ghicefox.ordersystem.Models.MenuItemInfo;
import com.example.ghicefox.ordersystem.Models.menu;
import com.example.ghicefox.ordersystem.R;
import com.example.ghicefox.ordersystem.Utils.RecyclerviewAdapter;
import com.example.ghicefox.ordersystem.Utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuActivity extends AppCompatActivity {
    private List<MenuItem> rightList = new ArrayList<MenuItem>();
    private int orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //初始化菜单
        String requestMenuStr = getIntent().getStringExtra("requestMenuStr");
        getMenuItems(requestMenuStr);
    }

    //获取总金额
    public int getTotalMoney()
    {
        TextView TotalMoney = (TextView) findViewById(R.id.item_TotalMoney);
        String totalMoney = TotalMoney.getText().toString();
        totalMoney = totalMoney.replace("元","");
        return Integer.parseInt(totalMoney);
    }

    //设置（初始化）总金额
    public void setTotalMoney(int newTotalMoney)
    {
        TextView TotalMoney = (TextView) findViewById(R.id.item_TotalMoney);
        TotalMoney.setText(String.valueOf(newTotalMoney)+"元");
    }

    //获取菜单项
    private void getMenuItems(final String requestMenuStr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://22614b9s80.iask.in/api/getMenu?"+requestMenuStr)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    InitMenu(responseData);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //根据获取的菜单项建立菜单
    private void initMenuItems(String responseData){
        //通过构造函数来获取
        Gson gson = new Gson();
        Type objectType = new TypeToken<List<menu>>() {}.getType();
        List<menu> menus = gson.fromJson(responseData, objectType);
        orderId = menus.get(0).getOrderId();
        int totalMoney = 0;
        //初始化金额
        TextView TotalMoney = (TextView) findViewById(R.id.item_TotalMoney);
        for(menu item :menus){
            MenuItem menuItem = new MenuItem(
                    item.getItemName(),
                    item.getPrice(),
                    item.getCount(),
                    item.getId(),
                    item.getOrderId());
            totalMoney += item.getCount() *item.getPrice();
            rightList.add(menuItem);
        }
        TotalMoney.setText(totalMoney+"元");
    }

    //初始化菜单
    private void InitMenu(final String responseData){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initMenuItems(responseData);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
                RecyclerviewAdapter adapter = new RecyclerviewAdapter(MenuActivity.this,rightList);
                recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    //下单
    public void pushOrder(View v){
        Gson gson = new Gson();
        String pushData = gson.toJson(rightList);
        pushToServer(pushData);
    }

    //提交订单到服务器
    private void pushToServer(final String pushData){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    FormBody formBody = new FormBody
                            .Builder()
                            .add("str",pushData)//设置参数名称和参数值
                            .build();
                    Request request = new Request.Builder()
                            .url("http://22614b9s80.iask.in/api/pushOrder")
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    ToastUtils.showToast(MenuActivity.this,responseData);
                    startActivities(new Intent[]{new Intent(MenuActivity.this,MainActivity.class)});
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //获取菜品详情
    public void getMenuItemInfo(final int menuId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://22614b9s80.iask.in/api/getMenuItemInfo?menuId="+menuId)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //通过构造函数来获取
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<MenuItemInfo>() {}.getType();
                    MenuItemInfo menuItemInfo = gson.fromJson(responseData, objectType);
                    Looper.prepare();
                    showDialog(menuItemInfo.getProfile(),stringToBitmap(menuItemInfo.getPicture()));
                    Looper.loop();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void showDialog(String profile, Bitmap picture)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(MenuActivity.this).inflate(R.layout.dialog_layout, null);
        // 设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        //这个位置十分重要，只有位于这个位置逻辑才是正确的
        final AlertDialog dialog = builder.show();
        ImageView menuItemPicture = view.findViewById(R.id.menuitem_picture);
        TextView menuItemProfile = view.findViewById(R.id.menuitem_profile);
        menuItemPicture.setImageBitmap(picture);
        menuItemProfile.setText(profile);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
