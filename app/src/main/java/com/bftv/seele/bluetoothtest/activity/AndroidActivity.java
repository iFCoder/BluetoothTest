package com.bftv.seele.bluetoothtest.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bftv.seele.bluetoothtest.R;
import com.bftv.seele.bluetoothtest.bean.POWifiMsg;
import com.bftv.seele.bluetoothtest.bluetooth.BaseHandler;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothBroadcastRece;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothChatService;
import com.bftv.seele.bluetoothtest.bluetooth.adapter.WifiInfoAdapter;
import com.bftv.seele.bluetoothtest.bluetooth.adapter.WifiInfos;
import com.bftv.seele.bluetoothtest.bluetooth.onReceiverListener;
import com.bftv.seele.bluetoothtest.utils.ToastUtil;
import com.bftv.seele.bluetoothtest.wifi.WifiActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AndroidActivity extends AppCompatActivity implements onReceiverListener {

    private Button sendInfo;
    private EditText etPwd;
    private ListView mListView;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pList;
    private List<WifiInfos> infoList = new ArrayList<>();
    private WifiInfoAdapter wifiInfoAdapter;
    private BluetoothBroadcastRece mReceive;
    private BluetoothChatService bluetoothChatService;

    /**
     * 需要通过蓝牙传递连接WIFI的信息
     */
    private String needWifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);
        initToolBar();
        initView();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            getBluetoothList();
        } else {
            bluetoothAdapter.enable();
        }
        //注册监听
        mReceive = new BluetoothBroadcastRece(this);
        IntentFilter connectedFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceive, connectedFilter);
        bluetoothChatService = new BluetoothChatService(AndroidActivity.this, new ClientHandler());
    }

    private void initToolBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Client");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        etPwd = findViewById(R.id.et_pwd);
        sendInfo = findViewById(R.id.send_info);
        mListView = findViewById(R.id.lv_wifi);
        wifiInfoAdapter = new WifiInfoAdapter(this, infoList);
        mListView.setAdapter(wifiInfoAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (bluetoothChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                    ToastUtil.showShort("已经连接了");
                    return;
                }
                String address = infoList.get(i).getAddress();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                bluetoothChatService.connect(device, false);
            }
        });
        //发送
        sendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPwd(needWifiInfo);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter.isEnabled()) {
            if (bluetoothChatService != null) {
                if (bluetoothChatService.getState() == BluetoothChatService.STATE_NONE) {
                    bluetoothChatService.start();
                }
            }
        } else {
            bluetoothAdapter.enable();
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "配对成功", Toast.LENGTH_SHORT).show();
        getBluetoothList();
    }

    @Override
    public void onDelete() {
        getBluetoothList();
    }

    @Override
    public void onBonding() {
    }

    /**
     * 获取已经配对的设备列表
     */
    private void getBluetoothList() {
        pList = bluetoothAdapter.getBondedDevices();
        if (pList != null && pList.size() > 0) {
            for (BluetoothDevice bluetoothDevice : pList) {
                WifiInfos wifiInfos = new WifiInfos();
                wifiInfos.setName(bluetoothDevice.getName());
                wifiInfos.setAddress(bluetoothDevice.getAddress());
                wifiInfos.setBondState(bluetoothDevice.getBondState());
                wifiInfos.setDeviceType(bluetoothDevice.getBluetoothClass().getDeviceClass());
                infoList.add(wifiInfos);
            }
        }
        wifiInfoAdapter.notifyDataSetChanged();
    }

    /**
     * 发送数据
     *
     * @param pwd
     */
    private void sendPwd(String pwd) {
        if (TextUtils.isEmpty(etPwd.getText().toString().trim())) {
            Toast.makeText(AndroidActivity.this, "WIFI密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("WifiActivity", needWifiInfo);
        if (bluetoothChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "与该设备还未建立连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwd.length() > 0) {
            byte[] send = pwd.getBytes();
            bluetoothChatService.write(send);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 500 && resultCode == 501) {
            POWifiMsg infoWifi = data.getParcelableExtra("INFO");
            etPwd.setText(infoWifi.getPwd());
            Log.e("WifiActivity", infoWifi.getSSID() + "-----" + infoWifi.getBSSID() + "-----" + infoWifi.getPwd() + "----" + infoWifi.getWifiType());
            needWifiInfo = infoWifi.getPwd() + "@" + infoWifi.getSSID() + "@" + infoWifi.getBSSID() + "@" + infoWifi.getWifiType();
            Log.e("WifiActivity", needWifiInfo);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothChatService != null) {
            bluetoothChatService.stop();
        }
        unregisterReceiver(mReceive);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_bluetooth:
                getBluetooth();
                break;
            case R.id.action_server:
                break;
            case R.id.action_wifi:
                Intent wifi = new Intent(AndroidActivity.this, WifiActivity.class);
                startActivityForResult(wifi, 500);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 跳转到蓝牙系统设置界面
     */
    public void getBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }

    /**
     * 客户端响应操作
     */
    private class ClientHandler extends BaseHandler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
