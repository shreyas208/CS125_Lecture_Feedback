package com.shreyas208.cs125lecture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ((TextView)findViewById(R.id.about_tvByShreyasPatil)).setText(Html.fromHtml(getString(R.string.about_byShreyasPatil)));
        ((TextView)findViewById(R.id.about_tvForCS125)).setText(Html.fromHtml(getString(R.string.about_forCS125)));
        ((TextView)findViewById(R.id.about_tvAtUIUC)).setText(Html.fromHtml(getString(R.string.about_atUIUC)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
