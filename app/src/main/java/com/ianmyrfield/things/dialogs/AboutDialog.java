package com.ianmyrfield.things.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.ianmyrfield.things.R;

/**
 * Created by Ian on 6/17/2016.
 */
public class AboutDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {

        AlertDialog.Builder builder  = new AlertDialog.Builder( getActivity());
        LayoutInflater       inflater = getActivity().getLayoutInflater();

        builder.setTitle( R.string.about)
               .setMessage(R.string.about_text)
               .setPositiveButton( R.string.dialog_about_confirmation, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });

        return builder.create();
    }
}
