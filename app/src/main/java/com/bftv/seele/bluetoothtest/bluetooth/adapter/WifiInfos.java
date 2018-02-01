package com.bftv.seele.bluetoothtest.bluetooth.adapter;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public class WifiInfos {
    private String name;
    private String address;
    private int bondState;
    private int deviceType;
    /**
     * 已配对，是否连接
     */
    private boolean isConnect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    @Override
    public String toString() {
        return "WifiInfos{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", bondState=" + bondState +
                ", deviceType=" + deviceType +
                ", isConnect=" + isConnect +
                '}';
    }
}
