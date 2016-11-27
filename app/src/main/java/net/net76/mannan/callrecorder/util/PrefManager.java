package net.net76.mannan.callrecorder.util;

import android.content.Context;
import android.content.SharedPreferences;

import net.net76.mannan.callrecorder.constants.Constants;

/**
 * Created by Mannan on 11/24/2016.
 */

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "callRecorderPref";
    private static final String CALL_RECORD = "callRecord";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setCallRecord(int callRecord) {
        editor.putInt(CALL_RECORD, callRecord);
        editor.commit();
    }

    public int getCallRecord() {
        return pref.getInt(CALL_RECORD, Constants.RECORD_ALL);
    }
}