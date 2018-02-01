package com.bftv.seele.bluetoothtest.bluetooth;

import java.util.UUID;

/**
 * @author LiFei
 * @time 2018/1/15
 * @description:
 */
public class Constants {
     public static final int MESSAGE_STATE_CHANGE = 1;
     public static final int MESSAGE_READ = 2;
     public static final int MESSAGE_WRITE = 3;
     public static final int MESSAGE_DEVICE_NAME = 4;
     public static final int MESSAGE_TOAST = 5;

     public static final String DEVICE_NAME = "device_name";
     public static final String DEVICE_ADDRESS = "device_address";
     public static final String TOAST = "toast";

     public static final int SUCCESS = 200;
     public static final int CLIENT= 0;
     public static final int SERVER= 1;

     /**
      * IOS
      */
     public static UUID UUID_SERVER = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
     public static UUID UUID_CHARREAD = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
     public static UUID UUID_CHARWRITE = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
     public static UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
