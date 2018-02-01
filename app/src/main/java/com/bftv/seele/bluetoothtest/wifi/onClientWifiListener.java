package com.bftv.seele.bluetoothtest.wifi;

import android.net.wifi.WifiInfo;

import com.bftv.seele.bluetoothtest.bean.POWifiMsg;

/**
 * @author LiFei
 * @time 2018/1/19
 * @description:
 */
public interface onClientWifiListener {
    void onClientConnected(WifiInfo wifiInfo,POWifiMsg infoWifi);
    void onClientConnecting();
    void onClientDisConnect();
}
