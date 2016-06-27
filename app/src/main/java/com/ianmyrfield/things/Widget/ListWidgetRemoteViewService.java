package com.ianmyrfield.things.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Ian on 6/10/2016.
 */
public class ListWidgetRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory (Intent intent) {
        return null;
    }
}
