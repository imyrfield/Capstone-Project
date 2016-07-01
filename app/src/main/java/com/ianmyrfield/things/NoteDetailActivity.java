package com.ianmyrfield.things;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * An activity representing a single Note detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link NoteListActivity}.
 */
public class NoteDetailActivity
        extends AppCompatActivity {

    private Bundle arguments;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_detail );
        Toolbar toolbar = (Toolbar) findViewById( R.id.detail_toolbar );
        if (toolbar != null) toolbar.setTitle( "" );
        setSupportActionBar( toolbar );

//         Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            arguments = getIntent().getExtras();

            NoteDetailFragment fragment = new NoteDetailFragment();
            fragment.setArguments( arguments );
            getSupportFragmentManager().beginTransaction()
                                       .add( R.id.note_detail_container, fragment )
                                       .commit();
        }
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
}
