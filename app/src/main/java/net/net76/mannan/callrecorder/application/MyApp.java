package net.net76.mannan.callrecorder.application;

import android.app.Application;
import android.util.Log;

import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;

import net.net76.mannan.callrecorder.util.MyDevice;

/**
 * Created by Mannan on 11/27/2016.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mint.initAndStartSession(getApplicationContext(), "747ef5f1");

        try {
            Mint.logEvent(MyDevice.getDeviceName()+" : "
                    +MyDevice.getDeviceOsVersion(), MintLogLevel.Info);
        }
        catch (Exception e){
            Log.d("MintSplunk","Exception: "+e.getMessage());
        }

    }

}
