package marcook_pool.pool_finder.managers;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import marcook_pool.pool_finder.R;

/**
 * Created by Carson on 28/11/2016.
 * Used to get user location for a table.
 */
public class TableLocationManager extends Service implements LocationListener {
    private final String TAG = "TableLocationManager";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //The minimum distance to change Updates in meters: 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; //The minimum time between updates in milliseconds: 1 minute


    private boolean mIsLocationEnabled = false; //uses network to get location
    private double mLatitude;
    private double mLongitude;

    private Context mContext;
    private LocationManager mLocationManager;

    public TableLocationManager(Context context) {
        this.mContext = context;
        this.mLocationManager = (android.location.LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        this.mIsLocationEnabled = mLocationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Used to get user location from gps or network!!
     * Used as code cleanup, called from getCoordinates().
     */
    @SuppressWarnings("all")
    //haveLocationPermission() checks for permission but IDE doesn't realize that and gives warning
    private void getLocation() {
        try {
            if (canGetLocation()) {
                if (mIsLocationEnabled && haveLocationPermission()) {
                    mLocationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Location location = mLocationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to prompt the user to turn on GPS. If they choose to do so,
     * sends them to the settings screen to turn it on.
     * Public so that this can be done at will throughout the app.
     */
    public void promptTurnOnGps() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(mContext.getString(R.string.gps_off))
                .setMessage(mContext.getString(R.string.go_to_settings));

        // On pressing Settings button
        alertDialog.setPositiveButton(mContext.getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //start settings in app to turn on location.
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        }).setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    /**
     * Used to stop acquiring updates from LocationManager.
     * Public so that anytime location taken in app, it can be stopped. Able to be used everywhere and at will.
     */
    @SuppressWarnings("all")
    //haveLocationPermission() checks for permission but IDE doesn't realize that and gives warning
    public void stopUsingGPS() {
        if (mLocationManager != null && haveLocationPermission()) {
            mLocationManager.removeUpdates(TableLocationManager.this);
        }
    }

    /**
     * Accessor to get lat/long in app.
     *
     * @return Returns string of format Lat:value Long:value.
     */
    public String getCoordinates() {
        getLocation(); //sets mLat/mLong
        return "Lat:" + getLatitude() + " Long:" + getLongitude(); //returns good with good formatting
    }

    /**
     * Gets saved latitude in mLatitude in a string formatted to degrees.
     *
     * @return String holding latitude in degrees.
     */
    private String getLatitude() {
        return Location.convert(mLatitude, Location.FORMAT_DEGREES);
    }

    /**
     * Gets saved longitude in mLongitude in a string formatted to degrees.
     *
     * @return String holding longitude in degrees.
     */
    private String getLongitude() {
        return Location.convert(mLongitude, Location.FORMAT_DEGREES);
    }

    /**
     * Checks if it is possible to get current location.
     * Public so code can check before calling to get location, able to handle this scenario.
     * Still used in this class, won't crash app if not called elsewhere before trying to get location.
     *
     * @return True if can get location (network or GPS), else false
     */
    public boolean canGetLocation() {
        return mIsLocationEnabled;
    }

    /**
     * Checks if have permission to get location of the phone.
     *
     * @return True if have permission, false if not.
     */
    public boolean haveLocationPermission() {
        return ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Gets distance between two sets of latitude and longitude: the user's and a pool table's.
     * Public so that the distance can be acquired at will throughout app.
     * No need to dummy proof for permission etc. as lats/longs are parameters, not class variables.
     *
     * @param userLat   The user's latitude.
     * @param userLong  The user's longitude.
     * @param tableLat  A table's latitude.
     * @param tableLong A table's longitude.
     * @return float holding distance between the two points in km
     */
    public float getDistanceBetweenLatLongPair(double userLat, double userLong, double tableLat, double tableLong) {
        Location userPosition = new Location("user_position");
        userPosition.setLatitude(userLat);
        userPosition.setLongitude(userLong);

        Location tablePosition = new Location("table_position");
        tablePosition.setLatitude(tableLat);
        tablePosition.setLongitude(tableLong);

        return userPosition.distanceTo(tablePosition) / 1000; //1000 converts from m to km
    }

    //methods that have to override.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        //use to fix when gps turned on mid session bug (issue #10)?
    }

    @Override
    public void onProviderDisabled(String s) {}
}
