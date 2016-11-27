package net.net76.mannan.callrecorder.activity.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.adapter.AudioCallsAdapter;
import net.net76.mannan.callrecorder.constants.CallStatus;
import net.net76.mannan.callrecorder.model.CallAllDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static net.net76.mannan.callrecorder.service.MyVoiceRecordingService.audioFileDirectoryPath;

public class IncomingFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public IncomingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IncomingFragment newInstance(int sectionNumber) {
        IncomingFragment fragment = new IncomingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    ProgressBar progressBar;
    TextView recordingHeadertv;
    ListView recordingsListView;
    Context context;
    private ArrayList<CallAllDetails> audioFilePathList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_audio_calls, container, false);

        initInstance(rootView);

        new MyAsyncTask(getContext()).execute();

        return rootView;
    }

    private void initInstance(View rootView) {
        audioFilePathList = new ArrayList<CallAllDetails>();
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        recordingHeadertv = (TextView) rootView.findViewById(R.id.recordingHeadertv);
        recordingsListView = (ListView) rootView.findViewById(R.id.recordingsListView);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        Context context;

        MyAsyncTask(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
//            category_progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                prepareList();
            } catch (Exception e) {
                Toast.makeText(context, "AsyncTaskException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (audioFilePathList.size() == 0) {
                recordingHeadertv.setText("No recording found.");
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recordingsListView.setAdapter(new AudioCallsAdapter(context, audioFilePathList));
            }
        }
    }

    private void prepareList() {
        String fileName;
        ArrayList<String> filespath = new ArrayList<String>();
        filespath = addFileToList();
        for (int i = 0; i < filespath.size(); i++) {
            fileName = new File(filespath.get(i)).getName();
            audioFilePathList.add(new CallAllDetails(filespath.get(i), fileName));
        }
        Collections.reverse(audioFilePathList);

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
//                if (myFiles[i].getName().endsWith(".mp3")) {
                if (myFiles[i].getName().contains(CallStatus.INCOMING)
                        || myFiles[i].getName().contains(CallStatus.incoming)) {
                    filespath.add(myFiles[i].getPath());
                }
            }
        } else
            Log.d("audioFile", "No files found");

        return filespath;
    }

}
