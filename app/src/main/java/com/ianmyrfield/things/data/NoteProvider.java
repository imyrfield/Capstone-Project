package com.ianmyrfield.things.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Ian on 6/10/2016.
 */
public class NoteProvider
        extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    // INT codes to return as matches in UriMatcher
    private static final int NOTE_TITLE       = 100;
    private static final int NOTE_ITEM        = 101;
    private static final int NOTES_WITH_TITLE = 102;

    private static final SQLiteQueryBuilder sNotesWithTitleQueryBuilder;

    static {
        sNotesWithTitleQueryBuilder = new SQLiteQueryBuilder();
        sNotesWithTitleQueryBuilder.setTables( NoteContract.NoteItems.TABLE_NAME + " INNER JOIN " +
                                                       NoteContract.NoteTitles.TABLE_NAME +
                                                       " ON " + NoteContract.NoteItems.TABLE_NAME +
                                                       "." + NoteContract.NoteItems.COL_TITLE_KEY +
                                                       " = " + NoteContract.NoteTitles.TABLE_NAME +
                                                       "." + NoteContract.NoteTitles._ID );
    }

    // note_titles.title = ?

    private static final String sNoteTitleSelection = NoteContract.NoteItems.TABLE_NAME +
            "." + NoteContract.NoteItems.COL_TITLE_KEY + " = " + "? ";

    private Cursor getItemsForTitle (Uri uri, String[] projection, String sortOrder) {
        String title = NoteContract.NoteItems.getColNoteTitle( uri );

        String   selection     = sNoteTitleSelection;
        String[] selectionArgs = new String[]{ title };
        return sNotesWithTitleQueryBuilder.query( mDbHelper.getReadableDatabase(),
                                                  projection,
                                                  selection,
                                                  selectionArgs,
                                                  null,
                                                  null,
                                                  sortOrder );
    }

    private static UriMatcher buildUriMatcher () {

        final UriMatcher matcher   = new UriMatcher( UriMatcher.NO_MATCH );
        final String     authority = NoteContract.CONTENT_AUTHORITY;

        matcher.addURI( authority, NoteContract.PATH_NOTE_TITLES, NOTE_TITLE );
        matcher.addURI( authority, NoteContract.PATH_NOTE_ITEMS, NOTE_ITEM );
        matcher.addURI( authority, NoteContract.PATH_NOTE_ITEMS + "/*", NOTES_WITH_TITLE );

        return matcher;
    }

    @Override
    public boolean onCreate () {
        mDbHelper = new DbHelper( getContext() );
        return true;
    }

    @Nullable
    @Override
    public Cursor query (Uri uri, String[] projection, String selection,
                         String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match( uri )) {
            // "title"
            case NOTE_TITLE:{
                retCursor = mDbHelper.getReadableDatabase().query(
                        NoteContract.NoteTitles.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "item"
            case NOTE_ITEM:{
                retCursor = mDbHelper.getReadableDatabase().query(
                        NoteContract.NoteItems.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "items/*"
            case NOTES_WITH_TITLE:{
                retCursor = getItemsForTitle( uri, projection, sortOrder );
                break;
            }
            default:
                throw new UnsupportedOperationException( "Unknown uri: " + uri );
        }
        retCursor.setNotificationUri( getContext().getContentResolver(), uri );
        return retCursor;
    }

    @Nullable
    @Override
    public String getType (Uri uri) {
        final int match = sUriMatcher.match( uri );

        switch (match) {
            case NOTE_TITLE:
                return NoteContract.NoteTitles.CONTENT_DIR_TYPE;
            case NOTE_ITEM:
                return NoteContract.NoteItems.CONTENT_DIR_TYPE;
            case NOTES_WITH_TITLE:
                return NoteContract.NoteItems.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException( "Unknown Uri: " + uri );
        }
    }

    @Nullable
    @Override
    public Uri insert (Uri uri, ContentValues values) {

        final SQLiteDatabase db    = mDbHelper.getWritableDatabase();
        final int            match = sUriMatcher.match( uri );
        Uri                  returnUri;

        switch (match) {
            case NOTE_TITLE: {
                long _id = db.insert( NoteContract.NoteTitles.TABLE_NAME, null, values );
                if (_id > 0) {
                    returnUri = NoteContract.NoteTitles.buildNoteTitleUri( _id );
                } else {
                    throw new SQLException( "Failed to insert row into " + uri );
                }
                break;
            }
            case NOTE_ITEM: {
                long _id = db.insert( NoteContract.NoteItems.TABLE_NAME, null, values );
                if (_id > 0) {
                    returnUri = NoteContract.NoteItems.buildNoteItemUri( _id );
                } else {
                    throw new SQLException( "Failed to insert row into " + uri );
                }
                break;
            }
            default:
                throw new UnsupportedOperationException( "Unknown uri: " + uri );
        }

        getContext().getContentResolver().notifyChange( uri, null );
        return returnUri;
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db    = mDbHelper.getWritableDatabase();
        final int            match = sUriMatcher.match( uri );
        int                  rowsDeleted;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case NOTE_TITLE:
                rowsDeleted = db.delete( NoteContract.NoteTitles.TABLE_NAME,
                                         selection,
                                         selectionArgs );
                break;
            case NOTE_ITEM:
                rowsDeleted = db.delete( NoteContract.NoteItems.TABLE_NAME,
                                         selection,
                                         selectionArgs );
                break;
            default:
                throw new UnsupportedOperationException( "Unknown uri: " + uri );
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange( uri, null );
        }
        return rowsDeleted;
    }

    @Override
    public int update (Uri uri, ContentValues values, String selection,
                       String[] selectionArgs) {

        final SQLiteDatabase db    = mDbHelper.getWritableDatabase();
        final int            match = sUriMatcher.match( uri );
        int                  rowsUpdated;

        switch (match) {
            case NOTE_TITLE:
                rowsUpdated = db.update( NoteContract.NoteTitles.TABLE_NAME,
                                         values,
                                         selection,
                                         selectionArgs);
                break;
            case NOTE_ITEM:
                rowsUpdated = db.update(NoteContract.NoteItems.TABLE_NAME,
                                        values,
                                        selection,
                                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException( "Unknown uri: " + uri );
        }
        if (rowsUpdated !=0 ) {
            getContext().getContentResolver().notifyChange( uri, null );
        }

        return rowsUpdated;
    }
}
