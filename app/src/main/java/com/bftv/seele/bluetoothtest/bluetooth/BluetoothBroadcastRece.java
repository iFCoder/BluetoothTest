package com.bftv.seele.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bftv.seele.bluetoothtest.utils.ClsUtils;

/**
 * @author LiFei
 * @time 2018/1/16
 * @description:
 */
public class BluetoothBroadcastRece extends BroadcastReceiver{
    private onReceiverListener mOnReceiverListener;

    public BluetoothBroadcastRece(onReceiverListener onReceiverListener) {
        mOnReceiverListener = onReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            switch (state) {
                case BluetoothDevice.BOND_NONE:
                    mOnReceiverListener.onDelete();
                    try {
                        ClsUtils.createBond(device.getClass(), device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case BluetoothDevice.BOND_BONDING:
                    mOnReceiverListener.onBonding();
                    break;
                case BluetoothDevice.BOND_BONDED:
                    mOnReceiverListener.onSuccess();
                    break;
                default:
                    break;
            }
        }
    }
}
