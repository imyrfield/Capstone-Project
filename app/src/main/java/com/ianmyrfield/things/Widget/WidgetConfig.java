package com.ianmyrfield.things.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ianmyrfield.things.NoteListActivity;
import com.ianmyrfield.things.R;
import com.ianmyrfield.things.data.NoteContract;

/**
 * Created by Ian on 7/6/2016.
 */
public class WidgetConfig extends AppCompatActivity {

    private int     mAppWidgetId;
    private Cursor  mCursor;
    private Context mContext;
    private int mID = -1;
    private String mTitle;
    private int mColor;

    @Override protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Protects user from backing out and setting an unconfigured widget
        setResult( RESULT_CANCELED, null );

        setContentView( R.layout.note_widget );
        mContext = this;
        ListView listView = (ListView) findViewById( R.id.widget_list );

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID );


        mCursor = getContentResolver().query( NoteContract.NoteTitles.CONTENT_URI,
                                              NoteListActivity.NOTE_COLUMNS,
                                              null,
                                              null,
                                              null  );

        if (mCursor != null) {

            listView.setAdapter( new CursorAdapter( mContext, mCursor, false ) {

                @Override public long getItemId ( int position ) {
                    return super.getItemId( position );
                }

                @Override
                public View newView ( Context context, Cursor cursor, ViewGroup parent ) {
                    return LayoutInflater.from( mContext ).inflate( R.layout.widget_item,
                                                                    parent, false );
                }

                @Override
                public void bindView ( View view, Context context, Cursor cursor ) {
                    TextView item = (TextView) view.findViewById( R.id.widget_item_textview );
                    item.setGravity( Gravity.CENTER );
                    item.setTextSize( 20 );
                    item.setText( mCursor.getString( 1 ) );
                    item.setTag( mCursor.getPosition() );
                    item.setOnClickListener( new View.OnClickListener() {
                        @Override public void onClick ( View v ) {
                            mCursor.moveToPosition( (int) v.getTag() );
                            mID = mCursor.getInt( 0 );
                            mTitle = mCursor.getString( 1 );
                            mColor = mCursor.getInt( 2 );

                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( mContext );

                            Bundle bundle = new Bundle();
                            bundle.putInt( "id", mID );
                            bundle.putInt( "color", mColor );
                            if ( mTitle != null ) {
                                bundle.putString( "title", mTitle );
                            }

                            appWidgetManager.updateAppWidgetOptions( mAppWidgetId, bundle );

                            // Finish Activity
                            Intent resultValue = new Intent();
                            resultValue.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId );
                            setResult( RESULT_OK, resultValue );
                            finish();
                        }
                    } );
                }
            } );

        }
    }
}
