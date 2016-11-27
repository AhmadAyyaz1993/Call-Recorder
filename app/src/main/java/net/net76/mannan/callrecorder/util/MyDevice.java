package net.net76.mannan.callrecorder.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by MANNAN on 8/9/2016.
 */
public class MyDevice {

    public static String getDeviceID(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();

        return deviceId;
    }

    public static String getDeviceName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = "" ;

        try {
            if (myDevice.getName() != null){
                deviceName = myDevice.getName();
            }
        }catch (Exception e){
            Log.d("MyDevice","getDeviceName Exception: "+e.getMessage());
        }

        return deviceName;
    }

    public static String getDeviceOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceOSName() {

        Field[] fields = Build.VERSION_CODES.class.getFields();
        String deviceOSName = fields[Build.VERSION.SDK_INT + 1].getName();

        return deviceOSName;
    }

}
