package com.bftv.seele.bluetoothtest.wifi;

import android.net.wifi.ScanResult;
import android.view.View;

/**
 * @author LiFei
 * @time 2018/1/16
 * @description:
 */
public interface onItemClickListener {
    void onItemClick(View view,int position,ScanResult result);
}
