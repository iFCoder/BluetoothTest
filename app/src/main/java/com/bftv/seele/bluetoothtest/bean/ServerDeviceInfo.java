package com.bftv.seele.bluetoothtest.bean;

/**
 * @author LiFei
 * @time 2018/1/26
 * @description:  服务端设备信息
 */
public class ServerDeviceInfo {
    private String name;
    private Boolean isAndroid;
    private Boolean isIos;
    private Boolean isWifi;
    private String wifiName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAndroid() {
        return isAndroid;
    }

    public void setAndroid(Boolean android) {
        isAndroid = android;
    }

    public Boolean getIos() {
        return isIos;
    }

    public void setIos(Boolean ios) {
        isIos = ios;
    }

    public Boolean getWifi() {
        return isWifi;
    }

    public void setWifi(Boolean wifi) {
        isWifi = wifi;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                ", \"isAndroid\":" + isAndroid +
                ", \"isIos\":" + isIos +
                ", \"isWifi\":" + isWifi +
                ", \"wifiName\":" + wifiName +
                '}';
    }
}
