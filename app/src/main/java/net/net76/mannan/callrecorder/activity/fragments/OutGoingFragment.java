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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.adapter.AudioCallsAdapter;
import net.net76.mannan.callrecorder.constants.CallStatus;
import net.net76.mannan.callrecorder.model.CallAllDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static net.net76.mannan.callrecorder.service.MyVoiceRecordingService.audioFileDirectoryPath;

public class OutGoingFragment extends Fragment {
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    boolean adshow=false;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public OutGoingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OutGoingFragment newInstance(int sectionNumber) {
        OutGoingFragment fragment = new OutGoingFragment();
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

        recordingHeadertv.setVisibility(View.GONE);

        new MyAsyncTask(getContext()).execute();
        mAdView = (AdView) rootView.findViewById(R.id.adView);

        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //      Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //       Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        if(adshow) {
            mAdView.loadAd(adRequest2);
            //  Toast.makeText(getApplicationContext(), "Add shown", Toast.LENGTH_SHORT).show();
        }

        mInterstitialAd = new InterstitialAd(getContext());

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (adshow){
                    showInterstitial();}
            }
        });
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
            recordingHeadertv.setVisibility(View.GONE);
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
                recordingHeadertv.setVisibility(View.VISIBLE);
                recordingHeadertv.setText("No recordings found.");
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

            Arrays.sort(myFiles, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return (object1.getName().compareTo( object2.getName()));
                }
            });

            for (int i = 0; i < myFiles.length; i++) {
//                if (myFiles[i].getName().endsWith(".mp3")) {
                if (myFiles[i].getName().contains(CallStatus.OUTGOING)
                        || myFiles[i].getName().contains(CallStatus.outgoing)) {
                    filespath.add(myFiles[i].getPath());
                }
            }
        } else
            Log.d("audioFile", "No files found");

        return filespath;
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
