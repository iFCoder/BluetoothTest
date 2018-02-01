package com.bftv.seele.bluetoothtest.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bftv.seele.bluetoothtest.R;

import java.util.List;

/**
 * @author LiFei
 * @time 2018/1/16
 * @description:
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder> {
    private Context mContext;
    private List<ScanResult> list;
    private onItemClickListener mItemClickListener;

    public void onItemClickListeners(onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public WifiAdapter(Context context, List<ScanResult> list) {
        mContext = context;
        this.list = list;
    }

    @Override
    public WifiAdapter.WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, parent, false);
        return new WifiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WifiAdapter.WifiViewHolder holder, int position) {
        holder.wifiName.setText(list.get(position).SSID);
        /*if (WifiUtil.getConnectWifiBssid(mContext).equals(list.get(position).BSSID)) {
            holder.tvState.setText("已连接");
            holder.tvState.setTextColor(Color.RED);
        } else {
            holder.tvState.setText("未连接");
            holder.tvState.setTextColor(Color.GRAY);
        }*/
        if((list.get(position).capabilities).contains("WPA")||(list.get(position).capabilities).contains("PSK")){
            holder.wifiPwd.setImageResource(R.mipmap.icon_wifi_pwd);
        }else{
            holder.wifiPwd.setImageResource(R.mipmap.icon_wifi_nopwd);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class WifiViewHolder extends RecyclerView.ViewHolder {
        TextView wifiName;
        TextView tvState;
        ImageView wifiPwd;

        public WifiViewHolder(View itemView) {
            super(itemView);
            wifiName = itemView.findViewById(R.id.wifi_name);
            tvState = itemView.findViewById(R.id.wifi_state);
            wifiPwd = itemView.findViewById(R.id.wifi_pwd);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, getLayoutPosition(), list.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

}
