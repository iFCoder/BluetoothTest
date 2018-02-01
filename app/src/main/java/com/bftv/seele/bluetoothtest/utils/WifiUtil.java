package com.bftv.seele.bluetoothtest.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

/**
 * @author LiFei
 * @time 2018/1/17
 * @description:
 */
public class WifiUtil {
    /**
     * 获取当前连接的WIFI信息
     *
     * @param mContext
     * @return
     */
    public static WifiInfo getConnectWifiInfo(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo;
    }

    /**
     * 获取当前连接的WIFI信息
     *
     * @param wifiManager
     * @return
     */
    public static WifiInfo getConnectWifiInfo(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo;
    }

    /**
     * 获取当前连接的WIFI信息的BSSID
     * @param mContext
     * @return
     */
    public static String getConnectWifiBssid(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    /**
     * 获取当前连接的WIFI信息的SSID
     * @param wifiManager
     * @return
     */
    public static String getConnectWifiBssid(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    /**
     * Wifi是否打开
     * @param wifiManager
     * @return
     */
    public static Boolean isWifiEnable(WifiManager wifiManager) {
        if (wifiManager.isWifiEnabled()) {
            return true;
        }
        return false;
    }
}
