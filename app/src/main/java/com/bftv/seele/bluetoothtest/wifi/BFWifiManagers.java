package com.bftv.seele.bluetoothtest.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.bftv.seele.bluetoothtest.bean.POWifiMsg;
import com.bftv.seele.bluetoothtest.utils.ToastUtil;
import com.bftv.seele.bluetoothtest.utils.WifiUtil;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public class BFWifiManagers {
    private Context mContext;
    private WifiManager wifiManager;
    private WifiStateReceiver_ mStateReceiver;
    private POWifiMsg infoWifi;
    private onClientWifiListener mOnClientWifiListener;
    private onServerWifiListener mOnServerWifiListener;
    private boolean isClient;

    public void setOnWifiListener(onClientWifiListener onWifiListener) {
        mOnClientWifiListener = onWifiListener;
    }

    public void setOnServerWifiListener(onServerWifiListener onServerWifiListener) {
        mOnServerWifiListener = onServerWifiListener;
    }

    public BFWifiManagers(Context context, WifiManager wifiManager, POWifiMsg infoWifi, boolean isClient) {
        mContext = context;
        this.wifiManager = wifiManager;
        this.infoWifi = infoWifi;
        this.isClient = isClient;
    }

    public void connectWifi() {
        if (WifiUtil.getConnectWifiBssid(mContext).equals(infoWifi.getBSSID())) {
            wifiManager.disconnect();
        }

        mStateReceiver = new WifiStateReceiver_();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //暂未处理
        filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.wifi.WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mStateReceiver, filter);

        WifiConfiguration wifiConfig = createWifiInfo(infoWifi.getSSID(), infoWifi.getPwd(), infoWifi.getWifiType());
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId != -1) {
            wifiManager.saveConfiguration();
        }
        wifiManager.enableNetwork(netId, true);
    }

    public class WifiStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    ToastUtil.showShort("Wifi断开，尝试连接");


                    if (isClient) {
                        mOnClientWifiListener.onClientDisConnect();
                    } else {
                        mOnServerWifiListener.onServerDisConnect();
                    }


                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    ToastUtil.showShort("成功连接" + infoWifi.getSSID());

                    if (isClient) {
                        mOnClientWifiListener.onClientConnected(wifiInfo, infoWifi);
                    } else {
                        mOnServerWifiListener.onServerConnected(wifiInfo, infoWifi);
                    }

                } else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
                    ToastUtil.showShort("正在连接...");

                    if (isClient) {
                        mOnClientWifiListener.onClientConnecting();
                    } else {
                        mOnServerWifiListener.onServerConnecting();
                    }

                }
            }
        }
    }


    public class WifiStateReceiver_ extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.DISCONNECTED) {
                        ToastUtil.showShort("Wifi断开，尝试连接");
                        if (isClient) {
                            mOnClientWifiListener.onClientDisConnect();
                        } else {
                            mOnServerWifiListener.onServerDisConnect();
                        }
                    } else if (state == NetworkInfo.State.CONNECTED) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        ToastUtil.showShort("成功连接" + wifiInfo.getSSID());
                        if (isClient) {
                            mOnClientWifiListener.onClientConnected(wifiInfo, infoWifi);
                        } else {
                            mOnServerWifiListener.onServerConnected(wifiInfo, infoWifi);
                        }
                    } else if (state == NetworkInfo.State.CONNECTING) {
                        ToastUtil.showShort("正在连接...");
                        if (isClient) {
                            mOnClientWifiListener.onClientConnecting();
                        } else {
                            mOnServerWifiListener.onServerConnecting();
                        }

                    }
                }
            }
        }
    }


    public void unregisterStateReceiver() {
        if (mStateReceiver != null) {
            mContext.unregisterReceiver(mStateReceiver);
        }
    }

    /**
     * 创建WIFI信息
     *
     * @param SSID
     * @param password
     * @param wifiType
     * @return
     */
    private WifiConfiguration createWifiInfo(String SSID, String password, int wifiType) {
        //清空config
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        //wifi名称
        config.SSID = "\"" + SSID + "\"";

        if (wifiType == 0) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (wifiType == 1) {
            config.hiddenSSID = false;
            //密码
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        if (wifiType == 2) {
            config.hiddenSSID = false;
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
            // For WPA
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            // For WPA2
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
}
