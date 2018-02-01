package com.bftv.seele.bluetoothtest.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bftv.seele.bluetoothtest.R;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothType;
import com.bftv.seele.bluetoothtest.utils.ToastUtil;
import com.bftv.seele.bluetoothtest.wifi.BLEManager;

import java.util.Set;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public class IOSActivity extends AppCompatActivity {
    private String TAG = "IOSActivity";
    private Button btnMsg;
    private ImageView btoothType;
    private TextView tvNetInfo, tvMsgInfo;
    private EditText etMsg;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pList;
    private BluetoothManager mBluetoothManager;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ios);
        initView();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            getBluetoothList();
        } else {
            bluetoothAdapter.enable();
        }
        final BLEManager bleManager = new BLEManager(this, mBluetoothManager, bluetoothAdapter, tvMsgInfo);
        bleManager.initGATTServer();

        //发送消息
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                if (tvNetInfo.equals("无")) {
                    ToastUtil.showShort("暂无连接的蓝牙设备");
                    return;
                }
                if (TextUtils.isEmpty(etMsg.getText().toString().trim())) {
                    ToastUtil.showShort("请输入信息");
                    return;
                }
                bleManager.reponseMsg(etMsg.getText().toString().trim());
            }
        });
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Client");
        btnMsg = findViewById(R.id.btn_msg);
        btoothType = findViewById(R.id.iv_btooth_type);
        tvNetInfo = findViewById(R.id.tv_net_info);
        tvMsgInfo = findViewById(R.id.tv_msg_info);
        etMsg = findViewById(R.id.et_msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tvNetInfo.setText("");
        getBluetoothList();
    }

    /**
     * 获取已经配对的设备列表
     */
    private void getBluetoothList() {
        pList = bluetoothAdapter.getBondedDevices();
        if (pList != null && pList.size() > 0) {
            for (BluetoothDevice bluetoothDevice : pList) {
                if (bluetoothDevice.getBluetoothClass().getDeviceClass() == BluetoothType.PHONE_SMART) {
                    btoothType.setImageResource(R.mipmap.icon_mobile_unselect);
                } else if (bluetoothDevice.getBluetoothClass().getDeviceClass() == BluetoothType.COMPUTER_LAPTOP) {
                    btoothType.setImageResource(R.mipmap.icon_computer);
                }
                tvNetInfo.setText(bluetoothDevice.getName() + "          " + bluetoothDevice.getAddress() + "\n");
            }
        } else {
            tvNetInfo.setText("无");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem server =  menu.findItem(R.id.action_server);
        MenuItem wifi =  menu.findItem(R.id.action_wifi);
        server.setVisible(false);
        wifi.setVisible(false);
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
            case R.id.action_wifi:
                ToastUtil.showShort("无");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 系统蓝牙界面
     */
    public void getBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }
}
