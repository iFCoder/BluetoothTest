package com.bftv.seele.bluetoothtest.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author LiFei
 * @time 2018/1/19
 * @description:
 */
public class POWifiMsg implements Parcelable {

    private int wifiType;
    private String pwd;
    private String BSSID;
    private String SSID;

    public int getWifiType() {
        return wifiType;
    }

    public void setWifiType(int wifiType) {
        this.wifiType = wifiType;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    @Override
    public String toString() {
        return "POWifiMsg{" +
                "wifiType=" + wifiType +
                ", pwd='" + pwd + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", SSID='" + SSID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.wifiType);
        dest.writeString(this.pwd);
        dest.writeString(this.BSSID);
        dest.writeString(this.SSID);
    }

    public POWifiMsg() {
    }

    protected POWifiMsg(Parcel in) {
        this.wifiType = in.readInt();
        this.pwd = in.readString();
        this.BSSID = in.readString();
        this.SSID = in.readString();
    }

    public static final Parcelable.Creator<POWifiMsg> CREATOR = new Parcelable.Creator<POWifiMsg>() {
        @Override
        public POWifiMsg createFromParcel(Parcel source) {
            return new POWifiMsg(source);
        }

        @Override
        public POWifiMsg[] newArray(int size) {
            return new POWifiMsg[size];
        }
    };
}
