package com.ianmyrfield.things.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ian on 6/10/2016.
 */
public class NoteContract {

    public NoteContract () {}

    public static final String CONTENT_AUTHORITY = "com.ianmyrfield.things";

    // Base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://" + CONTENT_AUTHORITY  );

    // Possible paths (appended to base content URI for possible URI's)
    // [com.ianmyrfield.things/note]
    public static final String PATH_NOTE_TITLES = "note_titles";
    public static final String PATH_NOTE_ITEMS  = "note_items";

    public static final class NoteTitles implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(
                PATH_NOTE_TITLES ).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE_TITLES;

        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE_TITLES;

        public static final String TABLE_NAME = "note_titles";
        public static final String COL_TITLE = "title";
        public static final String COL_CREATED_DATE = "created_dated";
        public static final String COL_COLOR = "note_color";


        public static Uri buildNoteTitleUri(long id){
            return ContentUris.withAppendedId( CONTENT_URI, id );
        }

        /**
         * Get Color assosciated with Note
         * @param uri
         * @return color of note
         */
        public static String getColColorFromUri(Uri uri){
            return uri.getPathSegments().get( 3 );
        }
    }

    public static final class NoteItems implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(
                PATH_NOTE_ITEMS ).build() ;

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE_ITEMS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE_ITEMS;

        public static final String TABLE_NAME = "note_items";
        public static final String COL_ITEM_CONTENT = "item_content";
        public static final String COL_TITLE_KEY = "title_id";
        public static final String COL_CREATED_DATE = "created_date";
        public static final String COL_REMINDER = "item_reminder";


        public static Uri buildNoteItemUri (long id) {
            return ContentUris.withAppendedId( CONTENT_URI, id );
        }

        public static Uri buildNoteWithTitleUri(String title){
            return CONTENT_URI.buildUpon().appendPath( title ).build();
        }

        public static Uri buildNoteWithAlarmUri(String reminder){
            return CONTENT_URI.buildUpon().appendPath( reminder ).build();
        }

        public static String getColItemContent (Uri uri) {
            return uri.getPathSegments().get( 1 );
        }

        public static String getColNoteTitle(Uri uri) {
            return uri.getPathSegments().get( 1 );
        }

        public static String getColReminder(Uri uri){
            return uri.getPathSegments().get( 4 );
        }
    }
}
