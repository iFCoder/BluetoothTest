package com.bftv.seele.bluetoothtest.bluetooth.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bftv.seele.bluetoothtest.R;
import com.bftv.seele.bluetoothtest.bluetooth.BluetoothType;

import java.util.List;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public class WifiInfoAdapter extends BaseAdapter {
    private List<WifiInfos> mInfosList;
    private LayoutInflater inflater;

    public WifiInfoAdapter(Context context, List<WifiInfos> infosList) {
        mInfosList = infosList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mInfosList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View infoView = inflater.inflate(R.layout.item_bluetooth, null);
        WifiInfos wifiInfos = mInfosList.get(i);
        TextView textName = infoView.findViewById(R.id.item_name_info);
        TextView textAddress = infoView.findViewById(R.id.item_address_info);
        ImageView viewType = infoView.findViewById(R.id.btooth_type);
        View line = infoView.findViewById(R.id.view_line);
        if(i== mInfosList.size() -1){
            line.setVisibility(View.GONE);
        }else{
            line.setVisibility(View.VISIBLE);
        }
        if(wifiInfos.isConnect()){
            textName.setText("已连接 "+wifiInfos.getName());
        }else{
            textName.setText("已配对 "+wifiInfos.getName());
        }
        textAddress.setText(wifiInfos.getAddress());
        //智能手机
        if(wifiInfos.getDeviceType() == BluetoothType.PHONE_SMART){
            if(wifiInfos.isConnect()){
                viewType.setImageResource(R.mipmap.icon_mobile_select);
            }else{
                viewType.setImageResource(R.mipmap.icon_mobile_unselect);
            }
        }else if(wifiInfos.getDeviceType() == BluetoothType.COMPUTER_LAPTOP){
            viewType.setImageResource(R.mipmap.icon_computer);
        }
        return infoView;
    }
}
