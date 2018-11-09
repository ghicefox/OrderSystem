package com.example.ghicefox.ordersystem.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ghicefox.ordersystem.R;
import com.example.ghicefox.ordersystem.Utils.ToastUtils;
import com.github.shenyuanqing.zxingsimplify.zxing.Activity.CaptureActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PayActivity extends AppCompatActivity {
    private static final int REQUEST_SCAN = 0;
    private Context mContext;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
    }

    public void ScanCodes(View v)
    {
        startActivityForResult(new Intent(PayActivity.this, CaptureActivity.class), REQUEST_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            payMoney(data.getStringExtra("barCode"));
        }
    }
    private void payMoney(final String requestMenuStr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://22614b9s80.iask.in/api/payMoney?"+requestMenuStr)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    ToastUtils.showToast(PayActivity.this,responseData);
                    Intent intent = new Intent(PayActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
