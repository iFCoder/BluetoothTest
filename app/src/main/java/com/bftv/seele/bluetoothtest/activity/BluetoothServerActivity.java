package com.bftv.seele.bluetoothtest.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bftv.seele.bluetoothtest.R;
import com.bftv.seele.bluetoothtest.bean.POWifiMsg;
import com.bftv.seele.bluetoothtest.bean.PTOMsg;
import com.bftv.seele.bluetoothtest.bean.ServerDeviceInfo;
import com.bftv.seele.bluetoothtest.bluetooth.BaseHandler;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothChatService;
import com.bftv.seele.bluetoothtest.bluetooth.Constants;
import com.bftv.seele.bluetoothtest.utils.ToastUtil;
import com.bftv.seele.bluetoothtest.utils.WifiUtil;
import com.bftv.seele.bluetoothtest.wifi.BFWifiManagers;
import com.bftv.seele.bluetoothtest.wifi.onServerWifiListener;

/**
 * @author LiFei
 * @time 2018/1/23
 * @description:  蓝牙服务端
 */
public class BluetoothServerActivity extends AppCompatActivity {
    private String TAG = "tag";
    private ServerDeviceInfo mServerDeviceInfo;
    /**
     * 服务端当前连接的wifi信息
     */
    private WifiInfo wifiInfo;
    private TextView tvServer, btoothName, connectName, isAndroid, isIos, isWifi, isWifiName;
    private LinearLayout btoothInfo, connectInfo, deviceInfo;
    private CheckBox cbSwitch;
    private MenuItem loading;


