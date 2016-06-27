package com.ianmyrfield.things.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ianmyrfield.things.NoteListActivity;
import com.ianmyrfield.things.R;


/**
 * Created by Ian on 6/17/2016.
 */
public class SettingsDialog extends AppCompatActivity {

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.settings_activity );

        Toolbar toolbar = (Toolbar) findViewById( R.id.settings_toolbar );
        setSupportActionBar( toolbar );

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                                       .add( R.id.settings_container,
                                             new SettingsFragment() )
                                       .commit();
        }
        // TODO: Tablet Layout - Cardview
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo( new Intent( this, NoteListActivity.class ) );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
        }

        @Override
        public void onCreatePreferences (Bundle bundle, String s) {
            addPreferencesFromResource( R.xml.preferences );
        }
    }
}
