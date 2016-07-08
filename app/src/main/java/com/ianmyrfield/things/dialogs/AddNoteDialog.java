package com.ianmyrfield.things.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ianmyrfield.things.R;
import com.ianmyrfield.things.data.NoteContract;
import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * Created by Ian on 6/17/2016.
 */
public class AddNoteDialog
        extends DialogFragment {

    private View          view;
    private EditText      editText;
    private Button        pickColor;
    private ContentValues cv;
    private int mColor = -1;

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {

        final AlertDialog.Builder builder  = new AlertDialog.Builder( getActivity() );
        LayoutInflater            inflater = getActivity().getLayoutInflater();
        view = inflater.inflate( R.layout.add_note_dialog, null );
        editText = (EditText) view.findViewById( R.id.edit_text );
        pickColor = (Button) view.findViewById( R.id.button_pick_color );
        cv = new ContentValues();

        builder.setTitle( R.string.add_note_dialog_title );
        builder.setView( view )
               .setPositiveButton( R.string.add_note_dialog_confirmation,
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick (DialogInterface dialog,
                                                            int which) {
                                           String title = editText.getText().toString();
                                           cv.put( NoteContract.NoteTitles.COL_TITLE,
                                                   title );

                                           if (mColor == -1) {
                                               mColor = getResources().getColor( R.color.yellow );
                                           }
                                           cv.put( NoteContract.NoteTitles.COL_COLOR,
                                                   mColor );
                                           try {
                                               Uri uri = getActivity().getContentResolver()
                                                                      .insert(
                                                                              NoteContract.NoteTitles.CONTENT_URI,
                                                                              cv );
                                           } catch (SQLException e) {
                                               e.printStackTrace();
                                               Log.d( "AddNoteDialog", "onClick (line " +
                                                       "77): " + e.getMessage() );
                                           }


                                       }
                                   } )
               .setNegativeButton( R.string.add_note_dialog_cancel,
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick (DialogInterface dialog,
                                                            int which) {
                                           AddNoteDialog.this.getDialog().cancel();
                                       }
                                   } );
        final int[] colors = getResources().getIntArray( R.array.colors );
        pickColor.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                new SpectrumDialog.Builder( getContext() ).setColors( colors )
                                                          .setDismissOnColorSelected( true )
                                                          .setSelectedColor( mColor )
                                                          .setFixedColumnCount( colors.length)
                                                          .setOnColorSelectedListener( new SpectrumDialog.OnColorSelectedListener() {
                                                              @Override
                                                              public void onColorSelected (
                                                                      boolean positiveResult,
                                                                      @ColorInt int color) {
                                                                  if (positiveResult) {
                                                                      mColor = color;
                                                                      pickColor.setBackgroundColor(
                                                                              mColor );
                                                                  }

                                                              }
                                                          } )
                                                          .build()
                                                          .show( getFragmentManager(),
                                                                 "pick_color" );
            }
        } );

        // Disables Ok button until a title has been entered
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {

            @Override
            public void onShow (DialogInterface dialog) {
                ( (AlertDialog) dialog ).getButton( AlertDialog.BUTTON_POSITIVE )
                                        .setEnabled( false );
            }
        } );

        editText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count,
                                           int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged (Editable s) {
                if (dialog.getButton( AlertDialog.BUTTON_POSITIVE ) == null) {
                    return;
                }
                // Check if edittext is empty
                if (TextUtils.isEmpty( s )) {
                    // Disable ok button
                    dialog.getButton( AlertDialog.BUTTON_POSITIVE )
                          .setEnabled( false );
                } else {
                    // Something into edit text. Enable the button.
                    dialog.getButton( AlertDialog.BUTTON_POSITIVE )
                          .setEnabled( true );
                }
            }

        } );

        return dialog;

    }
}

