package com.shreyas208.cs125lecture;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HistoryActivity extends AppCompatActivity {

    ListView lvSubmissions;
    SubmissionDbHelper submissionDbHelper;
    SimpleCursorAdapter cursorAdapter;

    String[] columns = new String[] {SubmissionDbHelper.COLUMN_NAME_TIMESTAMP, SubmissionDbHelper.COLUMN_NAME_USER_NETID, SubmissionDbHelper.COLUMN_NAME_PARTNER_NETID, SubmissionDbHelper.COLUMN_NAME_LECTURE_RATING};
    int[] layoutIds = new int[] {R.id.history_item_tvTimestamp, R.id.history_item_tvUserNetID, R.id.history_item_tvPartnerNetID, R.id.history_item_tvLectureRating};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvSubmissions = (ListView)findViewById(R.id.history_lvSubmissions);

        submissionDbHelper = new SubmissionDbHelper(HistoryActivity.this);

        cursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.history_item,
                submissionDbHelper.fetchAllSubmissions(),
                columns,
                layoutIds,
                0);

        lvSubmissions.setAdapter(cursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history_clear) {
            submissionDbHelper.clearDatabase(submissionDbHelper.getReadableDatabase());
            finish();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
