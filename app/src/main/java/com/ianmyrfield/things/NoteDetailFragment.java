package com.ianmyrfield.things;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ianmyrfield.things.data.NoteContract;
import com.ianmyrfield.things.dialogs.SettingsDialog;
import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * A fragment representing a single Note detail screen.
 * This fragment is either contained in a {@link NoteListActivity}
 * in two-pane mode (on tablets) or a {@link NoteDetailActivity}
 * on handsets.
 */
public class NoteDetailFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
                   SharedPreferences.OnSharedPreferenceChangeListener{
    
    private static final String TAG = "NoteDetailFragment";
    
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_TITLE   = "title";
    public static final String ARG_COLOR   = "color";
    private String mTitle;
    private String newTitle;
    private int    mColor;
    private int    newColor;
    
    private RecyclerView         mRecyclerView;
    private NoteItemAdapter      mAdapter;
    private EditText             mEditTitle;
    private EditText             mAddItem;
    private Toolbar              mToolbar;
    private TextInputLayout      itemInputLayout;
    private FloatingActionButton mFab;
    
    Context mContext;
    
    public static final String[] NOTE_COLUMNS = { NoteContract.NoteTitles.TABLE_NAME + "." + NoteContract.NoteTitles._ID,
                                                  NoteContract.NoteTitles.COL_TITLE,
                                                  NoteContract.NoteItems.TABLE_NAME + "." + NoteContract.NoteItems._ID,
                                                  NoteContract.NoteItems.COL_ITEM_CONTENT,
                                                  NoteContract.NoteItems.COL_CREATED_DATE,
                                                  NoteContract.NoteItems.COL_REMINDER };
    
    static final int COL_ID           = 0;
    static final int COL_TITLE        = 1;
    static final int COL_ITEM_ID      = 2;
    static final int COL_ITEM_CONTENT = 3;
    static final int COL_CREATED_DATE = 4;
    static final int COL_REMINDER     = 5;
    
    public static final int NOTE_ITEM_LOADER = 1;
    private String ID;
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment () {
    }
    
    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        
        mContext = getContext();
        setHasOptionsMenu( true );
        ID = String.valueOf( getArguments().getInt( ARG_ITEM_ID ) );
        mTitle = getArguments().getString( ARG_TITLE );
        mColor = getArguments().getInt( ARG_COLOR );
        
        getLoaderManager().initLoader( NOTE_ITEM_LOADER, null, this );
    }
    
    @Override
    public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater ) {
        getActivity().getMenuInflater().inflate( R.menu.detail, menu );
        super.onCreateOptionsMenu( menu, inflater );
    }
    
    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {

        //TODO: import drawable resources for edit and cancel buttons
        switch ( item.getItemId() ) {
            case R.id.edit_title:
                if ( mEditTitle.getVisibility() == View.GONE ) {
                    //item.setIcon(  );
                }
                else {
                    //item.setIcon(  );
                }
                editTitle();
                return true;
            case R.id.change_color:
                
                // resets color
                newColor = 0;
                
                new SpectrumDialog.Builder( getContext() ).setColors( getResources().getIntArray(
                        R.array.colors ) )
                                                          .setDismissOnColorSelected( true )
                                                          .setSelectedColor( mColor )
                                                          .setOnColorSelectedListener( new SpectrumDialog.OnColorSelectedListener() {
                                                              @Override
                                                              public void onColorSelected (
                                                                                                  boolean positiveResult,
                                                                                                  @ColorInt int color ) {
                                                                  if ( positiveResult ) {
                                                                      newColor = color;
                                                                      saveChanges();
                                                                  }
                                                              }
                                                          } )
                                                          .build()
                                                          .show( getFragmentManager(),
                                                                 "pick_color" );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
    
    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState ) {
        
        final View root = inflater.inflate( R.layout.note_detail, container, false );
        mAddItem = (EditText) root.findViewById( R.id.add_item );
        itemInputLayout = (TextInputLayout) root.findViewById( R.id.input_layout );

        // Setup FAB
        mFab = (FloatingActionButton) getActivity().findViewById( R.id.fab );
        if ( mFab != null ) {
            mFab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick ( View view ) {
                    showNewItemPrompt();
                }
            } );
        }

        mRecyclerView = (RecyclerView) root.findViewById( R.id.note_detail );
        if ( mRecyclerView != null ) { setupRecyclerView( mRecyclerView ); }
        
        mToolbar = (Toolbar) getActivity().findViewById( R.id.detail_toolbar );
        mEditTitle = (EditText) getActivity().findViewById( R.id.edit_title );
        mEditTitle.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction ( TextView v, int actionId, KeyEvent event ) {
                boolean handled = false;
                if ( actionId == EditorInfo.IME_ACTION_DONE
                             || event.getAction() == KeyEvent.KEYCODE_ENTER ) {
                    newTitle = mEditTitle.getText().toString();
                    Log.d( TAG, "onEditorAction: newTitle: " + newTitle );
                    saveChanges();
                    editTitle();
                    handled = true;
                }
                return handled;
            }
        } );
        // TODO: touching outside keyboard cancels edit, and closes keyboard.
        //        mEditTitle.setOnFocusChangeListener( new View.OnFocusChangeListener() {
        //            @Override
        //            public void onFocusChange ( View v, boolean hasFocus ) {
        //
        //                if ( !hasFocus ) {
        //
        //                }
        //            }
        //        } );

        setupToolBar();

        mAddItem.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction ( TextView v, int actionId, KeyEvent event ) {
                boolean handled = false;

                 if (actionId == EditorInfo.IME_NULL || event.getAction() == KeyEvent
                                                                                     .KEYCODE_ENTER){
                    saveNewItem();
                    handled = true;
                }
                return handled;
            }
        } );
        return root;
    }
    
    private void setupRecyclerView ( RecyclerView recyclerView ) {
        mAdapter = new NoteItemAdapter( mContext );
        mRecyclerView.setAdapter( mAdapter );
    }
    
    @Override
    public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {

        if ( id == 1 ) {
            String sortOrder = NoteContract.NoteItems.COL_CREATED_DATE + " DESC";
            Uri    uri       = NoteContract.NoteItems.buildNoteWithTitleUri( ID );
            Log.d("NoteDetailFragment", "onCreateLoader (line 232): " + uri.toString());
            // TODO: Check if that's the right setup for returning list items
            return new CursorLoader( mContext, uri, NOTE_COLUMNS, null, null, sortOrder );
        }
        else {
            return null;
        }
    }
    
    @Override
    public void onLoadFinished ( Loader<Cursor> loader, Cursor data ) {
        Log.d("NoteDetailFragment", "onLoadFinished (line 245): " + data.getCount());
        mAdapter.swapCursor( data );
    }
    
    @Override
    public void onLoaderReset ( Loader<Cursor> loader ) {
        mAdapter.swapCursor( null );
    }

    @Override
    public void onSharedPreferenceChanged ( SharedPreferences sharedPreferences,
                                            String key ) {
        // TODO: onSharedPreferenceChanged
        // move key vaues to Settings Activity as public static final Strings?
        // will that work for preferences.xml
        
        switch ( key ) {
            case SettingsDialog.sort:
                break;
            default:
                break;
        }
    }
    
    private void setupToolBar () {
        
        if ( mToolbar != null ) {
            
            mToolbar.setTitle( mTitle );
            mToolbar.setBackgroundColor( mColor );
        }
    }
    
    private void editTitle () {
        
        // TODO: How to make it loose focus when clicking elsewhere on screen or add enter button to keyboard
        if ( mEditTitle.getVisibility() == View.GONE ) {
            mEditTitle.setText( mTitle );
            mEditTitle.setVisibility( View.VISIBLE );
            mToolbar.setTitle( "" );
        }
        else {
            mEditTitle.setVisibility( View.GONE );
            mToolbar.setTitle( mTitle );
            Utility.hideKeyboardFrom( mContext, mEditTitle );
        }
    }

    /**
     * Saves changes to the title, and/or color, of the list.
     */
    private void saveChanges () {
        
        // Protects against NPE
        if ( newTitle == null ) {newTitle = mTitle;}
        
        // No change, therefore no need to save.
        if ( newTitle.equals( mTitle ) && newColor == mColor ) {return;}
        
        ContentValues cv = new ContentValues();
        
        mTitle = newTitle;
        if ( newColor != 0 ) { mColor = newColor; }
        
        cv.put( NoteContract.NoteTitles.COL_TITLE, mTitle );
        cv.put( NoteContract.NoteTitles.COL_COLOR, mColor );
        
        int updated = getActivity().getContentResolver()
                                   .update( NoteContract.NoteTitles.CONTENT_URI,
                                            cv,
                                            NoteContract.NoteTitles._ID + " = ?",
                                            new String[] { ID } );
        setupToolBar();
    }

    /**
     * Toggles visibility of FAB and AddItem EditTextView
     */
    private void showNewItemPrompt () {

        if ( itemInputLayout.getVisibility() == View.GONE ) {
            itemInputLayout.setVisibility( View.VISIBLE );
            mFab.setVisibility( View.GONE );
        }
        else {
            itemInputLayout.setVisibility( View.GONE );
            mFab.setVisibility( View.VISIBLE );
            Utility.hideKeyboardFrom( mContext, mAddItem );
        }
    }

    /**
     * Saves new item to the list
     */
    private void saveNewItem () {

        String        s             = mAddItem.getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put( NoteContract.NoteItems.COL_ITEM_CONTENT, s );
        contentValues.put( NoteContract.NoteItems.COL_TITLE_KEY, ID );

        try {
            Uri uri = getActivity().getContentResolver()
                                   .insert( NoteContract.NoteItems.CONTENT_URI,
                                            contentValues );
            Log.d( "NoteDetailFragment", "saveNewItem (line 319):  successful: " + s );
        } catch ( SQLException e ) {
            e.printStackTrace();
            Log.d( TAG, "saveNewItem: " + e.getMessage() );
        }

        showNewItemPrompt();
    }
}
