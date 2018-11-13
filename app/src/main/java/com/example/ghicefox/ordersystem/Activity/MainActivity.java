package com.example.ghicefox.ordersystem.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ghicefox.ordersystem.R;
import com.example.ghicefox.ordersystem.Utils.ToastUtils;
import com.github.shenyuanqing.zxingsimplify.zxing.Activity.CaptureActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SCAN = 0;
    private Context mContext;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
    }

    //点餐按钮绑定事件
    public void Order(View v) {
        init();
    }

    //结账按钮绑定事件
    public void PayMoney(View v){
        startActivities(new Intent[]{new Intent(MainActivity.this,PayActivity.class)});
    }

    public void ScanCodes()
    {
        startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
    }


    private void init() {
        getRuntimeRight();
    }
    /*
     * 获得运行时权限
     */
    private void getRuntimeRight() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            ScanCodes();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ScanCodes();
                } else {
                    ToastUtils.showToast(MainActivity.this,"拒绝");
                }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            ToastUtils.showToast(MainActivity.this,data.getStringExtra("barCode"));
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra("requestMenuStr", data.getStringExtra("barCode"));
            startActivity(intent);
        }
    }


}
