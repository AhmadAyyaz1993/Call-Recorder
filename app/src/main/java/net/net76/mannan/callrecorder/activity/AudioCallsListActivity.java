package net.net76.mannan.callrecorder.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.adapter.AudioCallsAdapter;
import net.net76.mannan.callrecorder.model.CallAllDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static net.net76.mannan.callrecorder.service.MyVoiceRecordingService.audioFileDirectoryPath;

/**
 * Created by Shahid on 8/5/2016.
 */
public class AudioCallsListActivity extends AppCompatActivity {

    TextView recordingHeadertv;
    ListView recordingsListView;
    Context context;
    private ArrayList<CallAllDetails> audioFileList = new ArrayList<CallAllDetails>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_calls);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initaliseViews();

        ArrayList<String> filespath = new ArrayList<String>();

        String fileName;
        filespath = addFileToList();
        for (int i = 0; i < filespath.size(); i++) {
            fileName = new File(filespath.get(i)).getName();
            audioFileList.add(new CallAllDetails(filespath.get(i), fileName));
        }
        Collections.reverse(audioFileList);
        context = this;
        if (audioFileList.size() == 0) {
            recordingHeadertv.setText("No recording found.");
        } else {
            recordingsListView.setAdapter(new AudioCallsAdapter(this, audioFileList));
        }
    }

    private void initaliseViews() {
        recordingHeadertv = (TextView) findViewById(R.id.recordingHeadertv);
        recordingsListView = (ListView) findViewById(R.id.recordingsListView);
    }

    private ArrayList<String> addFileToList() {
        ArrayList<String> filespath = new ArrayList<String>();
//        File pathToRecordings = new File(Environment.getExternalStorageDirectory()+"/Download");//gennymotion
//        File pathToRecordings = new File(Environment.getExternalStorageDirectory()+ "/Music");//Mobiles link
        File pathToRecordings = new File(Environment.getExternalStorageDirectory() + audioFileDirectoryPath);
//        File pathToRecordings = new File(Environment.getExternalStorageDirectory()+"/zedge/ringtone");
//        File pathToRecordings = new File(Environment.getExternalStorageDirectory()+ "/Music");//Nasir Mobiles link
        if (pathToRecordings.exists()) {
            File myFiles[] = pathToRecordings.listFiles();
            for (int i = 0; i < myFiles.length; i++) {
//                if (myFiles[i].getName().endsWith(".mp3"))
//                {
                filespath.add(myFiles[i].getPath());
//                }
            }
        } else
            Log.d("audioFile", "No files found");

        return filespath;
    }

}