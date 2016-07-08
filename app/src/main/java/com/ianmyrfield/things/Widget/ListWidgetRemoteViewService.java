package com.ianmyrfield.things.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ianmyrfield.things.NoteDetailFragment;
import com.ianmyrfield.things.R;
import com.ianmyrfield.things.data.NoteContract;

/**
 * Created by Ian on 6/10/2016.
 */
public class ListWidgetRemoteViewService
        extends RemoteViewsService {

    private Context mContext;
    private int mID = -1;
    private Cursor mCursor;

    @Override
    public RemoteViewsFactory onGetViewFactory ( Intent intent ) {
        Log.d( "ListWidgetRemoteViewSer", "onGetViewFactory (line 29): " );
        return new RemoteListViewsFactory(this.getApplicationContext(), intent);
    }

    class RemoteListViewsFactory
            implements RemoteViewsService.RemoteViewsFactory {

        private int mAppWidgetId;

        public RemoteListViewsFactory ( Context context, Intent intent ) {

            mContext = context;
            mAppWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
            mID = intent.getIntExtra( "id", -1 );
            Log.d("RemoteListViewsFactory", "RemoteListViewsFactory (line 44): " + mID);
        }

        @Override public void onCreate () {

            Log.d("RemoteListViewsFactory", "onCreate (line 49): " + mID);
            if (mID == -1) return;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( mContext );
            String mSortPref = sharedPreferences.getString( getString( R.string.pref_sort_key ), getString( R.string.pref_sort_default ) );

            String sortOrder = NoteContract.NoteItems.COL_CREATED_DATE + " " + mSortPref;
            Uri    uri       = NoteContract.NoteItems.buildNoteWithTitleUri( String.valueOf( mID ) );

            mCursor = getContentResolver().query( uri,
                                                  NoteDetailFragment.NOTE_COLUMNS,
                                                  null,
                                                  null,
                                                  sortOrder  );


        }

        @Override public void onDataSetChanged () {

        }

        @Override public void onDestroy () {
            if ( mCursor != null ) {

                mCursor.close();
            }
        }

        @Override public int getCount () {
            return 0;
        }

        @Override public RemoteViews getViewAt ( int position ) {
            if (mCursor == null) return null;

            RemoteViews rv = new RemoteViews( mContext.getPackageName(), R.layout.widget_item );
            rv.setTextViewText( R.id.widget_item_textview, mCursor.getString( 3 ) );
            return rv;
        }

        @Override public RemoteViews getLoadingView () {
            return null;
        }

        @Override public int getViewTypeCount () {
            return 0;
        }

        @Override public long getItemId ( int position ) {
            return 0;
        }

        @Override public boolean hasStableIds () {
            return false;
        }
    }
}