package com.ianmyrfield.things.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.ianmyrfield.things.NoteDetailFragment;
import com.ianmyrfield.things.NoteListActivity;
import com.ianmyrfield.things.R;

/**
 * Created by Ian on 6/10/2016.
 */
public class ListWidgetProvider
        extends AppWidgetProvider {

    private String mTitle;
    private int mID = -1;
    private int mColor;

    /**
     * Called every time widget needs to be updated
     * Any process that will last more then a few seconds should utilize a service.
     *
     * @param c
     * @param manager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate ( Context c, AppWidgetManager manager,
                           int[] appWidgetIds ) {
        // Configuration Activity sets up initial configuration

        //Loops through each App widget that belongs to this provider
        for ( int appWidgetId : appWidgetIds ) {
            updateAppWidget( c, appWidgetId, manager );
        }
        super.onUpdate( c, manager, appWidgetIds );

    }

    /**
     * Called when the widget is resized
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged ( Context context,
                                            AppWidgetManager appWidgetManager,
                                            int appWidgetId, Bundle newOptions ) {
        mID = newOptions.getInt( "id" );
        mTitle = newOptions.getString( "title" );
        mColor = newOptions.getInt( "color" );

        //        RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.note_widget );
        updateAppWidget( context, appWidgetId, appWidgetManager );

        super.onAppWidgetOptionsChanged( context, appWidgetManager, appWidgetId, newOptions );
    }

    private void updateAppWidget ( Context c, int appWidgetId,
                                   AppWidgetManager manager ) {
        // Setup Intent
        Intent intent = new Intent( c, ListWidgetRemoteViewService.class );
        intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
        intent.putExtra( "id", mID );
        intent.setData( Uri.parse( intent.toUri( Intent.URI_INTENT_SCHEME ) ) );

        // Attach adapter
        RemoteViews remoteViews = new RemoteViews( c.getPackageName(), R.layout.note_widget );
        remoteViews.setInt(R.id.widget, "setBackgroundColor", mColor );
        remoteViews.setInt(R.id.widget_title, "setBackgroundColor", mColor );
        remoteViews.setRemoteAdapter( R.id.widget_list, intent );
        if ( mTitle != null ) remoteViews.setTextViewText( R.id.widget_title, mTitle );
        remoteViews.setEmptyView( R.id.widget_list, R.id.empty_view );

        // Intent to launch Main Activity
        Intent        intentActivity = new Intent( c, NoteListActivity.class );
        PendingIntent pendingIntent  = PendingIntent.getActivity( c, 0, intentActivity, 0 );
        remoteViews.setOnClickPendingIntent( R.id.widget, pendingIntent );

        // Tell AppWidgetManager to perform an update
        manager.updateAppWidget( appWidgetId, remoteViews );
    }

    @Override
    public void onReceive ( @NonNull Context context, @NonNull Intent intent ) {
        super.onReceive(context, intent);
        if ( NoteDetailFragment.ACTION_DATA_UPDATED.equals( intent.getAction() )) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName( context, getClass()) );
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }
}
