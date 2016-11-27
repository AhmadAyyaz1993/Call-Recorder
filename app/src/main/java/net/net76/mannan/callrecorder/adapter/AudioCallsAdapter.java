package net.net76.mannan.callrecorder.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.net76.mannan.callrecorder.constants.CallStatus;
import net.net76.mannan.callrecorder.model.CallAllDetails;
import net.net76.mannan.callrecorder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shahid on 7/16/2016.
 */
public class AudioCallsAdapter extends BaseAdapter {
    private Context context;
    private MediaPlayer mediaPlayer;
    private boolean firsttime = true;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler;
    private Handler myHandler1;
    private TextView dfilename;
    private TextView callstatus;
    private TextView totaltime;
    private TextView remaingtime;
    private boolean running = true;
    private SeekBar audioPlayDialogSeekBar;
    private boolean filecompleted = false;
    private boolean checkpoint = true;
    private Runnable UpdateSongTime;
    private Runnable UpdateSongTime1;
    private ArrayList<CallAllDetails> audioFileList = new ArrayList<CallAllDetails>();
    private static LayoutInflater inflater = null;

    public AudioCallsAdapter(Context context, ArrayList<CallAllDetails> filespathlist) {
        // TODO Auto-generated constructor stub
        this.audioFileList = filespathlist;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return audioFileList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        private LinearLayout playpauseView;
        private ImageButton playpausebtn;
        private TextView callfileName;
        private TextView totaltime;
        private TextView filetimecreated;
        private TextView filedatecreated;
        private ImageView callstatus1;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.audio_calls_list_row, null);
        holder.playpauseView = (LinearLayout) rowView.findViewById(R.id.play_pause_view);
        holder.playpausebtn = (ImageButton) rowView.findViewById(R.id.play_pause_btn);
        holder.callfileName = (TextView) rowView.findViewById(R.id.audiofileName);
        holder.totaltime = (TextView) rowView.findViewById(R.id.durationtiming);
        holder.callstatus1 = (ImageView) rowView.findViewById(R.id.callstatusid);
        holder.filetimecreated = (TextView) rowView.findViewById(R.id.myaudiofiletime);
        holder.filedatecreated = (TextView) rowView.findViewById(R.id.myaudiofiledate);

        if (audioFileList.size() > 0) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(audioFileList.get(position).getMyfilepath());
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millSecond = Integer.parseInt(durationStr);
            //setting listview totaltime
            holder.totaltime.setText(String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours((long) millSecond),
                    TimeUnit.MILLISECONDS.toMinutes((long) millSecond),
                    TimeUnit.MILLISECONDS.toSeconds((long) millSecond) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) millSecond))));

            String filename = audioFileList.get(position).filename;
            final String call_type = filename.substring(25, 33);
            int numlastpositionstr = filename.lastIndexOf("_");
            String call_number = filename.substring(34, numlastpositionstr);
            String call_time = filename.substring(16, 21);
            String call_date = filename.substring(5, 15);
            char[] call_timeArray = call_time.toCharArray();
            for (int x = 0; x < call_timeArray.length; x++) {
                if (call_timeArray[x] == '.') {
                    call_timeArray[x] = ':';
                }
            }
            call_time = String.valueOf(call_timeArray);
            holder.filetimecreated.setText(call_time);
            holder.filedatecreated.setText(call_date);

            holder.callfileName.setText(call_number);

            //status incomming or outgoing.
            if (filename.contains(CallStatus.INCOMING) || filename.contains(CallStatus.incoming)) {
//            if (call_type.equals(CallStatus.INCOMING) || call_type.equals(CallStatus.incoming)) {
                holder.callstatus1.setBackgroundResource(R.drawable.ic_call_received_black_24dp);
            } else {
                holder.callstatus1.setBackgroundResource(R.drawable.ic_call_made_black_24dp);
            }
