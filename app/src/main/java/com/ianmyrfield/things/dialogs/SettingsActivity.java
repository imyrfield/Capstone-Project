package com.ianmyrfield.things.dialogs;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ianmyrfield.things.NoteListActivity;
import com.ianmyrfield.things.R;


/**
 * Created by Ian on 6/17/2016.
 */
public class SettingsActivity
        extends AppCompatActivity {

    public static final  String PREF_SORT_KEY                   = "pref_sort_key";
    public static final  String PREF_NOTIFICATION_KEY           = "pref_notification_key";
    private static final int    RC_SIGN_IN                      = 100;
    private TextView  mNameTextView;
    private ImageView mImageView;
    private Button    signIn;
    private Button    signOut;

    private static final Preference.OnPreferenceChangeListener sPreferenceChangeListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange ( Preference preference, Object o ) {
                    String stringValue = o.toString();
                    if ( preference instanceof ListPreference ) {
                        ListPreference listPreference = (ListPreference) preference;
                        int            index          = listPreference.findIndexOfValue( stringValue );

                        preference.setSummary( index >= 0
                                               ? listPreference.getEntries()[ index ]
                                               : null );
                    }  else {
                        preference.setSummary( stringValue );
                    }

                    return true;
                }
            };

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.settings_activity );
        Context context = this;
        Toolbar toolbar = (Toolbar) findViewById( R.id.settings_toolbar );
        setSupportActionBar( toolbar );

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }
        
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                                       .add( R.id.settings_container,
                                             new SettingsFragment() )
                                       .commit();
        }

        mNameTextView = (TextView) findViewById( R.id.profile_name );
        mImageView = (ImageView) findViewById( R.id.profile_pic );
        signIn = (Button) findViewById( R.id.settings_sign_in );
        signOut = (Button) findViewById( R.id.settings_sign_out );

        if ( signIn != null ) {
            signIn.setOnClickListener( new View.OnClickListener() {
                @Override public void onClick ( View v ) {
                    startActivityForResult( AuthUI.getInstance()
                                                  .createSignInIntentBuilder()
                                                  .setProviders( AuthUI.EMAIL_PROVIDER,
                                                                 AuthUI.GOOGLE_PROVIDER )
                                                  .setTheme( R.style.LoginTheme )
                                                  .build(), RC_SIGN_IN );
                }
            } );
        }

        if ( signOut != null ) {
            signOut.setOnClickListener( new View.OnClickListener() {
                @Override public void onClick ( View v ) {
                    AuthUI.getInstance().signOut( SettingsActivity.this )
                          .addOnCompleteListener( new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete ( @NonNull Task<Void> task ) {
                                  updateProfileInformation();
                                  Toast.makeText( SettingsActivity.this, "Sign Out Successful", Toast.LENGTH_SHORT )
                                       .show();
                              }
                          } );
                }
            } );
        }

        updateProfileInformation();
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo( new Intent( this, NoteListActivity.class ) );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    private static void bindPreferenceSummaryToValue ( Preference preference ) {
        preference.setOnPreferenceChangeListener( sPreferenceChangeListener );
        sPreferenceChangeListener.onPreferenceChange( preference,
                                                      PreferenceManager.getDefaultSharedPreferences( preference.getContext() )
                                                              .getString( preference.getKey(), "" ) );
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == RC_SIGN_IN ) {
            if ( resultCode == RESULT_OK ) {
                updateProfileInformation();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder( this );
                builder.setMessage( R.string.error_sign_in_failed );
                builder.setPositiveButton( R.string.button_ok_text,
                                           new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick ( DialogInterface dialog,
                                                                     int which ) {
                                                   dialog.dismiss();
                                               }
                                           } );
                builder.show();
            }
        }
    }

    private void updateProfileInformation () {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if ( auth.getCurrentUser() != null ) {
            FirebaseUser user = auth.getCurrentUser();

            String userName = user.getDisplayName();
            if ( mNameTextView != null && userName != null ) {
                mNameTextView.setText( userName );
            }

            Uri profilePictureUrl = user.getPhotoUrl();
            Log.d( "SettingsActivity", "updateProfileInformation (line 168): " + profilePictureUrl );
            if ( mImageView != null && profilePictureUrl != null ) {
                Glide.with( this )
                     .load( profilePictureUrl )
                     .placeholder( android.R.drawable.ic_menu_gallery)
                     .into( mImageView );
            }

            signIn.setVisibility( View.GONE );
            signOut.setVisibility( View.VISIBLE );

        }
        else {

            signIn.setVisibility( View.VISIBLE );
            signOut.setVisibility( View.GONE );
            mNameTextView.setText( R.string.settings_not_signed_in_message );
            Glide.with( this )
                 .load( android.R.drawable.ic_menu_gallery )
                 .into( mImageView );
        }
    }

    public static class SettingsFragment
            extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );
        }

        @Override
        public void onCreatePreferences ( Bundle bundle, String s ) {
            addPreferencesFromResource( R.xml.preferences );
            bindPreferenceSummaryToValue( findPreference( PREF_SORT_KEY ) );
            findPreference( PREF_NOTIFICATION_KEY ).setOnPreferenceChangeListener( this );
        }
    
        @Override
        public boolean onPreferenceChange ( Preference preference, Object o ) {
            Log.d("SettingsFragment", "onPreferenceChange (line 248): ");
            if (preference.getKey().equals( PREF_NOTIFICATION_KEY) && o.equals( false ) ){
                Log.d( "SettingsActivity", "onSharedPreferenceChanged (line 221): " );
                cancelNotifications();
            }
            return true;
        }

        private void cancelNotifications () {
            NotificationManager manager = (NotificationManager) getActivity().getSystemService( NOTIFICATION_SERVICE );
            manager.cancelAll();
        }
    }
}
