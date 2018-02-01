package com.bftv.seele.bluetoothtest.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bftv.seele.bluetoothtest.R;
import com.bftv.seele.bluetoothtest.bean.POWifiMsg;
import com.bftv.seele.bluetoothtest.bean.PTOMsg;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothChatService;
import com.bftv.seele.bluetoothtest.bluetooth.Constants;
import com.bftv.seele.bluetoothtest.utils.ToastUtil;
import com.bftv.seele.bluetoothtest.utils.WifiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiFei
 * @time 2018/1/16
 * @description:
 */
public class WifiActivity extends AppCompatActivity {
    public static WifiActivity wifiInstance = null;
    private String TAG = "WifiActivity";
    private WifiManager wifiManager;
    private RecyclerView mRvWifi;
    private LinearLayout llPb;
    private List<ScanResult> wifiList = new ArrayList<>();
    private WifiAdapter wifiAdapter;
    private int wifiType;

    private WifiReceiver receiverWifi;
    private String pwd;
    private WifiInfo mCurrentWifiInfo;
    private POWifiMsg mfinalWifiInfo;
    private BluetoothChatService mChatService;

    /**
     * wifi管理
     */
    private BFWifiManagers bfWifiManagers;

    /**
     * 所要申请的权限
     */
    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE};

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiInstance = this;
        setContentView(R.layout.activity_wifi);
        initView();
        mChatService = BluetoothChatService.mChatService;

        initInfo();
        //  bluetoothChatService = BluetoothChatService.getInstance(WifiActivity.this,new BaseHandler());
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Wifi");
        mRvWifi = findViewById(R.id.rv_wifi);
        llPb = findViewById(R.id.ll_state);
    }

    private void initInfo() {
        wifiAdapter = new WifiAdapter(WifiActivity.this, wifiList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WifiActivity.this);
        mRvWifi.setLayoutManager(linearLayoutManager);
        mRvWifi.setAdapter(wifiAdapter);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        //获取当前已连接的wifi信息
        mCurrentWifiInfo = WifiUtil.getConnectWifiInfo(wifiManager);

        //注册扫描WIFI广播
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
    }


    private void connectWifi(final ScanResult result) {
        if (mCurrentWifiInfo != null) {
            if (mCurrentWifiInfo.getBSSID().equals(result.BSSID)) {
                ToastUtil.showShort("您已连接该网络");
                return;
            }
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(result.SSID)
                .setCancelable(false);
        View etDialog = View.inflate(this, R.layout.dialog_wifi, null);
        dialog.setView(etDialog);
        final EditText etPwd = etDialog.findViewById(R.id.et_dialog);
        dialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pwd = etPwd.getText().toString();
                if (null == pwd || pwd.length() < 8) {
                    Toast.makeText(WifiActivity.this, "密码至少8位", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (result.capabilities.contains("WEP")) {
                    wifiType = 1;
                } else if (result.capabilities.contains("WPA")) {
                    wifiType = 2;
                } else {
                    wifiType = 0;
                }

                POWifiMsg infoWifi = new POWifiMsg();
                infoWifi.setPwd(pwd);
                infoWifi.setWifiType(wifiType);
                infoWifi.setBSSID(result.BSSID);
                infoWifi.setSSID(result.SSID);

                bfWifiManagers = new BFWifiManagers(WifiActivity.this,
                        wifiManager,
                        infoWifi, true);
                bfWifiManagers.connectWifi();
                bfWifiManagers.setOnWifiListener(new onClientWifiListener() {
                    @Override
                    public void onClientConnected(WifiInfo wifiInfo, POWifiMsg infoWifi) {
                        wifiManager.startScan();
                        mfinalWifiInfo = infoWifi;
                    }

                    @Override
                    public void onClientConnecting() {

                    }

                    @Override
                    public void onClientDisConnect() {

                    }
                });
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.wifi_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_wifi_ok:
                if (mfinalWifiInfo != null) {
                    Log.e(TAG, mfinalWifiInfo.getSSID() + "-----" + mfinalWifiInfo.getBSSID());
                    POWifiMsg wifiMsg = new POWifiMsg();
                    wifiMsg.setPwd(mfinalWifiInfo.getPwd());
                    wifiMsg.setSSID(mfinalWifiInfo.getSSID());
                    wifiMsg.setBSSID(mfinalWifiInfo.getBSSID());
                    wifiMsg.setWifiType(mfinalWifiInfo.getWifiType());

                    PTOMsg ptoWifiMsg = new PTOMsg();
                    ptoWifiMsg.setData(JSON.toJSONString(wifiMsg));
                    ptoWifiMsg.setType(Constants.CLIENT);
                    ptoWifiMsg.setMsg("wifi信息");

                    if (wifiMsg.toString().length() > 0) {
                        byte[] send = JSON.toJSONString(ptoWifiMsg).getBytes();
                        mChatService.write(send);
                    }
                } else {
                    ToastUtil.showShort("请连接Wifi");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * wifi列表广播
     */
    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                //获取当前已连接的wifi信息
                mCurrentWifiInfo = WifiUtil.getConnectWifiInfo(wifiManager);
                wifiList.clear();
                wifiList.addAll(wifiManager.getScanResults());
                wifiAdapter.notifyDataSetChanged();
                llPb.setVisibility(View.GONE);
                wifiAdapter.onItemClickListeners(new onItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, ScanResult result) {
                        connectWifi(result);
                    }
                });
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiverWifi != null) {
            unregisterReceiver(receiverWifi);
        }
        if (bfWifiManagers != null) {
            bfWifiManagers.unregisterStateReceiver();
        }
    }


}
