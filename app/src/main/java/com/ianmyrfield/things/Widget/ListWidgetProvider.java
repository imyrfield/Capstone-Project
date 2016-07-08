package com.ianmyrfield.things.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.ianmyrfield.things.R;

/**
 * Created by Ian on 6/10/2016.
 */
public class ListWidgetProvider
        extends AppWidgetProvider {

    private String mTitle;
    private int mID = -1;

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
        final int N = appWidgetIds.length;

        //Loops through each App widget that belongs to this provider
        for ( int i = 0; i < N; i++ ) {
            int appWidgetId = appWidgetIds[ i ];
            updateAppWidget( c, appWidgetId, manager );
        }
        super.onUpdate(c, manager, appWidgetIds);

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

//        RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.note_widget );
        updateAppWidget( context, appWidgetId, appWidgetManager);

        super.onAppWidgetOptionsChanged( context, appWidgetManager, appWidgetId, newOptions );
    }

    private void updateAppWidget(Context c, int appWidgetId, AppWidgetManager manager){
        // Setup Intent
        Intent        intent        = new Intent( c, ListWidgetRemoteViewService.class );
        intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
        intent.putExtra( "id", mID );
        intent.setData( Uri.parse( intent.toUri( Intent.URI_INTENT_SCHEME ) ) );

        // Attach adapter
        RemoteViews remoteViews = new RemoteViews( c.getPackageName(), R.layout.note_widget );
        remoteViews.setRemoteAdapter( R.id.widget_list, intent );
        if (mTitle != null ) remoteViews.setTextViewText( R.id.widget_title, mTitle );
        remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view );

        // Tell AppWidgetManager to perform an update
        manager.updateAppWidget( appWidgetId, remoteViews );
    }
}
