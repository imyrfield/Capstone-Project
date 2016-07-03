package com.ianmyrfield.things;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ianmyrfield.things.data.NoteContract;

/**
 * Created by Ian on 6/16/2016.
 */
public class NoteAdapter
        extends RecyclerView.Adapter<NoteAdapter.NoteAdapterViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();
    private       Cursor                    mCursor;
    final private Context                   mContext;
    private       NoteAdapterOnClickHandler mClickHandler;
    private       boolean                   mTwoPane;
    private       boolean                   displayOptions;
    private       View                      rootView;
    private View mEmptyView;

    public interface NoteAdapterOnClickHandler {
        void onClick (NoteAdapterViewHolder vh);
    }

    public NoteAdapter (Context context, NoteAdapterOnClickHandler clickHandler, View emptyView) {
        mContext = context;
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
    }

    @Override
    public NoteAdapterViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {

        mTwoPane = mContext.getResources().getBoolean( R.bool.use_detail_activity );
        displayOptions = false;
        rootView = parent;

        if (parent instanceof RecyclerView) {

            View view = LayoutInflater.from( parent.getContext() )
                                      .inflate( R.layout.note_list_content,
                                                parent,
                                                false );
            view.setFocusable( true );

            return new NoteAdapterViewHolder( view );

        } else {
            throw new RuntimeException( "Main Activity: Not bound to Recycler View" );
        }
    }

    @Override
    public void onBindViewHolder (NoteAdapterViewHolder holder, int position) {

        if (mCursor == null) { return; }

        mCursor.moveToPosition( position );

        String title = mCursor.getString( NoteListActivity.COL_TITLE );
        holder.mTextView.setText( title );

        int color = mCursor.getInt( NoteListActivity.COL_NOTE_COLOR );
        holder.mCardView.setBackgroundColor( color );
        holder.deleteButton.setBackgroundResource( 0 );
        holder.exitButton.setBackgroundResource( 0 );
        holder.shareButton.setBackgroundResource( 0 );

    }

    @Override
    public int getItemCount () {
            return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor (Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility( getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE );
    }

    public Cursor getCursor () {
        return mCursor;
    }

    public class NoteAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public final TextView       mTextView;
        public final ImageButton    deleteButton;
        public final ImageButton    exitButton;
        public final ImageButton    shareButton;
        public final RelativeLayout buttonParent;
        public final CardView mCardView;

        public NoteAdapterViewHolder (View view) {
            super( view );

            mCardView = (CardView) view.findViewById( R.id.id );
            mTextView = (TextView) view.findViewById( R.id.card_title );
            buttonParent = (RelativeLayout) view.findViewById( R.id.button_parent );

            deleteButton = (ImageButton) view.findViewById( R.id.delete );
            deleteButton.setOnClickListener( this );

            exitButton = (ImageButton) view.findViewById( R.id.exit );
            exitButton.setOnClickListener( this );

            shareButton = (ImageButton) view.findViewById( R.id.share );
            shareButton.setOnClickListener( this );

            view.setOnClickListener( this );
            view.setOnLongClickListener( this );
        }

        @Override
        public void onClick (View v) {
            int pos = getAdapterPosition();
            mCursor.moveToPosition( pos );
            switch (v.getId()) {
                case R.id.delete:
                    Log.d( TAG, "onClick: delete button" );
                    AlertDialog.Builder builder = new AlertDialog.Builder( mContext );

                    builder.setTitle( "Delete Note?" );
                    builder.setPositiveButton( "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface d, int which) {
                            d.dismiss();
                        }
                    } );
                    builder.setNegativeButton( "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface d, int which) {
                            deleteNote();
                        }
                    } );
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                case R.id.exit:
                    showButtons();
                    return;
                case R.id.share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    shareIntent.setType("text/plain");
                    // TODO: Format Share message
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "My Note");
                    mContext.startActivity( shareIntent );
                    return;
                default:
                    if(buttonParent.getVisibility() != View.VISIBLE) {
                        if (mTwoPane) {
                            Snackbar.make( v, "2Pain! Card clicked!", Snackbar.LENGTH_LONG )
                                    .setAction( "Action", null )
                                    .show();
                        } else {

                            Bundle bundle = new Bundle( );
                            bundle.putInt( NoteDetailFragment.ARG_ITEM_ID, mCursor.getInt( NoteListActivity.COL_ID ));
                            bundle.putString( NoteDetailFragment.ARG_TITLE, mCursor.getString( NoteListActivity.COL_TITLE ) );
                            bundle.putInt( NoteDetailFragment.ARG_COLOR , mCursor.getInt( NoteListActivity.COL_NOTE_COLOR ) );

                            Intent intent = new Intent( mContext, NoteDetailActivity.class);
                            intent.putExtras( bundle );
                            mContext.startActivity( intent );
                        }
                    }
            }
        }

        @Override
        public boolean onLongClick (View v) {
            showButtons();
            return true;
        }

        private void showButtons(){

            if (displayOptions) {
                buttonParent.setVisibility( View.VISIBLE );
                displayOptions = false;
            } else {
                buttonParent.setVisibility( View.GONE );
                displayOptions = true;
            }
        }
    }

    private void deleteNote(){
        if (mCursor == null) return;
        String id = mCursor.getString( NoteListActivity.COL_ID );
        int deleted = mContext.getContentResolver().delete( NoteContract.NoteTitles
                                                               .CONTENT_URI,
                                              NoteContract.NoteTitles._ID + " = ?",
                                              new String[]{id} );
        String message;
        if (deleted > 0 ){
            message = "Note Deleted!";
        } else{
            message = "Failed to delete Note";
        }
        Snackbar.make( rootView, message,
                       Snackbar.LENGTH_LONG )
                .setAction( "Action", null )
                .show();
    }

    @Override
    public void onDetachedFromRecyclerView (RecyclerView recyclerView) {
        if (mCursor != null) {
            mCursor.close();
        }
        super.onDetachedFromRecyclerView( recyclerView );
    }
}
