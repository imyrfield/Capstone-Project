package com.ianmyrfield.things;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Ian on 6/16/2016.
 */
public class ThingsApplication extends Application {

    private static final String TAG = "ThingsApplication";

    @Override
    public void onCreate () {
        super.onCreate();

        // TODO: Remove for Release!
        Stetho.InitializerBuilder builder = Stetho.newInitializerBuilder( this );
        builder.enableWebKitInspector( Stetho.defaultInspectorModulesProvider( this ) );
        Stetho.Initializer init = builder.build();
        Stetho.initialize( init );

        //Cause issues with Firebase Crash reporting
        //LeakCanary.install( this );
        //CustomActivityOnCrash.setShowErrorDetails( false );

        //CustomActivityOnCrash.install( this );
    }
}
