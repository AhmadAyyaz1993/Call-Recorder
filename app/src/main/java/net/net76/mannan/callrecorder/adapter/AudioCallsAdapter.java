package net.net76.mannan.callrecorder.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.constants.CallStatus;
import net.net76.mannan.callrecorder.constants.MyLogTags;
import net.net76.mannan.callrecorder.model.CallAllDetails;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static net.net76.mannan.callrecorder.service.MyVoiceRecordingService.audioFileDirectoryPath;

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
    public ArrayList<CallAllDetails> audioFileList = new ArrayList<CallAllDetails>();
    private static LayoutInflater inflater = null;
    private static MediaMetadataRetriever mmr;
    String name = "Incoming call from";
    public Context context1;
    Handler handler = new Handler();

    public AudioCallsAdapter(Context context, ArrayList<CallAllDetails> filespathlist) {
        // TODO Auto-generated constructor stub
        this.audioFileList = filespathlist;
        this.context = context;
        this.context1 = context;
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
        private ImageView btnshare;
        private TextView number;
        private LinearLayout names;
        private RelativeLayout rowmain;

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
        holder.btnshare = (ImageView) rowView.findViewById(R.id.btn_share);
        holder.number = (TextView) rowView.findViewById(R.id.txt_number);
        holder.names = (LinearLayout) rowView.findViewById(R.id.names);
        holder.filetimecreated = (TextView) rowView.findViewById(R.id.myaudiofiletime);
        holder.filedatecreated = (TextView) rowView.findViewById(R.id.myaudiofiledate);
        holder.rowmain = (RelativeLayout) rowView.findViewById(R.id.rowmain);
        holder.rowmain.setVisibility(View.VISIBLE);

     /*
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(audioFileList.get(position).getMyfilepath());
        String durationStr1 = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond1 = Integer.parseInt(durationStr1);
        String time=String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours((long) millSecond1),
                TimeUnit.MILLISECONDS.toMinutes((long) millSecond1),
                TimeUnit.MILLISECONDS.toSeconds((long) millSecond1) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) millSecond1)));
        System.out.println("Time in Adapter is "+time);

        */
        if (audioFileList.size() > 0) {
            try {


                mmr = new MediaMetadataRetriever();
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
                String name = getContactDisplayNameByNumber(call_number, context);
                holder.number.setText(call_number);
                holder.callfileName.setText(name);

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
            } catch (Exception e) {
                System.out.println("Exaption is here " + e);
            }
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
//Share Audio
                        /*final Uri data = Uri.parse(audioFileList.get(position).getMyfilepath());

                        Intent share = new Intent(Intent.ACTION_SEND); //share intent
                        share.setType("audio*//*");
                        share.putExtra(Intent.EXTRA_STREAM, data);
                        context.startActivity(Intent.createChooser(share, "Share sound to"));*/

                        if (filecompleted == true) {
                            updateTime();
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    audioPlayDialogButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                                }
                            } else {
                                mediaPlayer.start();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    audioPlayDialogButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                                }
                            }
                        } else {

                            try {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        audioPlayDialogButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                                    }
                                } else {
                                    mediaPlayer.start();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        audioPlayDialogButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }


                });

                audioPlayDialogSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        try {
                            if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                                if (fromUser)
                                    mediaPlayer.seekTo(progress);
                            } else if (mediaPlayer == null) {
                              /*  Toast.makeText(context.getApplicationContext(), "Media is not running",
                                        Toast.LENGTH_SHORT).show();*/
                                seekBar.setProgress(0);
                            }
                        } catch (Exception e) {
                            Log.e("seek bar", "" + e);
                            seekBar.setEnabled(false);

                        }
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

                System.out.println("file Path is here" + data.getPath());
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(context, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {


                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            finalTime = mp.getDuration();
                            running = true;
                            mp.start();
                            //      audioPlayDialogSeekBar = true;
                            audioPlayDialogSeekBar.setMax(mediaPlayer.getDuration());
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
                } catch (Exception e) {
                }
                myHandler = new Handler();
                totaltime.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours((long) finalTime),
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) finalTime))));

                updateTime();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    audioPlayDialogButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                }
                firsttime = false;

                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        audioPlayDialogSeekBar.setProgress(0);
                        audioPlayDialogButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                        filecompleted = true;
                        startTime = 0;
                        mediaPlayer.pause();
                        checkpoint = true;
                        mp.seekTo(10);
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
        holder.btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  final Uri data = Uri.parse(audioFileList.get(position).getMyfilepath());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(audioFileList.get(position).getMyfilepath())));
                context.startActivity(intent);

        /*    Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("audio*//**//*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(audioFileList.get(position).getMyfilepath())));
                context.startActivity(intent);*/
                // deleteVoiceFile((audioFileList.get(position).filename));
