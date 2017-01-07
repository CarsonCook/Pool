package marcook_pool.pool_finder.activites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.ui.StormpathLoginActivity;

import marcook_pool.pool_finder.R;

/**
 * Used to show settings such as filtering/sorting options, manage private tables and sign out.
 * Started from MainActivity.
 */
public class SettingsActivity extends AppCompatActivity {

    private Button mSignOut;
    private Button mFilterButton;
    private Button mSortButton;
    private Button mManagePrivatesButton;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPrefs = getPreferences(Context.MODE_PRIVATE);

        setViews();
        setClickListeners();
    }

    /**
     * Attaches UI element variables to XML layout elements.
     * Used for cleaner code above. Called from onCreate().
     */
    private void setViews() {
        mSignOut = (Button) findViewById(R.id.signOutButton);
        mFilterButton = (Button) findViewById(R.id.filter);
        mSortButton = (Button) findViewById(R.id.sort);
        mManagePrivatesButton = (Button) findViewById(R.id.manage_privates);
    }

    /**
     * Set onClickListeners on buttons.
     * Used for cleaner code above. Called from onCreate().
     */
    private void setClickListeners() {
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates dialog with text saved in XML array filter_choices
                makeDialog(R.array.filter_choices, getString(R.string.filter_results), MainActivity.KEY_FILTER_PREF);
            }
        });
        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates dialog with text in XML array sort_choices
                makeDialog(R.array.sort_choices, getString(R.string.sort_results), MainActivity.KEY_SORT_PREF);
            }
        });
        mManagePrivatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUserOut();
            }
        });
    }

    /**
     * Makes alert dialog based on XML array for text choices. Used to clean up code.
     *
     * @param array XML array with text choices for the dialog.
     * @param title String that is the title of the dialog.
     * @param key   String that identifies which dialog was chosen if information is put into shared prefs.
     *              Final because it is access in ClickListener inner class.
     */
    private void makeDialog(int array, String title, final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(title).setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //puts user choice into shared preferences, either a filter or sort
                mPrefs.edit().putInt(key, which).apply();
            }
        });
        builder.create().show();
    }

    /**
     * Signs user out of app using Stormpath Login API.
     * Used for code cleanup. Called when Sign Out button is clicked.
     */
    private void signUserOut() {
        Stormpath.logout();
        startActivity(new Intent(this, StormpathLoginActivity.class));
        finish();
    }
}
