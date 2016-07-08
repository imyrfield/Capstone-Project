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
import android.util.Log;
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
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
                   NoteAdapter.NoteAdapterOnClickHandler {

    public static final  String TAG             = NoteListActivity.class.getSimpleName();
    private static final String DIALOG_ABOUT    = "about";
    private static final String DIALOG_SETTINGS = "add";
    private NoteAdapter  mAdapter;
    private RecyclerView mRecyclerView;
    private View         mEmptyView;

    public static final String[] NOTE_COLUMNS = {
            NoteContract.NoteTitles._ID,
            NoteContract.NoteTitles.COL_TITLE,
            NoteContract.NoteTitles.COL_COLOR,
            };

    static final        int COL_ID         = 0;
    static final        int COL_TITLE      = 1;
    static final        int COL_NOTE_COLOR = 2;
    public static final int NOTE_LOADER    = 0;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_list );
        mTwoPane = getResources().getBoolean( R.bool.use_detail_activity );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        if ( toolbar != null ) toolbar.setTitle( getTitle() );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );

        if ( fab != null && !mTwoPane) {

            fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick ( View view ) {
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

        getLoaderManager().initLoader( NOTE_LOADER, null, this );
    }

    @Override public boolean onPrepareOptionsMenu ( Menu menu ) {
        if ( mTwoPane ) {
            menu.findItem( R.id.add_note ).setEnabled( true );
        }
        return super.onPrepareOptionsMenu( menu );
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {

        switch ( item.getItemId() ) {

            case R.id.about:

                AboutDialog dialog_Fragment = new AboutDialog();
                dialog_Fragment.show( getFragmentManager(), "dialog" );
                return true;

            case R.id.settings:
                Intent intent = new Intent( this, SettingsActivity.class );
                startActivity( intent );
                return true;

            case R.id.add_note:

                AddNoteDialog dialog = new AddNoteDialog();
                dialog.show( getSupportFragmentManager(), "dialog" );
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public void onBackPressed () {
        moveTaskToBack( true );
    }

    private void setupRecyclerView ( @NonNull RecyclerView recyclerView, @NonNull View
                                                                                 emptyView ) {

        mAdapter = new NoteAdapter( this, new NoteAdapter.NoteAdapterOnClickHandler() {
            @Override public void onClick ( Bundle bundle ) {
            }
        }, emptyView );

        recyclerView.setAdapter( mAdapter );
    }

    @Override
    public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {

        if ( id == 0 ) {
            return new CursorLoader( this,
                                     NoteContract.NoteTitles.CONTENT_URI,
                                     NOTE_COLUMNS,
                                     null,
                                     null,
                                     null );
        }
        else {
            return null;
        }
    }

    @Override
    public void onLoaderReset ( Loader<Cursor> loader ) {
        mAdapter.swapCursor( null );
    }

    @Override
    public void onLoadFinished ( Loader<Cursor> loader, final Cursor data ) {

        mAdapter.swapCursor( data );
    }
    
    @Override public void onClick ( Bundle bundle ) {
        String tag = bundle.getString( NoteDetailFragment.ARG_TITLE );

        Log.d( "NoteListActivity", "onClick (line 186): " + tag );

        if ( getSupportFragmentManager().findFragmentByTag( tag ) == null ) {
            NoteDetailFragment fragment = new NoteDetailFragment();
            fragment.setArguments( bundle );
            getSupportFragmentManager().beginTransaction()
                                       .replace( R.id.note_detail_container, fragment, tag )
                                       .commit();
        }
//        else {
//            getSupportFragmentManager().beginTransaction().replace( R.id
//                                                                            .note_detail_container, new NoteDetailFragment() , tag);
//        }
    }
}
