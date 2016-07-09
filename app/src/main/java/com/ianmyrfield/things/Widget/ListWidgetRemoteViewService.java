package com.ianmyrfield.things.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.AdapterView;
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
    private static final String TAG = "RemoteViewService";

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



        }

        @Override public void onDataSetChanged () {
            Log.d("RemoteListViewsFactory", "onDataSetChanged (line 55): " + mID);
            if (mID == -1) return;
            if (mCursor != null) mCursor.close();

            // This method is called by the app hosting the widget (e.g., the launcher)
            // However, our ContentProvider is not exported so it doesn't have access to the
            // data. Therefore we need to clear (and finally restore) the calling identity so
            // that calls use our process and permission
            final long identityToken = Binder.clearCallingIdentity();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( mContext );
            String mSortPref = sharedPreferences.getString( getString( R.string.pref_sort_key ), getString( R.string.pref_sort_default ) );

            String sortOrder = NoteContract.NoteItems.COL_CREATED_DATE + " " + mSortPref;
            Uri    uri       = NoteContract.NoteItems.buildNoteWithTitleUri( String.valueOf( mID ) );

            mCursor = getContentResolver().query( uri,
                                                  NoteDetailFragment.NOTE_COLUMNS,
                                                  null,
                                                  null,
                                                  sortOrder  );

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override public void onDestroy () {
            if ( mCursor != null ) {

                mCursor.close();
                mCursor = null;
            }
        }

        @Override public int getCount () {
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override public RemoteViews getViewAt ( int position ) {
            if (position == AdapterView.INVALID_POSITION ||
                        mCursor == null || !mCursor.moveToPosition(position)){
                Log.d("RemoteListViewsFactory", "getViewAt (line 84): ");
                return null;
            }

            mCursor.moveToPosition( position );
            RemoteViews rv = new RemoteViews( mContext.getPackageName(), R.layout.widget_item );
            Log.d( TAG, "getViewAt: "+ mCursor.getCount() );
            rv.setTextViewText( R.id.widget_item_textview, mCursor.getString( 3 ) );

            Log.d("RemoteListViewsFactory", "getViewAt (line 87): " + mCursor.getString( 3 ));
            return rv;
        }

        @Override public RemoteViews getLoadingView () {
            return null;
        }

        @Override public int getViewTypeCount () {
            return 1;
        }

        @Override public long getItemId ( int position ) {
            if (mCursor.moveToPosition( position )){
                return mCursor.getLong( 2 );
            }
            return position;
        }

        @Override public boolean hasStableIds () {
            return true;
        }
    }
}