//
                //  AudioCallsAdapter adapter=new AudioCallsAdapter(context1,audioFileList);
                //  AudioCallsListActivity audioCallsListActivity=new AudioCallsListActivity();
//                audioCallsListActivity.recordingsListView.setAdapter(adapter);
                // adapter.notifyDataSetChanged();

                //   AudioCallsAdapter.this.notifyDataSetChanged();


                //   adapter.notifyDataSetInvalidated();
                //   adapter.notifyDataSetChanged();


            }
        });
        holder.btnshare.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //   Toast.makeText(context, " " + position, Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Delete Confirmation!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this record?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(audioFileList.get(position).getMyfilepath())));
                                deleteVoiceFile(audioFileList.get(position).getFilename());
                                audioFileList.remove(position);
                                // audioFileList.clear();
                                AudioCallsAdapter.this.notifyDataSetInvalidated();
                                AudioCallsAdapter.this.notifyDataSetChanged();
                                //    AudioCallsAdapter adapter = new AudioCallsAdapter(context1,audioFileList);

                                //    recordingsListView.setAdapter(adapter);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return false;
            }
        });
        holder.playpausebtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Delete Confirmation!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this record?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(audioFileList.get(position).getMyfilepath())));
                                deleteVoiceFile(audioFileList.get(position).getFilename());
                                audioFileList.remove(position);
                                // audioFileList.clear();
                                AudioCallsAdapter.this.notifyDataSetInvalidated();
                                AudioCallsAdapter.this.notifyDataSetChanged();
                                //    AudioCallsAdapter adapter = new AudioCallsAdapter(context1,audioFileList);

                                //recordingsListView.setAdapter(adapter);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return false;
            }
        });
        holder.names.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Delete Confirmation!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this record?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(audioFileList.get(position).getMyfilepath())));
                                deleteVoiceFile(audioFileList.get(position).getFilename());
                                audioFileList.remove(position);
                                // audioFileList.clear();
                                AudioCallsAdapter.this.notifyDataSetInvalidated();
                                AudioCallsAdapter.this.notifyDataSetChanged();
                                //    AudioCallsAdapter adapter = new AudioCallsAdapter(context1,audioFileList);

                                //recordingsListView.setAdapter(adapter);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return false;
            }
        });
        holder.callstatus1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Delete Confirmation!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this record?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(audioFileList.get(position).getMyfilepath())));
                                deleteVoiceFile(audioFileList.get(position).getFilename());
                                audioFileList.remove(position);
                                getcallstatus(position);

//                                Collections.sort(audioFileList, (Comparator<? super CallAllDetails>) audioFileList);
                                //     audioFileList.notifyAll();
                                // audioFileList.clear();
                                AudioCallsAdapter.this.notifyDataSetInvalidated();
                                AudioCallsAdapter.this.notifyDataSetChanged();
                                //  AudioCallsAdapter adapter = new AudioCallsAdapter(context1,audioFileList);
                                //  recordingsListView.setAdapter(adapter);
                                getcallstatus(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return false;
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

    public String getContactDisplayNameByNumber(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        name = "Incoming call from";

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
                name = "Unknown Number";
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }


    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.applogo);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
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

}