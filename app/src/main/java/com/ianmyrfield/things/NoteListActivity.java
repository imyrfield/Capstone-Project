package com.ianmyrfield.things;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ianmyrfield.things.data.NoteContract;
import com.ianmyrfield.things.dialogs.AboutDialog;
import com.ianmyrfield.things.dialogs.AddNoteDialog;
import com.ianmyrfield.things.dialogs.SettingsActivity;

/**
 * An activity representing a list of Notes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NoteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class NoteListActivity
        extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String  TAG             = NoteListActivity.class.getSimpleName();
    private static final String DIALOG_ABOUT    = "about";
    private static final String DIALOG_SETTINGS = "add";
    private NoteAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    public static final String[] NOTE_COLUMNS = {
            NoteContract.NoteTitles._ID,
            NoteContract.NoteTitles.COL_TITLE,
            NoteContract.NoteTitles.COL_COLOR,
    };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_NOTE_COLOR = 2;
    public static final int NOTE_LOADER = 0;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_list );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        if (toolbar != null) toolbar.setTitle( getTitle() );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );

        // TODO: 7/3/2016 if (mTwoPane) { // Add + icon to toolbar for NoteList. } else { // Use FAB in NoteList }

        if (fab != null) {

            fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    // For testing Firebase Crash reporting
                    // throw new RuntimeException( "Boom" );
                    AddNoteDialog dialog = new AddNoteDialog();
                    dialog.show( getSupportFragmentManager(), "dialog" );
                }
            } );
        }

        mRecyclerView = (RecyclerView) findViewById( R.id.note_list );
        mEmptyView = findViewById( R.id.empty_view );
        assert mRecyclerView != null;
        setupRecyclerView( mRecyclerView, mEmptyView );

        mTwoPane = getResources().getBoolean( R.bool.use_detail_activity );

        getLoaderManager().initLoader( NOTE_LOADER, null, this );
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        String dialog;
        switch (item.getItemId()){

            case R.id.about:
                dialog = DIALOG_ABOUT;
                createDialog( dialog, null );
                return true;

            case R.id.settings:
                Intent intent = new Intent( this, SettingsActivity.class );
                startActivity( intent );
//                dialog = DIALOG_SETTINGS;
//                createDialog( dialog, null );
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public void onBackPressed () {
        moveTaskToBack( true );
    }

    private void setupRecyclerView (@NonNull RecyclerView recyclerView, @NonNull View
            emptyView) {

        mAdapter = new NoteAdapter( this, new NoteAdapter.NoteAdapterOnClickHandler() {
            @Override
            public void onClick (NoteAdapter.NoteAdapterViewHolder vh) {
            }
        }, emptyView );

        recyclerView.setAdapter( mAdapter );
    }

    @Override
    public Loader<Cursor> onCreateLoader (int id, Bundle args) {

        if (id == 0) {
            return new CursorLoader( this,
                                     NoteContract.NoteTitles.CONTENT_URI,
                                     NOTE_COLUMNS,
                                     null,
                                     null,
                                     null );
        } else {
            return null;
        }
    }

    @Override
    public void onLoaderReset (Loader<Cursor> loader) {
        mAdapter.swapCursor( null );
    }

    @Override
    public void onLoadFinished (Loader<Cursor> loader, final Cursor data) {

        mAdapter.swapCursor( data );
    }

    public void createDialog (String dialog, Bundle args) {

        switch (dialog) {

            case ( DIALOG_ABOUT ):

                AboutDialog dialog_Fragment = new AboutDialog();
                dialog_Fragment.show( getFragmentManager(), "dialog" );
                break;
        }
    }
}