//            if (call_type.equals(CallStatus.OUTGOING) || call_type.equals(CallStatus.outgoing)) {
//                holder.callstatus1.setBackgroundResource(android.R.drawable.sym_call_outgoing);
//            }
        } else {
            Log.d("audiofilesize", "Audio file size is 0");
        }

        holder.playpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder audioPlayDialog = new AlertDialog.Builder(context, R.style.DialogTheme);
                LayoutInflater inflater1 = LayoutInflater.from(context.getApplicationContext());
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                View layout1 = inflater1.inflate(R.layout.dialoguelayout, null);
                audioPlayDialog.setView(layout1)
                        .setNeutralButton("", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                mediaPlayer.pause();
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                myHandler.removeCallbacks(UpdateSongTime);
                                filecompleted = false;
                                running = false;
                            }
                        });
                audioPlayDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            mediaPlayer.pause();
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            myHandler.removeCallbacks(UpdateSongTime);
                            filecompleted = false;
                            running = false;
                        } catch (Exception e) {
                        }
                    }
                });
                audioPlayDialog.setCancelable(true);
                final ImageButton audioPlayDialogButton = (ImageButton) layout1.findViewById(R.id.your_dialog_button);
                audioPlayDialogSeekBar = (SeekBar) layout1.findViewById(R.id.your_dialog_seekbar);
                dfilename = (TextView) layout1.findViewById(R.id.dcallnameid);
                remaingtime = (TextView) layout1.findViewById(R.id.dremaingtime);
                totaltime = (TextView) layout1.findViewById(R.id.dtotaltime);
                callstatus = (TextView) layout1.findViewById(R.id.dcallstatus);
                audioPlayDialog.create();
                audioPlayDialog.show();

                String[] call_type1 = getcallstatus(position);
                callstatus.setText(call_type1[0]);
                if (call_type1[1].equals("")) {
                    dfilename.setText(call_type1[2]);
                } else {
                    dfilename.setText(call_type1[1]);
                }
                audioPlayDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (filecompleted == true) {
                            updateTime();
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    audioPlayDialogButton.setBackground(Resources.getSystem().getDrawable(android.R.drawable.ic_media_play));
                                }
                            } else {
                                mediaPlayer.start();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    audioPlayDialogButton.setBackground(Resources.getSystem().getDrawable(android.R.drawable.ic_media_pause));
                                }
                            }
                        } else {

                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    audioPlayDialogButton.setBackground(Resources.getSystem().getDrawable(android.R.drawable.ic_media_play));
                                }
                            } else {
                                mediaPlayer.start();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    audioPlayDialogButton.setBackground(Resources.getSystem().getDrawable(android.R.drawable.ic_media_pause));
                                }
                            }
                        }
                    }


                });

                audioPlayDialogSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        Log.d("Progress changed", "progress changed" + progress);
                        audioPlayDialogSeekBar.setMax((int) finalTime);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Log.d("starttracking", "progress starTT");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                final Uri data = Uri.parse(audioFileList.get(position).getMyfilepath());
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(context, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        finalTime = mp.getDuration();
                        running = true;
                        mp.start();

                        if (finalTime != 0) {
                            totaltime.setText(String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours((long) finalTime),
                                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                    toMinutes((long) finalTime))));
                        }
                    }

                });

                myHandler = new Handler();
                totaltime.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours((long) finalTime),
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) finalTime))));

                updateTime();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    audioPlayDialogButton.setBackground(Resources.getSystem().getDrawable(android.R.drawable.ic_media_pause));
                }
                firsttime = false;

                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        audioPlayDialogSeekBar.setProgress(0);
                        audioPlayDialogButton.setBackground(Resources.getSystem().getDrawable(android.R.drawable.ic_media_play));
                        filecompleted = true;
                        startTime = 0;
                        mediaPlayer.pause();
                        checkpoint = true;
                        mp.seekTo(0);
                        mp.pause();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return false;
                    }
                });
            }

            private void updateTime() {
                UpdateSongTime = new Runnable() {
                    public void run() {
                        if (running == true) {
                            startTime = mediaPlayer.getCurrentPosition();
                        }
                        if (filecompleted == true) {
                            if (checkpoint) {
                                startTime = 0;
                                checkpoint = false;
                            }
                        }
                        if (startTime > (finalTime - 100)) {
                            remaingtime.setText("00:00:00");

                        } else {
                            remaingtime.setText(String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours((long) startTime),
                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                    toMinutes((long) startTime))));
                            audioPlayDialogSeekBar.setProgress((int) startTime);
                            myHandler.postDelayed(this, 100);
                        }

                    }
                };

                myHandler1 = new Handler();
                UpdateSongTime1 = new Runnable() {
                    public void run() {
                        myHandler.postDelayed(this, 100);
                    }
                };
                startTime = mediaPlayer.getCurrentPosition();
                audioPlayDialogSeekBar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);

            }
        });

        return rowView;

    }

    public interface CustomAdapterInterface extends OnCompletionListener {
        void onItemClick(int position);

    }

    public String[] getcallstatus(int i) {
        String str = audioFileList.get(i).filename;
        String call_type1 = str.substring(25, 33);
        int numlastpositionstr = str.lastIndexOf("_");
        String call_number = str.substring(34, numlastpositionstr);
        String contactName = "";
        String[] array = {call_type1, contactName, call_number};
        return array;
    }
}