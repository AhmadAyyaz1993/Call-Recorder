package net.net76.mannan.callrecorder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import net.net76.mannan.callrecorder.constants.MyLogTags;
import net.net76.mannan.callrecorder.util.MyDateTimeStamp;

import java.io.File;
import java.io.IOException;

/**
 * Created by MANNAN on 6/21/2016.
 */
public class MyVoiceRecordingService extends Service {

    public static boolean recordStarted = false;
    public static boolean wasRinging = false;
    public static MediaRecorder recorder = null;
    File audioFile;
    String type, phNumber, name, agent_num = "1";
    public static String file_name;
    public static String audioFileDirectoryPath = "/CallRecorderFiles";
    public static String incomingPath = "/Incoming";
    public static String outgoingPath = "/Outgoing";
    Handler handler = new Handler();
    Runnable myRunnable;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                name = "name";
                phNumber = "1";
                agent_num = "1";
                type = "call";
            } else {
                phNumber = extras.getString("phNumber");
                name = extras.getString("name");
                agent_num = extras.getString("agent_num");
                type = extras.getString("type");
            }
            recordVoiceCall(phNumber, name, type);
//            recordVoiceCallAudioRecorder(phNumber, type);
        } catch (Exception e) {
            Log.d(MyLogTags.recording, "Recording bundle Exception: " + e.getMessage());
        }
        if (phNumber.contains("*")) {
            deleteVoiceFile(file_name);


        }

        if (phNumber.contains("#")) {
            deleteVoiceFile(file_name);


        }

        if (phNumber.contains("File Name")) {
            deleteVoiceFile(file_name);


        }
        myRunnable = new Runnable() {
            public void run() {
                stopAppServices(getApplicationContext());
            }
        };
        handler.postDelayed(myRunnable,300000);

/*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAppServices(getApplicationContext());

            }
        }, 60000);*/
        return START_NOT_STICKY;
//        return START_STICKY;

    }

    private void recordVoiceCallAudioRecorder(String phNumber, String type) {
        AudioRecord audioRecord = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            audioRecord = new AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(32000)
                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                            .build())
                    .setBufferSizeInBytes(2 * 8000)
                    .build();
        }
        audioRecord.startRecording();
    }

    public void recordVoiceCall(String phNumber, String name, String type) {

        File LastingSalesRecordingsDir = new File(Environment.getExternalStorageDirectory(),
                audioFileDirectoryPath);

/*
        if (type.equals(CallStatus.INCOMING)) {
            LastingSalesRecordingsDir = new File(Environment.getExternalStorageDirectory(),
                    audioFileDirectoryPath + incomingPath);
        } else if (type.equals(CallStatus.OUTGOING)) {
            LastingSalesRecordingsDir = new File(Environment.getExternalStorageDirectory(),
                    audioFileDirectoryPath + outgoingPath);
        }
*/

        if (!LastingSalesRecordingsDir.exists()) {
            LastingSalesRecordingsDir.mkdirs();
        }

        file_name = "call_" + MyDateTimeStamp.getCurrentDate() + "_" +
                MyDateTimeStamp.getCurrentTimeForFile() + "_" + type + "_" + phNumber + "_";
        try {
            audioFile = File.createTempFile(file_name, ".mp3", LastingSalesRecordingsDir);
        } catch (IOException e) {
            Log.d(MyLogTags.recording, "Recording IOException: " + e.getMessage());
            e.printStackTrace();
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        try {
            file_name = audioFile.getName();

            if (recorder == null) {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//            recorder.setAudioSamplingRate(8000);//8000 44100, 22050 and 11025
//            recorder.setAudioEncodingBitRate(8000);
//            recorder.setMaxDuration(60000*5);
//            recorder.setMaxFileSize(5000000);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audioFile.getAbsolutePath());

                Log.d(MyLogTags.recording, "Recording output formats set.");
            }
        } catch (Exception e) {
            Log.d(MyLogTags.recording, "Recording Exception: " + e);
        }
        try {
            recorder.prepare();
            Log.d(MyLogTags.recording, "Recording Recorder prepared.");
        } catch (IllegalStateException e) {
            deleteVoiceFile(file_name);
            Log.d(MyLogTags.recording, "Recording prepare IllegalStateException: " + e);
        } catch (IOException e) {
            deleteVoiceFile(file_name);
            Log.d(MyLogTags.recording, "Recording prepare IOException: " + e);
        }

        try {
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                Log.d(MyLogTags.recording, "SD Card is not mounted.  It is " + state + ".");
                throw new IOException("SD Card is not mounted.  It is " + state + ".");
            }

            recorder.start();
            Log.d(MyLogTags.recording, "Recording Recorder started for: " + phNumber);
            recordStarted = true;
        } catch (Throwable e) {
            deleteVoiceFile(file_name);
            Log.d(MyLogTags.recording, "Recording start Exception: " + e);
        }
    }

    public void deleteVoiceFile(String fileName) {
        File[] myFiles = new File[0];

        File pathToRecordings = new File(
                Environment.getExternalStorageDirectory() + audioFileDirectoryPath);
        if (pathToRecordings.exists()) {
            myFiles = pathToRecordings.listFiles();
        } else
            Log.d(MyLogTags.audioFile, "No files found");
        if (myFiles.length != 0) {
            for (File file : myFiles) {
//                if (file.getName().contains(phNumber)/* && file.getName().contains(MyDateTimeStamp.getCurrentDate())
//                        && file.getName().contains(type)*/) {
                String path = file.getAbsolutePath();
                if (file.getName().equals(fileName)) {
                    file.delete();
                }
                if (myFiles.length != 0) {
                    pathToRecordings.delete();
                }
//                }
            }
        } else
            pathToRecordings.delete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        DataSenderNew dataSenderNew = new DataSenderNew(getApplicationContext());
//        dataSenderNew.execute();
//        System.exit(0);

        if (recordStarted) {

            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            recordStarted = false;
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
                    handler.removeCallbacks(myRunnable);

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

}
