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
import android.util.Log;

/**
 * Created by Carson on 28/11/2016.
 */

public class GpsManager extends Service implements LocationListener {

    private final String TAG = "GpsManager";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //The minimum distance to change Updates in meters: 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; //The minimum time between updates in milliseconds: 1 minute

    //flags for able to get gps
    private boolean mIsGpsEnabled = false;
    private boolean mIsNetworkEnabled = false;

    private Location mLocation;
    private double mLatitude;
    private double mLongitude;

    private Context mContext;
    private LocationManager mLocationManager;

    public GpsManager(Context context) {
        mContext = context;
        mLocationManager = (android.location.LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        mIsGpsEnabled = mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        mIsNetworkEnabled = mLocationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
    }

    public Location getLocation() {
        try {
            if (canGetLocation()) {
                // First get location from Network Provider
                if (mIsNetworkEnabled) {
                    mLocationManager.requestLocationUpdates
                            (android.location.LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (mIsGpsEnabled) {
                    if (mLocation == null && ContextCompat.checkSelfPermission(mContext, //checks if GPS Permission had, if not GET IN ACTIVITY
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLocationManager.requestLocationUpdates
                                (android.location.LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (mLocationManager != null) {
                            mLocation = mLocationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }

    public void promptTurnOnGps() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS is settings")
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    public void stopUsingGPS() {
        if (mLocationManager != null && ContextCompat.checkSelfPermission(mContext, //checks if GPS Permission had
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(GpsManager.this);
        }
    }

    public String getCoordinates() {
        getLocation();
        return "Lat: " + getLatitude() + " Long: " + getLongitude();
    }

    private String getLatitude() {
        return Location.convert(mLatitude, Location.FORMAT_DEGREES);
    }

    private String getLongitude() {
        return Location.convert(mLongitude, Location.FORMAT_DEGREES);
    }

    public boolean canGetLocation() {
        return mIsNetworkEnabled || mIsGpsEnabled;
    }

    public boolean haveGpsPermission() {
        return ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Gets distance between two sets of latitude and longitude: the user's and a pool table's.
     *
     * @param userLat   The user's latitude.
     * @param userLong  The user's longitude.
     * @param tableLat  A table's latitude.
     * @param tableLong A table's longitude.
     * @return float holding distance bewteen the two points in km
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
