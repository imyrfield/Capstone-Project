package com.ianmyrfield.things;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignIn
        extends AppCompatActivity {

    public static final  String  PREF_IS_FIRST_LOAD     = "isFirstLoad";
    private static final int     RC_SIGN_IN             = 100;

    private TextView welcome;
    private Button   skip;
    private Button   signIn;

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        welcome = (TextView) findViewById( R.id.welcome );

        FirebaseAuth      auth        = FirebaseAuth.getInstance();
        SharedPreferences sp          = getPreferences( MODE_PRIVATE );
        boolean           isFirstLoad = sp.getBoolean( PREF_IS_FIRST_LOAD, true );

        if (!isFirstLoad) {
            // User already auth'd - Show welcome message
            if (auth.getCurrentUser() != null) {
                //TODO: setup transition, not being displayed since activity starts right away
                welcome.setVisibility( View.VISIBLE );
                Log.d( "SignIn", "onCreate (line 47): " );
            }
            // User has not signed in, but has already seen sign in screen
            startMainActivity();
        }
        signIn = (Button) findViewById( R.id.sign_in );
        signIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivityForResult( AuthUI.getInstance()
                                              .createSignInIntentBuilder()
                                              .setProviders( AuthUI.EMAIL_PROVIDER,
                                                             AuthUI.GOOGLE_PROVIDER )
                                              .setTheme( R.style.LoginTheme )
                                              .build(), RC_SIGN_IN );
            }
        } );


        // Starts MainActivity, and hide's sign in on future launches
        skip = (Button) findViewById( R.id.skip_button );
        skip.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                hideSignIn();
                startMainActivity();
            }
        } );
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                hideSignIn();
                startMainActivity();
            } else {
                signInFailed();
            }
        }
    }

    private void startMainActivity () {
        Intent intent = new Intent( this, NoteListActivity.class );
        startActivity( intent );
    }

    private void signInFailed () {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.error_sign_in_failed );
        builder.setPositiveButton( R.string.button_ok_text,
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick (DialogInterface dialog,
                                                            int which) {
                                           startMainActivity();
                                       }
                                   } )
               .setOnDismissListener( new DialogInterface.OnDismissListener() {
                   @Override
                   public void onDismiss (DialogInterface dialog) {
                       startMainActivity();
                   }
               } );
        builder.show();
    }

    private void hideSignIn () {
        SharedPreferences        pref   = getPreferences( MODE_PRIVATE );
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean( PREF_IS_FIRST_LOAD, false );
        editor.commit();
    }

}
