package com.ianmyrfield.things;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Ian on 6/28/2016.
 */
public class NoteItemAdapter extends RecyclerView.Adapter<NoteItemAdapter.NoteItemAdapterViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public NoteItemAdapter (Context context) {
        mContext = context;
    }

    @Override
    public NoteItemAdapterViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {

            View view = LayoutInflater.from( parent.getContext() )
                 .inflate( R.layout.note_item, parent, false );
            view.setFocusable( true );

            return new NoteItemAdapterViewHolder( view ) ;

        } else {
            throw new RuntimeException( "Detail Fragment: Not bound to Recycler View" );
        }
    }

    @Override
    public void onBindViewHolder (NoteItemAdapterViewHolder holder, int position) {

        if (mCursor == null) return;

        mCursor.moveToPosition( position );

        String itemContent = mCursor.getString( NoteDetailFragment.COL_ITEM_CONTENT );
        holder.mTextView.setText( itemContent );

        String reminder = mCursor.getString( NoteDetailFragment.COL_REMINDER );
        holder.mImageButton.setVisibility( reminder != null ? View.VISIBLE : View.INVISIBLE );
    }

    @Override
    public int getItemCount () {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public class NoteItemAdapterViewHolder extends RecyclerView.ViewHolder implements
                                                                           View.OnDragListener {

        public final TextView mTextView;
        public final ImageButton mImageButton;

        public NoteItemAdapterViewHolder (View view) {
            super( view );

            mTextView = (TextView) view.findViewById( R.id.list_item );
            mImageButton = (ImageButton) view.findViewById( R.id.alarm_icon );
        }

        @Override
        public boolean onDrag (View v, DragEvent event) {

            // TODO: Swipe to delete
            // after swipe, replace view with Undo View for 5 seconds?
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                default:
                    break;

            }
            return false;
        }
    }

    private void deleteItem(){

    }

    @Override
    public void onDetachedFromRecyclerView (RecyclerView recyclerView) {
        if (mCursor != null) {
            mCursor.close();
        }

        super.onDetachedFromRecyclerView( recyclerView );
    }
}
