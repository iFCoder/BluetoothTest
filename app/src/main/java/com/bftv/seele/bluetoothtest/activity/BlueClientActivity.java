package com.bftv.seele.bluetoothtest.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bftv.seele.bluetoothtest.R;
import com.bftv.seele.bluetoothtest.bean.PTOMsg;
import com.bftv.seele.bluetoothtest.bean.ServerDeviceInfo;
import com.bftv.seele.bluetoothtest.bluetooth.BaseHandler;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothChatService;
import com.bftv.seele.bluetoothtest.bluetooth.Constants;
import com.bftv.seele.bluetoothtest.bluetooth.adapter.WifiInfoAdapter;
import com.bftv.seele.bluetoothtest.bluetooth.adapter.WifiInfos;
import com.bftv.seele.bluetoothtest.utils.ToastUtil;
import com.bftv.seele.bluetoothtest.wifi.WifiActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author LiFei
 * @time 2018/1/26
 * @description: 蓝牙客户端
 */
public class BlueClientActivity extends AppCompatActivity {
    private String TAG = "tag";
    private LinearLayout llSwitch, llBtoothInfo, setWifi, sDeviceInfo;
    private CheckBox mCbSwitch;
    private TextView btoothName, sDeviceName, sIsAndroid, sIsIos, sIsWifi, sIsWifiName;
    private ListView lvDevice;

    private Set<BluetoothDevice> pList;
    private List<WifiInfos> infoList = new ArrayList<>();
    private WifiInfoAdapter wifiInfoAdapter;


    private BtoothWifiBrocast btoothWifiBrocast;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothChatService bluetoothChatService;

    private int nowConnectPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothChatService = BluetoothChatService.getInstance(BlueClientActivity.this, new ClientHandler());
        initBroadcast();
        initView();

        //获取已配对设备列表
        getBluetoothList();
    }

    /**
     * 初始化广播监听
     */
    private void initBroadcast() {
        btoothWifiBrocast = new BtoothWifiBrocast();
        //蓝牙打开关闭广播
        IntentFilter btoothFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        //蓝牙配对广播
        IntentFilter connectedFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(btoothWifiBrocast, btoothFilter);
        registerReceiver(btoothWifiBrocast, connectedFilter);
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Client");
        actionBar.setDisplayHomeAsUpEnabled(true);
        llSwitch = findViewById(R.id.ll_switch);
        llSwitch.setClickable(true);
        mCbSwitch = findViewById(R.id.cb_switch);
        mCbSwitch.setClickable(false);
        llBtoothInfo = findViewById(R.id.btooth_info);
        btoothName = findViewById(R.id.btooth_name);
        btoothName.setText(bluetoothAdapter.getName());
        lvDevice = findViewById(R.id.lv_device);
        wifiInfoAdapter = new WifiInfoAdapter(this, infoList);
        lvDevice.setAdapter(wifiInfoAdapter);
        setWifi = findViewById(R.id.set_wifi);

        sDeviceInfo = findViewById(R.id.device_info);
        sDeviceName = findViewById(R.id.device_name);
        sIsAndroid = findViewById(R.id.is_android);
        sIsIos = findViewById(R.id.is_ios);
        sIsWifi = findViewById(R.id.is_wifi);
        sIsWifiName = findViewById(R.id.is_wifi_name);

        llSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBluetooth();
            }
        });
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (bluetoothChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                    ToastUtil.showShort("已经连接了");
                    return;
                }
                nowConnectPosition = position;
                String address = infoList.get(position).getAddress();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                bluetoothChatService.connect(device, false);
            }
        });
        setWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wifi = new Intent(BlueClientActivity.this, WifiActivity.class);
                startActivity(wifi);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter.isEnabled()) {
            mCbSwitch.setChecked(true);
            llBtoothInfo.setVisibility(View.VISIBLE);
            lvDevice.setVisibility(View.VISIBLE);
            if (bluetoothChatService != null) {
                if (bluetoothChatService.getState() == BluetoothChatService.STATE_NONE) {
                    bluetoothChatService.start();
                }
            }
        } else {
            mCbSwitch.setChecked(false);
            llBtoothInfo.setVisibility(View.INVISIBLE);
            lvDevice.setVisibility(View.GONE);
            sDeviceInfo.setVisibility(View.GONE);
            setWifi.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 跳转到蓝牙系统设置界面
     */
    public void setBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }

    /**
     * 获取已经配对的设备列表
     */
    private void getBluetoothList() {
        infoList.clear();
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

    class BtoothWifiBrocast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        ToastUtil.showShort("蓝牙打开中...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        ToastUtil.showShort("蓝牙已打开");
                        llBtoothInfo.setVisibility(View.VISIBLE);
                        getBluetoothList();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        ToastUtil.showShort("蓝牙关闭中...");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        ToastUtil.showShort("蓝牙已关闭");
                        llBtoothInfo.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---->destory");
        if (bluetoothChatService != null) {
            bluetoothChatService.stop();
        }
        unregisterReceiver(btoothWifiBrocast);
    }

    /**
     * 客户端响应操作
     */
    private PTOMsg pto;

    private class ClientHandler extends BaseHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.e(TAG, "与该设备已建立连接");
                            sDeviceInfo.setVisibility(View.VISIBLE);
                            setWifi.setVisibility(View.VISIBLE);
                            if (infoList.size() > 0) {
                                WifiInfos data = infoList.get(nowConnectPosition);
                                data.setConnect(true);
                                sDeviceName.setText(data.getName() + "的设备信息");
                                wifiInfoAdapter.notifyDataSetChanged();
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.e(TAG, "正在建立连接....");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                        default:
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e(TAG, readMessage + "@@@@");
                    pto = JSON.parseObject(readMessage, PTOMsg.class);
                    if (pto != null) {
                        ToastUtil.showShort(pto.getMsg());
                        if (pto.getData() != null) {
                            ServerDeviceInfo info = JSON.parseObject(pto.getData().toString(), ServerDeviceInfo.class);
                            showConnectInfo(info);
                        }
                        if ((pto.getType() == Constants.SUCCESS) && (WifiActivity.wifiInstance != null)) {
                            WifiActivity.wifiInstance.finish();
                        }
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    nowConnectPosition = -1;
                    setWifi.setVisibility(View.GONE);
                    sDeviceInfo.setVisibility(View.GONE);
                    if (infoList.size() > 0) {
                        for (int i = 0; i < infoList.size(); i++) {
                            WifiInfos datas = infoList.get(i);
                            datas.setConnect(false);
                        }
                        wifiInfoAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 显示连接服务端设备信息
     *
     * @param info
     */
    private void showConnectInfo(ServerDeviceInfo info) {
        if (info.getAndroid()) {
            sIsAndroid.setText("支持");
        } else {
            sIsAndroid.setText("不支持");
        }
        if (info.getIos()) {
            sIsIos.setText("支持");
        } else {
            sIsIos.setText("不支持");
        }
        if (info.getWifi()) {
            sIsWifi.setText("已开启");
            sIsWifiName.setText(info.getWifiName());
        } else {
            sIsIos.setText("不可用");
            sIsWifiName.setText("不可用");
        }
    }
}
