package com.shreyas208.cs125lecture;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

public class SubmitActivity extends AppCompatActivity {

    private EditText etUserNetID;
    private EditText etPartnerNetID;
    private SeekBar sbRating;
    private TextView tvRating;
    private EditText etFeedbackStruggling;
    private EditText etFeedbackGood;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        etUserNetID = (EditText)findViewById(R.id.etUserNetID);
        etPartnerNetID = (EditText)findViewById(R.id.etPartnerNetID);
        sbRating = (SeekBar)findViewById(R.id.sbRating);
        tvRating = (TextView)findViewById(R.id.tvRating);
        etFeedbackGood = (EditText)findViewById(R.id.etFeedbackGood);
        etFeedbackStruggling = (EditText)findViewById(R.id.etFeedbackStruggling);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);

        this.setTitle(getString(R.string.title_activity_submit));

        sbRating.incrementProgressBy(1);
        sbRating.setMax(9);
        sbRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {updateRatingText();}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        updateRatingText();

        etFeedbackGood.setSingleLine(true);
        etFeedbackGood.setLines(3);
        etFeedbackGood.setHorizontallyScrolling(false);
        etFeedbackGood.setVerticalScrollBarEnabled(true);
        etFeedbackGood.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        etFeedbackStruggling.setSingleLine(true);
        etFeedbackStruggling.setLines(3);
        etFeedbackStruggling.setHorizontallyScrolling(false);
        etFeedbackStruggling.setVerticalScrollBarEnabled(true);
        etFeedbackStruggling.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Retrieve saved NetID
        etUserNetID.setText(getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.shared_pref_key_netid),""));

    }

    private void updateRatingText() {
        tvRating.setText(String.valueOf(sbRating.getProgress() + 1));
    }

    public void processSubmission(View v) {
        // Retrieve input values
        String userNetID = etUserNetID.getText().toString().trim().toLowerCase();
        String partnerNetID = etPartnerNetID.getText().toString().trim().toLowerCase();
        int rating = sbRating.getProgress() + 1;
        String feedbackGood = etFeedbackGood.getText().toString().trim();
        String feedbackStruggling = etFeedbackStruggling.getText().toString().trim();

        // Validate NetIDs
        if (userNetID.isEmpty()) {
            Toast.makeText(this, getString(R.string.submit_toast_userNetID_blank), Toast.LENGTH_SHORT).show();
            return;
        }
        if (partnerNetID.isEmpty()) {
            Toast.makeText(this, getString(R.string.submit_toast_partnerNetID_blank), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(Pattern.matches("[A-Z,a-z,0-9,-]+",userNetID) && Pattern.matches("[A-Z,a-z,0-9,-]+",partnerNetID))) {
            Toast.makeText(this, getString(R.string.submit_toast_netID_invalid), Toast.LENGTH_LONG).show();
            return;
        }

        // Save user's NetID
        getPreferences(Context.MODE_PRIVATE).edit().putString(getString(R.string.shared_pref_key_netid),userNetID).commit();

        String[] submissionData = new String[] {userNetID, partnerNetID, String.valueOf(rating), feedbackGood, feedbackStruggling};

        // submit to server

        // check success

        // if successful
        {
            // add submission to local database
            SubmissionDbHelper submissionDbHelper = new SubmissionDbHelper(SubmitActivity.this);
            submissionDbHelper.addSubmission(submissionData[0], submissionData[1], Integer.parseInt(submissionData[2]), submissionData[3], submissionData[4]);

            startActivity(new Intent(SubmitActivity.this, HistoryActivity.class));
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_history) {
            startActivity(new Intent(SubmitActivity.this, HistoryActivity.class));
            return true;
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(SubmitActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
