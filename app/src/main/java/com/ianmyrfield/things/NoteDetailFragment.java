package com.ianmyrfield.things;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ianmyrfield.things.data.NoteContract;

/**
 * A fragment representing a single Note detail screen.
 * This fragment is either contained in a {@link NoteListActivity}
 * in two-pane mode (on tablets) or a {@link NoteDetailActivity}
 * on handsets.
 */
public class NoteDetailFragment
        extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
private String title;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment () {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (getArguments().containsKey( ARG_ITEM_ID )) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            Cursor c = getActivity().getContentResolver().query( NoteContract.NoteTitles.CONTENT_URI,
                                                                 null, null,
                                                                 null, null );
            c.moveToPosition( getArguments().getInt( ARG_ITEM_ID ));
            title = c.getString( 1 );
            //mItem = DummyContent.ITEM_MAP.get( getArguments().getString( ARG_ITEM_ID
            // ) );

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(
                    R.id.toolbar_layout );
            if (appBarLayout != null) {
                appBarLayout.setTitle( title );
            }
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.note_detail, container, false );

        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ( (TextView) rootView.findViewById( R.id.note_detail ) ).setText( mItem.details );
//        }

        return rootView;
    }
}
