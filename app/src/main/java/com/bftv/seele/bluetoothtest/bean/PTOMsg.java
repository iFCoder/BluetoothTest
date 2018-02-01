package com.bftv.seele.bluetoothtest.bean;

/**
 * @author LiFei
 * @time 2018/1/29
 * @description:
 */
public class PTOMsg {
   private int type;
   private String data;
   private String msg;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
