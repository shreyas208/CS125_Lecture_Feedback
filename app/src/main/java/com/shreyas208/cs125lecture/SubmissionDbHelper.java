package com.shreyas208.cs125lecture;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.Date;

public class SubmissionDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3; // increment to trigger DB rebuild on install
    public static final String DATABASE_NAME = "Submissions.db";

    public static final String TABLE_NAME = "submissions";

    public static final String COLUMN_NAME_SERIAL = "serial";
    public static final String COLUMN_NAME_TIMESTAMP = "submittime";
    public static final String COLUMN_NAME_USER_NETID = "usernetid";
    public static final String COLUMN_NAME_PARTNER_NETID = "partnernetid";
    public static final String COLUMN_NAME_LECTURE_RATING = "lecturerating";
    public static final String COLUMN_NAME_FEEDBACK_GOOD = "feedbackgood";
    public static final String COLUMN_NAME_FEEDBACK_STRUGGLING = "feedbackstruggling";

    public SubmissionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "\n(\n"
                + COLUMN_NAME_SERIAL + " INTEGER PRIMARY KEY,\n"
                + COLUMN_NAME_TIMESTAMP + " TEXT,\n"
                + COLUMN_NAME_USER_NETID + " VARCHAR(64),\n"
                + COLUMN_NAME_PARTNER_NETID + " VARCHAR(64),\n"
                + COLUMN_NAME_LECTURE_RATING + " INTEGER,\n"
                + COLUMN_NAME_FEEDBACK_GOOD + " TEXT,\n"
                + COLUMN_NAME_FEEDBACK_STRUGGLING + " TEXT\n"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearDatabase(db);
    }

    public void addSubmission(String userNetID, String partnerNetID, int lectureRating, String feedbackGood, String feedbackStruggling) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_TIMESTAMP, DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
        values.put(COLUMN_NAME_USER_NETID, userNetID);
        values.put(COLUMN_NAME_PARTNER_NETID, partnerNetID);
        values.put(COLUMN_NAME_LECTURE_RATING, lectureRating);
        values.put(COLUMN_NAME_FEEDBACK_GOOD, feedbackGood);
        values.put(COLUMN_NAME_FEEDBACK_STRUGGLING, feedbackStruggling);
        db.insert(TABLE_NAME, null, values);
    }

    public void clearDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor fetchAllSubmissions() {
        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT rowid _id,* FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAME_SERIAL + " DESC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}