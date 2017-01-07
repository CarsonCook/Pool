package marcook_pool.pool_finder.activites;

import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.models.StormpathError;
import com.stormpath.sdk.models.UserProfile;
import com.stormpath.sdk.ui.StormpathLoginActivity;

import marcook_pool.pool_finder.fragments.PoolLocationsFragment;
import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.fragments.SubmitLocationFragment;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    public static String KEY_SORT_PREF = "sort_pref";
    public static String KEY_FILTER_PREF = "filter_pref";

    private final String TABLE_LOCATIONS = "table_locations";
    private final String SUBMIT_LOCATION = "submit_location";

    private static final String baseUrl = "https://stormpathnotes.herokuapp.com/";

    PoolLocationsFragment mPoolLocationsFragment = new PoolLocationsFragment();
    SubmitLocationFragment mSubmitLocationFragment = new SubmitLocationFragment();

    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login();
        makeTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Stormpath.getUserProfile(new StormpathCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                showToast(getString(R.string.logged_in));
            }

            @Override
            public void onFailure(StormpathError error) {
                // Show error message and login view
                if (error.code() != -1) { //unknown error, happens when open app for first time. Don't show toast for this
                    showToast(getString(R.string.error_logging_in));
                }
                Log.d(TAG, "stormpath login error: " + error.message());
                startActivity(new Intent(MainActivity.this, StormpathLoginActivity.class));
            }
        });
    }

    /**
     * Logs user into app. Uses Stormpath Login API.
     * Used only to make above code clearer. Called in onCreate()
     */
    private void login() {
        if (!Stormpath.isInitialized()) { //don't need to redo these things if just re-opening app
            // Initialize Stormpath if not already
            StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                    .baseUrl(baseUrl)
                    .build();
            Stormpath.init(this, stormpathConfiguration);

            // Initialize OkHttp library.
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Stormpath.logger().d(message);
                }
            });

            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            this.okHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(httpLoggingInterceptor).build();
            startActivity(new Intent(this, StormpathLoginActivity.class)); //**only when Stormpath not init??
        }
    }

    /**
     * Makes tabs for PoolLocationsFragment and SubmitLocationFragment.
     * Used to make above code clearer. Called from onCreate().
     */
    private void makeTabs() {
        ActionBar actionBar = getSupportActionBar(); //the top bar on the app, holds name, tabs etc.
        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                if (tab.getTag() == TABLE_LOCATIONS) { //start PoolLocationsFragment
                    ft.replace(R.id.activity_main, mPoolLocationsFragment);

                } else if (tab.getTag() == SUBMIT_LOCATION) { //start SubmitLocationFragment
                    ft.replace(R.id.activity_main, mSubmitLocationFragment);
                }
                //any other option is an error, do nothing
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                //ignore this event
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                //ignore this event
            }
        };

        //adds tabs to the action bar
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.table_locations_tab))
                .setTag(TABLE_LOCATIONS).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.submit_tables_tab))
                .setTag(SUBMIT_LOCATION).setTabListener(tabListener));
    }

    /**
     * Used to display toasts. Makes code cleaner.
     * @param text String to display in toast
     */
    private void showToast(String text) {
        Spannable centeredText = new SpannableString(text); //need Spannable to make centred text
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, text.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE); //actually centre text
        Toast.makeText(this, centeredText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.review_app:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Stormpath.logout();//logs user out when app closed
    }
}