    private BlutToothWifiState blutToothWifiState;
    private BluetoothChatService bluetoothChatService;
    private BluetoothAdapter bluetoothAdapter;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_server);
        //初始化服务端信息
        initServer();

        initView();
       // bluetoothChatService = new BluetoothChatService(BluetoothServerActivity.this, new ServerHandler());
        bluetoothChatService = BluetoothChatService.getInstance(BluetoothServerActivity.this,new ServerHandler());
        initBrocast();

    }

    private void initServer() {
        mServerDeviceInfo = new ServerDeviceInfo();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //获取当前已连接的wifi信息
        //mCurrentWifiInfo = WifiUtil.getConnectWifiInfo(wifiManager);
    }

    /**
     * 初始化广播
     */
    private void initBrocast() {
        blutToothWifiState = new BlutToothWifiState();
        IntentFilter btoothFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        IntentFilter wifiConnectFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(blutToothWifiState, btoothFilter);
        registerReceiver(blutToothWifiState, wifiFilter);
        registerReceiver(blutToothWifiState, wifiConnectFilter);
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Server");
        cbSwitch = findViewById(R.id.cb_switch);
        btoothInfo = findViewById(R.id.btooth_info);
        btoothName = findViewById(R.id.btooth_name);
        btoothName.setText(bluetoothAdapter.getName());
        mServerDeviceInfo.setName(bluetoothAdapter.getName());
        connectInfo = findViewById(R.id.ll_connect_info);
        connectName = findViewById(R.id.tv_connect_Name);
        tvServer = findViewById(R.id.tv_server);

        deviceInfo = findViewById(R.id.device_info);
        isAndroid = findViewById(R.id.is_android);
        isIos = findViewById(R.id.is_ios);
        isWifi = findViewById(R.id.is_wifi);
        isWifiName = findViewById(R.id.is_wifi_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter.isEnabled()) {
            cbSwitch.setChecked(true);
            btoothInfo.setVisibility(View.VISIBLE);
            deviceInfo.setVisibility(View.VISIBLE);
            setWifiInfo();
            startChatService();
        } else {
            cbSwitch.setChecked(false);
            btoothInfo.setVisibility(View.INVISIBLE);
            deviceInfo.setVisibility(View.INVISIBLE);
        }

        cbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //关闭蓝牙
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    //打开蓝牙
                } else {
                    bluetoothAdapter.enable();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.progressbar_menu, menu);
        loading = menu.findItem(R.id.action_loading);
        loading.setActionView(R.layout.actionbar_progress);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (bluetoothChatService != null) {
                    bluetoothChatService.stop();
                }
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 监听蓝牙打开，关闭状态
     */
    class BlutToothWifiState extends BroadcastReceiver {
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
                        cbSwitch.setChecked(true);
                        btoothInfo.setVisibility(View.VISIBLE);
                        deviceInfo.setVisibility(View.VISIBLE);
                        setWifiInfo();
                        startChatService();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        ToastUtil.showShort("蓝牙关闭中...");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        ToastUtil.showShort("蓝牙已关闭");
                        cbSwitch.setChecked(false);
                        btoothInfo.setVisibility(View.INVISIBLE);
                        deviceInfo.setVisibility(View.INVISIBLE);
                        if (bluetoothChatService != null) {
                            bluetoothChatService.stop();
                        }
                        break;
                    default:
                        break;
                }
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                //获取当前的wifi状态
                int mWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (mWifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //WIFI已打开
                        setWifiInfo();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //WIFI已关闭
                        setWifiInfo();
                        break;
                    default:
                        break;
                }

            }else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    isWifiName.setText(wifiInfo.getSSID());
                    mServerDeviceInfo.setWifiName(wifiInfo.getSSID());
                }
            }
        }
    }

    /**
     * 开启通讯服务
     */
    private void startChatService() {
        if (bluetoothChatService != null) {
            if (bluetoothChatService.getState() == BluetoothChatService.STATE_NONE) {
                bluetoothChatService.start();
                mServerDeviceInfo.setAndroid(true);
                mServerDeviceInfo.setIos(true);
                isAndroid.setText("支持");
                isIos.setText("支持");
            }
        }
    }

    /**
     * 设置WIFI信息
     */
    private void setWifiInfo() {
        if (WifiUtil.isWifiEnable(wifiManager)) {
            mServerDeviceInfo.setWifi(true);
            isWifi.setText("已开启");
            wifiInfo = WifiUtil.getConnectWifiInfo(wifiManager);
            if (wifiInfo != null) {
                isWifiName.setText(wifiInfo.getSSID());
            } else {
                isWifiName.setText("暂未连接");
            }

        } else {
            mServerDeviceInfo.setWifi(false);
            isWifi.setText("不可用");
            isWifiName.setText("不可用");
        }
    }

    /**
     * 服务端响应操作
     */
    private class ServerHandler extends BaseHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.e(TAG,"与该设备已建立连接");
                            Log.e(TAG,mServerDeviceInfo.toString());
                            if (mServerDeviceInfo.toString().length() > 0) {
                                PTOMsg ptoWifiMsg = new PTOMsg();
                                ptoWifiMsg.setData(JSON.toJSONString(mServerDeviceInfo));
                                ptoWifiMsg.setType(Constants.SERVER);
                                ptoWifiMsg.setMsg("可开始设置WiFi了");

                                //发送自已的信息
                                if (mServerDeviceInfo.toString().length() > 0) {
                                    byte[] send = JSON.toJSONString(ptoWifiMsg).getBytes();
                                    bluetoothChatService.write(send);
                                }
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.e(TAG,"正在建立连接....");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                        default:
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    if (loading != null) {
                        loading.setVisible(false);
                    }
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    PTOMsg pto = JSON.parseObject(readMessage, PTOMsg.class);
                    if(pto != null){
                        if(pto.getData() != null){
                            //接收客户端发来的WiFi信息，并连接
                            tvServer.setText("Msg:" + readMessage);
                            serverConnectWifi(JSON.parseObject(pto.getData().toString(),POWifiMsg.class));
                        }else{
                           // ToastUtil.showShort(pto.getMsg());
                        }
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (loading != null) {
                        loading.setVisible(true);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private static int dis= 1;
    private static int con = 1;
    private void serverConnectWifi(final POWifiMsg poWifiMsg) {
        if (WifiUtil.getConnectWifiBssid(this).equals(poWifiMsg.getBSSID())) {
            ToastUtil.showShort("您已连接该网络");
            PTOMsg ptoMsg = new PTOMsg();
            ptoMsg.setType(Constants.SERVER);
            ptoMsg.setMsg("对接设备已连接该网络");
            bluetoothChatService.write(JSON.toJSONString(ptoMsg).getBytes());
            return;
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BFWifiManagers bfWifiManagers = new BFWifiManagers(this, wifiManager, poWifiMsg, false);
        bfWifiManagers.connectWifi();
        bfWifiManagers.setOnServerWifiListener(new onServerWifiListener() {
            @Override
            public void onServerConnected(WifiInfo wifiInfo, POWifiMsg infoWifi) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "";
                        PTOMsg ptoWifiMsg = new PTOMsg();
                        if(con == 1){
                            msg = "开始连接";
                            con --;
                        }else{
                            mServerDeviceInfo.setName(bluetoothAdapter.getName());
                            ptoWifiMsg.setData(JSON.toJSONString(mServerDeviceInfo));
                            msg = "对接设备WiFi连接成功";
                            con=1;
                        }
                        ptoWifiMsg.setType(Constants.SUCCESS);
                        ptoWifiMsg.setMsg(msg);
                        bluetoothChatService.write(JSON.toJSONString(ptoWifiMsg).getBytes());
                        dis =1;
                    }
                }, 400);
            }

            @Override
            public void onServerConnecting() {
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PTOMsg ptoWifiMsg = new PTOMsg();
                        ptoWifiMsg.setType(Constants.SUCCESS);
                        ptoWifiMsg.setMsg("正在连接....");
                        bluetoothChatService.write(JSON.toJSONString(ptoWifiMsg).getBytes());
                    }
                },300);*/
            }

            @Override
            public void onServerDisConnect() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(dis == 1){
                            dis --;
                            PTOMsg ptoWifiMsg = new PTOMsg();
                            ptoWifiMsg.setType(Constants.SUCCESS);
                            ptoWifiMsg.setMsg("正在断开WiFi");
                            bluetoothChatService.write(JSON.toJSONString(ptoWifiMsg).getBytes());
                        }
                    }
                },100);

            }
        });




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothChatService != null) {
            bluetoothChatService.stop();
        }
        unregisterReceiver(blutToothWifiState);
    }
}
