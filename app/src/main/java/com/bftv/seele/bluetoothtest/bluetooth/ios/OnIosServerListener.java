package com.bftv.seele.bluetoothtest.bluetooth.ios;

import android.bluetooth.BluetoothDevice;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public interface OnIosServerListener {
    void onSuccess();
    void onMsg(String msg);
    void onDeviceInfo(BluetoothDevice device);
}
