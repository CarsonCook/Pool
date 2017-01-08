package marcook_pool.pool_finder.activites;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
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

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.util.SwipePagerAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Main screen in app, has tabs to go view the tables in the database, or to submit a new table.
 * Can also navigate to settings and search from here.
 * Attaches TablesListFragment and SubmitTableFragment.
 * Start SettingsActivity and StormpathLoginActivity (Stormpath Login API).
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    public static String KEY_SORT_PREF = "sort_pref";
    public static String KEY_FILTER_PREF = "filter_pref";
    private final String TABLE_LOCATIONS = "table_locations";
    private final String SUBMIT_LOCATION = "submit_location";
    public static final int TABLES_LIST_POS = 0; //1st tab so position 0
    public static final int SUBMIT_TABLE_POS = 1; //second tab
    public static final int NUM_TABS = 2; //number of tabs used

    private ViewPager mViewPager;

    //for Stormpath Login API, private doesn't work
    public OkHttpClient mOkHttpClient;
    public static final String mBaseUrl = "https://stormpathnotes.herokuapp.com/";

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
            public void onSuccess(UserProfile userProfile) { //User is logged in
                showToast(getString(R.string.logged_in));
            }

            @Override
            public void onFailure(StormpathError error) {
                // Show error message and login view
                if (error.code() != -1) { //unknown error, happens when open app for first time
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
                    .baseUrl(mBaseUrl)
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
            this.mOkHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(httpLoggingInterceptor).build();
            startActivity(new Intent(this, StormpathLoginActivity.class)); //**only when Stormpath not init??
        }
    }

    /**
     * Makes tabs for TablesListFragment and SubmitTableFragment.
     * Used to make above code clearer. Called from onCreate().
     */
    private void makeTabs() {
        SwipePagerAdapter swipePagerAdapter = new SwipePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(swipePagerAdapter);
        final ActionBar actionBar = getSupportActionBar(); //the top bar on the app, holds name, tabs etc.

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = createTabListener();
        //adds tabs to the action bar
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.table_locations_tab))
                .setTag(TABLE_LOCATIONS).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.submit_tables_tab))
                .setTag(SUBMIT_LOCATION).setTabListener(tabListener));

        setupSwipeTab(actionBar);
    }

    /**
     * Create a tab listener for the action bar that handles events like on Tab Selected.
     * Used for code cleanup, called from makeTabs().
     *
     * @return TabListener object that holds the event callbacks.
     */
    private ActionBar.TabListener createTabListener() {
        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) { //for tab click NOT swipe
                if (tab.getTag() == TABLE_LOCATIONS) { //start TablesListFragment
                    mViewPager.setCurrentItem(tab.getPosition());
                    // ft.replace(R.id.activity_main, mTablesListFragment);

                } else if (tab.getTag() == SUBMIT_LOCATION) { //start SubmitTableFragment
                    mViewPager.setCurrentItem(tab.getPosition());
                }
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };
        return tabListener;
    }

    /**
     * Sets the listener for the tabs that allows swiping between them.
     *
     * @param actionBar Actionbar where the tabs are, need to change tabs.
     */
    private void setupSwipeTab(final ActionBar actionBar) {
        //set listener for page change, implements swipe views NOT tab clicks
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
    }

    /**
     * Used to display toasts. Makes code cleaner.
     *
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
        inflater.inflate(R.menu.options_menu, menu); //adds options_menu layout to options menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //determine which button is clicked based on Id
        switch (item.getItemId()) { //search for pool tables
            case R.id.search:
                return true;
            case R.id.settings: //start the SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.review_app: //review app in Play Store
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
