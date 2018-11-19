package com.example.ghicefox.ordersystem.Activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ghicefox.ordersystem.R;
import com.example.ghicefox.ordersystem.Utils.ToastUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueTooth extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //获取系统蓝牙适配器管理类
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ListView lvDevices;
    private List<String> bluetoothDevices = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    private final UUID MY_UUID = UUID
            .fromString("abcd1234-ab12-ab12-ab12-abcdef123456");//随便定义一个
    private BluetoothSocket clientSocket;
    private BluetoothDevice device;
    private OutputStream os;//输出流
    private AcceptThread acceptThread;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream is;

    public void BeginSearch(View v){
        //如果当前在搜索，就先取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //开启搜索
        mBluetoothAdapter.startDiscovery();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        // 询问打开蓝牙
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvDevices = (ListView) findViewById(R.id.lv_devices);
        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDevices.add(device.getName() + ":"+ device.getAddress());
            }
        }
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,bluetoothDevices);
        lvDevices.setAdapter(arrayAdapter);
        lvDevices.setOnItemClickListener(this);//Activity实现OnItemClickListener接口
        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    /**
     * 定义广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    bluetoothDevices.add(device.getName() + ":" + device.getAddress());
                    arrayAdapter.notifyDataSetChanged();//更新适配器
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //已搜素完成
            }
        }
    };

    // 申请打开蓝牙请求的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ToastUtils.showToast(BlueTooth.this,"蓝牙已经开启");
            } else if (resultCode == RESULT_CANCELED) {
                ToastUtils.showToast(BlueTooth.this,"没有蓝牙权限");
                finish();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = arrayAdapter.getItem(position);
        String address = s.substring(s.indexOf(":") + 1).trim();//把地址解析出来
        //主动连接蓝牙服务端
        try {
            //判断当前是否正在搜索
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            try {
                if (device == null) {
                    //获得远程设备
                    device = mBluetoothAdapter.getRemoteDevice(address);
                }
                if (clientSocket == null) {
                    //创建客户端蓝牙Socket
                    clientSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    //开始连接蓝牙，如果没有配对则弹出对话框提示我们进行配对
                    clientSocket.connect();
                    //获得输出流（客户端指向服务端输出文本）
                    os = clientSocket.getOutputStream();
                }
            } catch (Exception e) {
            }
            if (os != null) {
                //往服务端写信息
                os.write("蓝牙信息来了".getBytes("utf-8"));
            }
        } catch (Exception e) {
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), String.valueOf(msg.obj),
                    Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    };

    //服务端监听客户端的线程类
    private class AcceptThread extends Thread {
        public AcceptThread() {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MyServer", UUID.fromString("abcd1234-ab12-ab12-ab12-abcdef123456"));
            } catch (Exception e) {
            }
        }
        public void run() {
            try {
                socket = serverSocket.accept();
                is = socket.getInputStream();
                while(true) {
                    byte[] buffer =new byte[1024];
                    int count = is.read(buffer);
                    Message msg = new Message();
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    handler.sendMessage(msg);
                }
            }
            catch (Exception e) {
            }
        }
    }
}
