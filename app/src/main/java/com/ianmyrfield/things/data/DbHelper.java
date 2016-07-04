package com.ianmyrfield.things.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ianmyrfield.things.data.NoteContract.NoteItems;
import com.ianmyrfield.things.data.NoteContract.NoteTitles;

/**
 * Created by Ian on 6/10/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "things.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper (Context c) {
        super( c, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate (SQLiteDatabase db) {

        // Create the Title Table
        final String CREATE_NOTE_TITLE_TABLE = "CREATE TABLE " +
                NoteTitles.TABLE_NAME + " (" +
                NoteTitles._ID + " INTEGER PRIMARY KEY, " +
                NoteTitles.COL_TITLE + " TEXT NOT NULL, " +
                NoteTitles.COL_CREATED_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                NoteTitles.COL_COLOR + " TEXT " + " );";

        db.execSQL(CREATE_NOTE_TITLE_TABLE);
        Log.d("DbHelper", "onCreate (line 35): " + CREATE_NOTE_TITLE_TABLE );

        // Create Note Item Table
        final String CREATE_NOTE_ITEM_TABLE = "CREATE TABLE " +
                NoteItems.TABLE_NAME + " (" +
                NoteItems._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteItems.COL_ITEM_CONTENT + " TEXT NOT NULL, " +
                NoteItems.COL_TITLE_KEY + " TEXT NOT NULL, " +
                NoteItems.COL_CREATED_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                NoteItems.COL_REMINDER + " TEXT, " +

                "FOREIGN KEY (" + NoteItems.COL_TITLE_KEY + ") REFERENCES " +
                NoteTitles.TABLE_NAME + " (" + NoteTitles.COL_TITLE + "));";

        db.execSQL(CREATE_NOTE_ITEM_TABLE);
        Log.d("DbHelper", "onCreate (line 35): " + CREATE_NOTE_ITEM_TABLE );
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d( "DbHelper", "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED.");

        // FIX: Currently this drops existing table if DB upgrades.
        // As the DB is going to store user favorites this likely isn't a good policy
        db.execSQL("DROP IF TABLE EXISTS " + NoteItems.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + NoteTitles.TABLE_NAME);

        // Recreates the database
        onCreate(db);
    }
}
