package com.bftv.seele.bluetoothtest.bluetooth.ios;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public class IosServer {
    private String TAG = "IosServer";
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGattCharacteristic characteristicRead;
    private OnIosServerListener mIosServerListener;
    private BluetoothDevice mDevice;

    public void setIosServerListener(OnIosServerListener iosServerListener) {
        mIosServerListener = iosServerListener;
    }

    public IosServer(Context context, BluetoothManager mBluetoothManager, BluetoothAdapter mBluetoothAdapter) {
        mContext = context;
        this.mBluetoothManager = mBluetoothManager;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }




}
