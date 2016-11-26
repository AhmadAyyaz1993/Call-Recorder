package net.net76.mannan.callrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import net.net76.mannan.callrecorder.util.MyDateTimeStamp;
import net.net76.mannan.callrecorder.constants.MyLogTags;

import java.io.File;
import java.io.IOException;

/**
 * Created by MANNAN on 6/21/2016.
 */
public class MyVoiceRecordingService extends Service{

    public static boolean recordStarted = false;
    public static boolean wasRinging = false;
    public static MediaRecorder recorder = null;
    File audioFile;
    String type, phNumber, agent_num = "1";
    public static String file_name;
    public static String audioFileDirectoryPath = "/CallRecorderFiles";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Bundle extras = intent.getExtras();
            if(extras == null) {
                phNumber= "1";
                agent_num= "1";
                type= "call";
            } else {
                phNumber = extras.getString("phNumber");
                agent_num = extras.getString("agent_num");
                type = extras.getString("type");
            }
            recordVoiceCall(phNumber, type);
//            recordVoiceCallAudioRecorder(phNumber, type);
        }catch (Exception e){
            Log.d(MyLogTags.recording, "Recording bundle Exception: "+e.getMessage());
        }

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
                    .setBufferSizeInBytes(2*8000)
                    .build();
        }
        audioRecord.startRecording();
    }

    public void recordVoiceCall(String phNumber, String type){

        File LastingSalesRecordingsDir = new File(Environment.getExternalStorageDirectory(),
                audioFileDirectoryPath);
        if (!LastingSalesRecordingsDir.exists()) {
            LastingSalesRecordingsDir.mkdirs();
        }

        file_name = "call_"+ MyDateTimeStamp.getCurrentDate()+"_"+
                MyDateTimeStamp.getCurrentTimeForFile()+"_"+type+"_"+phNumber+"_";
        try {
            audioFile = File.createTempFile(file_name, ".mp3", LastingSalesRecordingsDir);
        }
        catch (IOException e) {
            Log.d(MyLogTags.recording, "Recording IOException: "+e.getMessage());
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
        }catch (Exception e){
            Log.d(MyLogTags.recording, "Recording Exception: "+e);
        }
        try {
            recorder.prepare();
            Log.d(MyLogTags.recording, "Recording Recorder prepared.");
        } catch (IllegalStateException e) {
            deleteVoiceFile(file_name);
            Log.d(MyLogTags.recording, "Recording prepare IllegalStateException: "+e);
        } catch (IOException e) {
            deleteVoiceFile(file_name);
            Log.d(MyLogTags.recording, "Recording prepare IOException: "+e);
        }

        try {
            String state = Environment.getExternalStorageState();
            if(!state.equals(Environment.MEDIA_MOUNTED))  {
                Log.d(MyLogTags.recording,"SD Card is not mounted.  It is " + state + ".");
                throw new IOException("SD Card is not mounted.  It is " + state + ".");
            }

            recorder.start();
            Log.d(MyLogTags.recording, "Recording Recorder started for: "+phNumber);
            recordStarted = true;
        }catch (Throwable e) {
            deleteVoiceFile(file_name);
            Log.d(MyLogTags.recording, "Recording start Exception: "+e);
        }
    }

    public void deleteVoiceFile(String fileName){
        File[] myFiles = new File[0];

        File pathToRecordings = new File(
                Environment.getExternalStorageDirectory() + audioFileDirectoryPath);
        if(pathToRecordings.exists())
        {
            myFiles = pathToRecordings.listFiles();
        }
        else
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
        }else
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

}
