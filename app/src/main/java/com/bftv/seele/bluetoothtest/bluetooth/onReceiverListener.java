package com.bftv.seele.bluetoothtest.bluetooth;

/**
 * @author LiFei
 * @time 2018/1/16
 * @description:
 */
public interface onReceiverListener {
    /**
     * 配对成功
     */
    void onSuccess();

    /**
     * 取消配对
     */
    void onDelete();

    /**
     * 正在配对
     */
    void onBonding();
}
