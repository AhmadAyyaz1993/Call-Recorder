package net.net76.mannan.callrecorder.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.constants.Constants;
import net.net76.mannan.callrecorder.util.PrefManager;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity {

    Switch allSwitch, incomingSwitch, outgoingSwitch;
    PrefManager prefManager;
    TextView shareapp, apprestart;
    SharedPreferences sp;
    Switch tgbutton;
    boolean toglevalueh;
    ComponentName componentName1;
    int min = 1234;
    int max = 12345;
    int randumnum;
    String randumnumberlogical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab();
        initializeViews();
        setDefaultSwitchStates();
        switchListners();

        Random r = new Random();
        randumnum = r.nextInt(max - min + 1) + min;

        myListeners();
    }

    private void initializeViews() {
        allSwitch = (Switch) findViewById(R.id.recordAllSwitch);
        incomingSwitch = (Switch) findViewById(R.id.recordIncomingSwitch);
        outgoingSwitch = (Switch) findViewById(R.id.recordOutgoinSwitch);
        prefManager = new PrefManager(getApplicationContext());
        shareapp = (TextView) findViewById(R.id.shareapp);
        apprestart = (TextView) findViewById(R.id.btn_restart);
        tgbutton = (Switch) findViewById(R.id.switchAB1);
        componentName1 = new ComponentName(this, SplashActivity.class);
        SharedPreferences settings2 = getSharedPreferences("login", MODE_PRIVATE);
        toglevalueh = settings2.getBoolean("tglvalh", Boolean.parseBoolean(null));
    }

    private void setDefaultSwitchStates() {
        if (prefManager.getCallRecord() == Constants.RECORD_ALL) {
            allSwitch.setChecked(true);
            incomingSwitch.setChecked(true);
            outgoingSwitch.setChecked(true);
        } else if (prefManager.getCallRecord() == Constants.RECORD_OUTGOING) {
            allSwitch.setChecked(false);

            outgoingSwitch.setChecked(true);
        } else if (prefManager.getCallRecord() == Constants.RECORD_INCOMING) {
            allSwitch.setChecked(false);
            incomingSwitch.setChecked(true);

        }

        if (toglevalueh) {

            tgbutton.setChecked(true);
        }
        if (!toglevalueh) {
            tgbutton.setChecked(false);
        }

        tgbutton.setChecked(false);

    }

    private void switchListners() {
        allSwitchListner();
        incomingSwitchListner();
        outgoinSwitchListner();
    }

    private void allSwitchListner() {
        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    incomingSwitch.setChecked(true);
                    outgoingSwitch.setChecked(true);
                    prefManager.setCallRecord(Constants.RECORD_ALL);
                } else {
                    incomingSwitch.setChecked(false);
                    outgoingSwitch.setChecked(false);
                    prefManager.setCallRecord(Constants.RECORD_NOTHING);
                }
            }
        });
    }

    private void incomingSwitchListner() {
        incomingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefManager.setCallRecord(Constants.RECORD_INCOMING);
                    incomingSwitch.setChecked(true);
                    if (outgoingSwitch.isChecked()) {
                        allSwitch.setChecked(true);
                    }
                } else {
                    allSwitch.setOnCheckedChangeListener(null);
                    allSwitch.setChecked(false);
                    allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                incomingSwitch.setChecked(true);
                                outgoingSwitch.setChecked(true);
                                prefManager.setCallRecord(Constants.RECORD_ALL);
                            } else {
                                incomingSwitch.setChecked(false);
                                outgoingSwitch.setChecked(false);
                                prefManager.setCallRecord(Constants.RECORD_NOTHING);
                            }
                        }
                    });
                }
            }
        });
    }

    private void outgoinSwitchListner() {
        outgoingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefManager.setCallRecord(Constants.RECORD_OUTGOING);
                    outgoingSwitch.setChecked(true);
                    if (incomingSwitch.isChecked()) {
                        allSwitch.setChecked(true);
                    }
                } else {
                    allSwitch.setOnCheckedChangeListener(null);
                    allSwitch.setChecked(false);
                    allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                incomingSwitch.setChecked(true);
                                outgoingSwitch.setChecked(true);
                                prefManager.setCallRecord(Constants.RECORD_ALL);
                            } else {
                                incomingSwitch.setChecked(false);
                                outgoingSwitch.setChecked(false);
                                prefManager.setCallRecord(Constants.RECORD_NOTHING);
                            }
                        }
                    });
                }
            }
        });
    }

    private void myListeners() {
        shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=net.net76.mannan.callrecorder");

                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Choose sharing method"));
            }
        });
        apprestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        tgbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (tgbutton.isChecked()) {


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                    alertDialog.setCancelable(false);
                    // Setting Dialog Title
                    alertDialog.setTitle("Confirmation");
                    String strI = String.valueOf(gen());
                    randumnumberlogical = strI.toString();
                    // Setting Dialog Message
                    //   Html.fromHtml("Are you sure you want to hide this Application on Descktop?it will take few seconds to take place, Dialer Code: "+"<b>"+randumnumberlogical+"</b>");
                    alertDialog.setMessage(Html.fromHtml("Are you sure you want to hide this Application on Descktop?it will take few seconds to take place, Dialer Code: " + "<b>" + randumnumberlogical + "</b>"));

                    //     alertDialog.setMessage("Are you sure you want to hide this Application on Descktop?it will take few seconds to take place, Dialer Code: "+randumnumberlogical);

                    // Setting Icon to Dialog
                    //   alertDialog.setIcon(R.drawable.delete);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            PackageManager p1 = getPackageManager();

                            p1.setComponentEnabledSetting(componentName1, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                            ComponentName componentName = new ComponentName(getApplicationContext(), SplashActivity.class);
                            getPackageManager().setComponentEnabledSetting(
                                    componentName,
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                    PackageManager.DONT_KILL_APP);


                            sp = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor e1 = sp.edit();
                            e1.putBoolean("tglvalh", true);
                            // e1.putString(String.valueOf(randumnumberlogical), "");
                            e1.putString("randumval", randumnumberlogical);

                            e1.commit();

                            //    android.os.Process.killProcess(android.os.Process.myPid());

                            finish();

                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event

                            tgbutton.setChecked(false);

                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();


                }
                if (!tgbutton.isChecked()) {
                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(SettingsActivity.this, SplashActivity.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                    sp = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor e2 = sp.edit();
                    e2.putBoolean("tglvalh", false);
                    e2.commit();

                    Toast.makeText(SettingsActivity.this, "Your Application is now visible on your Desktop", Toast.LENGTH_SHORT).show();
                    tgbutton.setChecked(true);
                }
            }
        });
    }

    public int gen() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    private void fab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
