package com.shreyas208.cs125lecture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
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

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.regex.Pattern;

public class SubmitActivity extends AppCompatActivity {

    private EditText etUserNetID;
    private EditText etPartnerNetID;
    private SeekBar sbRating;
    private TextView tvRating;
    private EditText etFeedbackStruggling;
    private EditText etFeedbackGood;
    private Button btnSubmit;

    private final OkHttpClient client = new OkHttpClient();

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

        btnSubmit.setEnabled(true);

    }

    private void updateRatingText() {
        tvRating.setText(String.valueOf(sbRating.getProgress() + 1));
    }

    public void processSubmission(View v) {
        btnSubmit.setEnabled(false);

        // Retrieve input values
        String userNetID = etUserNetID.getText().toString().trim().toLowerCase();
        String partnerNetID = etPartnerNetID.getText().toString().trim().toLowerCase();
        int rating = sbRating.getProgress() + 1;
        String feedbackGood = etFeedbackGood.getText().toString().trim();
        String feedbackStruggling = etFeedbackStruggling.getText().toString().trim();

        // Validate NetIDs
        if (userNetID.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.submit_toast_userNetID_blank), Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true);
            return;
        }
        if (partnerNetID.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.submit_toast_partnerNetID_blank), Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true);
            return;
        }
        if (!(Pattern.matches("[A-Za-z0-9-]+",userNetID) && Pattern.matches("[A-Za-z0-9-]+",partnerNetID))) {
            Toast.makeText(getApplicationContext(), getString(R.string.submit_toast_netID_invalid), Toast.LENGTH_LONG).show();
            btnSubmit.setEnabled(true);
            return;
        }

        // Save user's NetID
        getPreferences(Context.MODE_PRIVATE).edit().putString(getString(R.string.shared_pref_key_netid),userNetID).commit();

        try {
            new PostForm().run(userNetID, partnerNetID, rating, feedbackGood, feedbackStruggling);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CS 125", e.getMessage());
            btnSubmit.setEnabled(true);
            Toast.makeText(getApplicationContext(), getString(R.string.submit_toast_server_error), Toast.LENGTH_LONG).show();
        }
    }

    public class PostForm {

        //public static final String HTTP_POST_URL = "http://shreyas208.com/CS125LectureFeedback/submit.php";
        public static final String HTTP_POST_URL = "http://cs125class.web.engr.illinois.edu/processfeedback.php";

        private final OkHttpClient client = new OkHttpClient();

        public void run(final String userNetID, final String partnerNetID, final int rating, final String feedbackGood, final String feedbackStruggling) {
            final RequestBody formBody = new FormEncodingBuilder()
                    .add("yournetid", userNetID)
                    .add("theirnetid", partnerNetID)
                    .add("lecturerating", String.valueOf(rating))
                    .add("understand", feedbackGood)
                    .add("struggle", feedbackStruggling)
                    .build();
            Request request = new Request.Builder()
                    .url(HTTP_POST_URL)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                    Log.e("CS 125", e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            btnSubmit.setEnabled(true);
                            Toast.makeText(getApplicationContext(), getString(R.string.submit_toast_network_error), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    SubmissionDbHelper submissionDbHelper = new SubmissionDbHelper(SubmitActivity.this);
                    submissionDbHelper.addSubmission(userNetID, partnerNetID, rating, feedbackGood, feedbackStruggling);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            btnSubmit.setEnabled(true);
                            etPartnerNetID.setText("");
                            etFeedbackGood.setText("");
                            etFeedbackStruggling.setText("");
                            sbRating.setProgress(5);
                            updateRatingText();
                        }
                    });

                    startActivity(new Intent(SubmitActivity.this, HistoryActivity.class));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
