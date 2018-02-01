package com.bftv.seele.bluetoothtest.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bftv.seele.bluetoothtest.utils.ToastUtil;

/**
 * @author LiFei
 * @time 2018/1/23
 * @description:
 */
public class BaseHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BluetoothChatService.STATE_CONNECTED:
                        ToastUtil.showShort("与该设备已建立连接");
                        break;
                    case BluetoothChatService.STATE_CONNECTING:
                        ToastUtil.showShort("正在建立连接....");
                        break;
                    case BluetoothChatService.STATE_LISTEN:
                      //  ToastUtil.showShort("正在监听....");
                    case BluetoothChatService.STATE_NONE:
                      //  ToastUtil.showShort("关闭");
                        break;
                    default:
                        break;
                }
                break;
            case Constants.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                String writeMessage = new String(writeBuf);
                break;
            case Constants.MESSAGE_READ:
                // byte[] readBuf = (byte[]) msg.obj;
                // String readMessage = new String(readBuf, 0, msg.arg1);
                // showPwd.setText("信息：" + readMessage);
                // WIFI连接
                // connectWifi(readMessage);
                break;
            case Constants.MESSAGE_DEVICE_NAME:
                Log.e("TAG" ,"已成功连接到" + msg.getData().getString(Constants.DEVICE_NAME) + "，可通讯");
                break;
            case Constants.MESSAGE_TOAST:
                ToastUtil.showShort(msg.getData().getString(Constants.TOAST));
                break;
            default:
                break;
        }
    }
}
