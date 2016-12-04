package net.net76.mannan.callrecorder.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.constants.Constants;
import net.net76.mannan.callrecorder.util.PrefManager;

public class SettingsActivity extends AppCompatActivity {

    Switch allSwitch, incomingSwitch, outgoingSwitch;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab();
        initializeViews();
        setDefaultSwitchStates();
        switchListners();

    }

    private void initializeViews() {
        allSwitch = (Switch) findViewById(R.id.recordAllSwitch);
        incomingSwitch = (Switch) findViewById(R.id.recordIncomingSwitch);
        outgoingSwitch = (Switch) findViewById(R.id.recordOutgoinSwitch);
        prefManager = new PrefManager(getApplicationContext());
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
                    if (incomingSwitch.isChecked()) {
                        prefManager.setCallRecord(Constants.RECORD_ALL);
                        incomingSwitch.setChecked(true);
                        outgoingSwitch.setChecked(true);
                    } else {
                        prefManager.setCallRecord(Constants.RECORD_INCOMING);
                    }
                } else {
                    if (allSwitch.isChecked()) {
                        prefManager.setCallRecord(Constants.RECORD_OUTGOING);
                    } else {
                        prefManager.setCallRecord(Constants.RECORD_NOTHING);
                    }
                }
            }
        });
    }

    private void outgoinSwitchListner() {
        outgoingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (incomingSwitch.isChecked()) {
                        prefManager.setCallRecord(Constants.RECORD_ALL);
                    } else {
                        prefManager.setCallRecord(Constants.RECORD_OUTGOING);
                    }
                } else {
                    if (allSwitch.isChecked()) {
                        prefManager.setCallRecord(Constants.RECORD_INCOMING);
                    } else {
                        prefManager.setCallRecord(Constants.RECORD_NOTHING);
                    }
                }
            }
        });
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
