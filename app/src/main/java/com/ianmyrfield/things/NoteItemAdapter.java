package com.ianmyrfield.things;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.ianmyrfield.things.data.NoteContract;

/**
 * Created by Ian on 6/28/2016.
 */
public class NoteItemAdapter
        extends RecyclerView.Adapter<NoteItemAdapter.NoteItemAdapterViewHolder> {

    private static final String TAG                 = "NoteItemAdapter";
    public static final  String ACTION_DATA_UPDATED = "com.ianmyrfield.things.app.ACTION_DATA_UPDATED";
    private       Cursor  mCursor;
    private       Context mContext;
    private final View    mEmptyView;
    private       View    rootView;

    public NoteItemAdapter ( Context context, View emptyView ) {
        mContext = context;
        mEmptyView = emptyView;
    }

    @Override
    public NoteItemAdapterViewHolder onCreateViewHolder ( ViewGroup parent,
                                                          int viewType ) {

        if ( parent instanceof RecyclerView ) {

            rootView = parent;
            View view = LayoutInflater.from( parent.getContext() )
                                      .inflate( R.layout.note_item, parent, false );
            view.setFocusable( true );

            return new NoteItemAdapterViewHolder( view );

        }
        else {
            throw new RuntimeException( "Detail Fragment: Not bound to Recycler View" );
        }
    }

    @Override
    public void onBindViewHolder ( NoteItemAdapterViewHolder holder, int position ) {

        if ( mCursor == null ) return;

        mCursor.moveToPosition( position );

        String itemContent = mCursor.getString( NoteDetailFragment.COL_ITEM_CONTENT );
        holder.mTextView.setText( itemContent );

        String reminder = mCursor.getString( NoteDetailFragment.COL_REMINDER );
        holder.mImageButton.setVisibility( reminder != null
                                           ? View.VISIBLE
                                           : View.INVISIBLE );

        holder.mSwipeLayout.setShowMode( SwipeLayout.ShowMode.LayDown );
        holder.mSwipeLayout.addSwipeListener( new SwipeLayout.SwipeListener() {
            @Override public void onStartOpen ( SwipeLayout layout ) {

            }

            @Override public void onOpen ( SwipeLayout layout ) {

            }

            @Override public void onStartClose ( SwipeLayout layout ) {

            }

            @Override public void onClose ( SwipeLayout layout ) {

            }

            @Override
            public void onUpdate ( SwipeLayout layout, int leftOffset, int topOffset ) {

            }

            @Override
            public void onHandRelease ( SwipeLayout layout, float xvel, float yvel ) {

            }
        } );
    }

    @Override
    public int getItemCount () {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor ( Cursor newCursor ) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility( getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE );
    }

    public Cursor getCursor () {
        return mCursor;
    }

    public class NoteItemAdapterViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView    mTextView;
        public final ImageButton mImageButton;
        private      SwipeLayout mSwipeLayout;
        private      ImageButton mDelete;

        public NoteItemAdapterViewHolder ( View view ) {
            super( view );

            mSwipeLayout = (SwipeLayout) view.findViewById( R.id.swipe );
            mTextView = (TextView) view.findViewById( R.id.list_item );
            mImageButton = (ImageButton) view.findViewById( R.id.alarm_icon );
            mDelete = (ImageButton) view.findViewById( R.id.delete );
            mDelete.setOnClickListener( this );
        }

        @Override public void onClick ( View v ) {
            int pos = getAdapterPosition();
            mCursor.moveToPosition( pos );
            deleteItem();
        }
    }

    private void deleteItem () {
        if ( mCursor == null ) return;

        String id = mCursor.getString( NoteDetailFragment.COL_ITEM_ID );
        int deleted = mContext.getContentResolver()
                              .delete( NoteContract.NoteItems.CONTENT_URI,
                                       NoteContract.NoteItems._ID + "" + " = ?",
                                       new String[] { id } );
        String message;
        if ( deleted > 0 ) {
            message = mCursor.getString( NoteDetailFragment.COL_ITEM_CONTENT ) + " Deleted!";
            updateWidgets();
        }
        else {
            message = "Failed to delete item";
        }
        Snackbar.make( rootView, message,
                       Snackbar.LENGTH_LONG )
                .setAction( "Action", null )
                .show();
    }

    @Override
    public void onDetachedFromRecyclerView ( RecyclerView recyclerView ) {
        if ( mCursor != null ) {
            mCursor.close();
        }

        super.onDetachedFromRecyclerView( recyclerView );
    }

    private void updateWidgets () {
        Context context = mContext;
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent( ACTION_DATA_UPDATED ).setPackage( context.getPackageName() );
        context.sendBroadcast( dataUpdatedIntent );
    }
}
