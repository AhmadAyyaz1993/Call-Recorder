package net.net76.mannan.callrecorder.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.net76.mannan.callrecorder.constants.CallStatus;
import net.net76.mannan.callrecorder.constants.Constants;
import net.net76.mannan.callrecorder.constants.MyLogTags;
import net.net76.mannan.callrecorder.service.MyVoiceRecordingService;
import net.net76.mannan.callrecorder.util.PrefManager;

import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class CallReceiver extends BroadcastReceiver {

    int CallType;
    int CallTypeIncoming = 0;
    int CallTypeOutgoing = 1;
    String dir = null;
    PrefManager prefManager;

    //this function is called when a call is dialed or receieved or missed etc.
    public void onReceive(final Context context, final Intent intent) {

        prefManager = new PrefManager(context);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                ContentResolver contentResolver = context.getContentResolver();
                Handler handler = new Handler();
                String origanalname = "";
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String contactName = "";

                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                  /*  CallType = CallTypeIncoming;
                    Log.d(MyLogTags.MPR, "Its Ringing [" + number + "]");
                    if (prefManager.getCallRecord() == Constants.RECORD_ALL
                            || prefManager.getCallRecord() == Constants.RECORD_INCOMING) {
                        startVoiceRecordingService(context, number, contactName, CallStatus.INCOMING);
                    }*/
                }
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state) && number != null) {
                    CallType = CallTypeIncoming;
                    Log.d(MyLogTags.MPR, "Its Ringing yaha [" + number + "]");
                    if (prefManager.getCallRecord() == Constants.RECORD_ALL
                            || prefManager.getCallRecord() == Constants.RECORD_INCOMING) {
                        startVoiceRecordingService(context, number, contactName, CallStatus.INCOMING);
                    }
                }
                if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                    Log.d(MyLogTags.MPR, "Its Idle");

                    stopAppServices(context);

                    StringBuffer sb = new StringBuffer();

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                            != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Cursor managedCursor = context.getContentResolver()
                            .query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
                    int numbers = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                    int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                    int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
                    sb.append("Call Details :");

                    while (managedCursor.moveToNext()) {
                        managedCursor.moveToFirst();
                        String phNumber = managedCursor.getString(numbers);
                        String namee = managedCursor.getString(name);
                        String callType = managedCursor.getString(type);
                        String callDate = managedCursor.getString(date);
                        Date callDayTime = new Date(Long.valueOf(callDate));
                        String callDuration = managedCursor.getString(duration);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                        contactName = namee;
//                        phNumber = phNumber.replaceAll("[^0-9]","");

                        try {
                            if (phNumber.startsWith("+92")) {
                                phNumber = "0" + phNumber.substring(3, phNumber.length());
                            } else if (phNumber.startsWith("+")) {
                                phNumber = "00" + phNumber.substring(1, phNumber.length());
                            } else {
                                phNumber = phNumber;
                            }
                        } catch (Exception e) {
                        }

                        int secondindex = date;
                        long seconds = managedCursor.getLong(secondindex);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = formatter.format(new Date(seconds));

                        int dircode = Integer.parseInt(callType);
                        switch (dircode) {
                            case CallLog.Calls.OUTGOING_TYPE:
                                dir = CallStatus.OUTGOING;
                                break;
                            case CallLog.Calls.INCOMING_TYPE:
                                if (duration == 0) {
                                    dir = CallStatus.Rejected;
                                } else
                                    dir = CallStatus.INCOMING;
                                break;
                            case CallLog.Calls.MISSED_TYPE:
                                dir = CallStatus.MISSED;
                                break;
                        }
                        sb.append("\nPhone Number:--- " + phNumber + "\nName:---" + namee
                                + "\nTime:---" + dateString + "\nCall Type:--- " + dir
                                + " \nCall Date:--- " + callDayTime
                                + " \nCall duration in sec :--- " + callDuration);
                        sb.append("\n----------------------------------");

                        String datee = dateString.substring(0, 10);

                        if (dir == null) {
                            if (CallType == CallTypeIncoming) {
                                dir = CallStatus.INCOMING;
                            } else if (CallType == CallTypeOutgoing) {
                                dir = CallStatus.OUTGOING;
                            } else {
                                dir = "Not Defined";
                            }
                        }
                        if (sharedPreferences.getLong("call_id", -1) != managedCursor.getLong(id)
                                || !sharedPreferences.getString("call_number", "nothing").equals(phNumber)
                                || !sharedPreferences.getString("call_name", "nothing").equals(namee)
                                || !sharedPreferences.getString("call_type", "nothing").equals(dir)
                                || !sharedPreferences.getString("call_date", "nothing").equals(datee)
                                || !sharedPreferences.getString("call_duration", "nothing").equals(callDuration)) {

                            if (dir.equals(CallStatus.INCOMING)) {
                                if (!callDuration.equals("0")) {
                                    dir = CallStatus.receivedCalls;
                                } else if (callDuration.equals("0")) {
                                    dir = CallStatus.Rejected;
                                }
                            }
                            if (dir.equals(CallStatus.OUTGOING)) {

                                dir = CallStatus.dialedCalls;

                                if (callDuration.equals("0")) {
                                    dir = CallStatus.notAttended;
                                }
                            }
                        }
                        break;
                    }
                    managedCursor.close();
                }
                String mobile = "";
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                    CallType = CallTypeOutgoing;
                    Log.d(MyLogTags.MPR, "Its OffHook");
                    if (number != null) {
                        try {
                            origanalname = getContactDisplayNameByNumber(number, context);
                            System.out.println("Recever name is " + getContactDisplayNameByNumber(number, context));
                            ;
                            if (number.startsWith("+92")) {
                                number = "0" + number.substring(3, number.length());
                            } else if (number.startsWith("+")) {
                                number = "00" + number.substring(1, number.length());
                            }
                        } catch (Exception e) {
                        }
                        Log.d(MyLogTags.recording, "mobile number: " + number);
                        if (prefManager.getCallRecord() == Constants.RECORD_ALL
                                || prefManager.getCallRecord() == Constants.RECORD_OUTGOING) {
                            startVoiceRecordingService(context, number, contactName, CallStatus.OUTGOING);

                        }
                    }
                }
                if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
                    CallType = CallTypeOutgoing;
                    Log.d(MyLogTags.MPR, "Its Outgoing");
                    final String originalNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                    mobile = originalNumber;
                    try {
                        if (originalNumber.startsWith("+92")) {
                            mobile = "0" + originalNumber.substring(3, originalNumber.length());
                        } else if (originalNumber.startsWith("+")) {
                            mobile = "00" + originalNumber.substring(1, originalNumber.length());
                        } else {
                            mobile = originalNumber;
                        }
                    } catch (Exception e) {
                    }
                    if (prefManager.getCallRecord() == Constants.RECORD_ALL
                            || prefManager.getCallRecord() == Constants.RECORD_OUTGOING) {
                        startVoiceRecordingService(context, mobile, contactName, CallStatus.OUTGOING);
                    }
                }
            }
        }, 500);
    }

    private void startVoiceRecordingService(Context context, String mobile, String name, String callType) {
        try {
            if (MyVoiceRecordingService.wasRinging == false) {
                MyVoiceRecordingService.wasRinging = true;
//                                if (MyVoiceRecordingService.wasRinging == true) {
                Intent voice_record_intent = new Intent(context, MyVoiceRecordingService.class);
                voice_record_intent.putExtra("name", name);
                voice_record_intent.putExtra("phNumber", mobile);
                voice_record_intent.putExtra("type", callType);
                context.startService(voice_record_intent);
//                Toast.makeText(context, "Recording Started.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d(MyLogTags.recording, "Recording Error: " + e.getMessage());
        }
    }

    private void stopAppServices(Context context) {
        try {
            if (MyVoiceRecordingService.wasRinging = true) {
                MyVoiceRecordingService.wasRinging = false;
                if (MyVoiceRecordingService.recordStarted) {
                    MyVoiceRecordingService.recorder.stop();
                    MyVoiceRecordingService.recorder.reset();
                    MyVoiceRecordingService.recorder.release();
                    MyVoiceRecordingService.recorder = null;
                    MyVoiceRecordingService.recordStarted = false;
                }
                context.stopService(new Intent(context, MyVoiceRecordingService.class));
//                Toast.makeText(context, "Recording Stopped.", Toast.LENGTH_SHORT).show();
                Log.d(MyLogTags.recording, "Recording Stopped");

//                MyVoiceRecordingService service = new MyVoiceRecordingService();
//                if (!dir.equals(CallStatus.receivedCalls) && !dir.equals(CallStatus.dialedCalls)) {
//                    service.deleteVoiceFile(MyVoiceRecordingService.file_name);
//                }
            }
        } catch (Exception e) {
            Log.d(MyLogTags.recording, "Recording Stop Failed: " + e);
        }
    }

    private void uploadContactPhoto(Context context, String number) {

        Log.v("ffnet", "Started uploadcontactphoto...");

        String name = null;
        String contactId = null;
        InputStream input = null;

// define the columns I want the query to return
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

// encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

// query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if (cursor.moveToFirst()) {

            // Get values from contacts database:
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

            // Get photo of contactId as input stream:
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
            input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);

            Log.v("ffnet", "Started uploadcontactphoto: Contact Found @ " + number);
            Log.v("ffnet", "Started uploadcontactphoto: Contact name  = " + name);
            Log.v("ffnet", "Started uploadcontactphoto: Contact id    = " + contactId);

        } else {

            Log.v("ffnet", "Started uploadcontactphoto: Contact Not Found @ " + number);
            return; // contact not found

        }

// Only continue if we found a valid contact photo:
        if (input == null) {
            Log.v("ffnet", "Started uploadcontactphoto: No photo found, id = " + contactId + " name = " + name);
            return; // no photo
        } else {
            // this.type = contactId;
            Log.v("ffnet", "Started uploadcontactphoto: Photo found, id = " + contactId + " name = " + name);
        }

    }

    public String getContactDisplayNameByNumber(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "Incoming call from";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                // this.id =
                // contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                // String contactId =
                // contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            } else {
                name = "Unknown number";
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }


}
