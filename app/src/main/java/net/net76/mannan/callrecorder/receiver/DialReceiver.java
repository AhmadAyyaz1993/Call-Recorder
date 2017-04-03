package net.net76.mannan.callrecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.net76.mannan.callrecorder.activity.SplashActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Mannan on 03/04/2017.
 */

public class DialReceiver extends BroadcastReceiver {
    // SharedPreferences sp;

    String phoneno;
    protected SQLiteDatabase db;
    protected
    Cursor cursor;
    String password = "1111";
    //  LoginScreen ph = new LoginScreen();
    boolean toglevalue;
    String randumvalue;

    @Override
    public void onReceive(Context context, final Intent intent) {
        SharedPreferences settings2 = context.getSharedPreferences("login", MODE_PRIVATE);
        toglevalue = settings2.getBoolean("tglvalh", Boolean.parseBoolean(null));
        // randumvalue = settings2.getString("randumval", "");
        SharedPreferences settings4 = context.getSharedPreferences("login", MODE_PRIVATE);
        randumvalue = settings4.getString("randumval", null);
        //  Toast.makeText(context, "Value is  "+toglevalue, Toast.LENGTH_SHORT).show();

        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            phoneno = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        }
        if (phoneno.equals(password) || phoneno.equals(randumvalue)) {

            //    sp = context.getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences settings23 = context.getSharedPreferences("login", context.MODE_PRIVATE);

            SharedPreferences.Editor e2 = settings23.edit();
            e2.putBoolean("tglvalh", false);
            e2.commit();

            SharedPreferences settings22 = context.getSharedPreferences("login", context.MODE_PRIVATE);
            toglevalue = settings22.getBoolean("tglvalh", Boolean.parseBoolean(null));
            //  Toast.makeText(context, "New Value is  "+toglevalue, Toast.LENGTH_SHORT).show();


            ComponentName componentName = new ComponentName(context,

                    SplashActivity.class);
            context.getPackageManager().setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);


            Intent intent1 = new Intent(context,
                    SplashActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent1.putExtra("some_data", toglevalue);
            context.startActivity(intent1);
            context.stopService(intent);

        }

    }
}