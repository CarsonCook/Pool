package marcook_pool.pool_finder.util;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ryan on 18/09/16.
 * Represents a single pool table in the Firebase Cloud database. Holds all of the information needed.
 */
@IgnoreExtraProperties
public class PoolTable {
    public int ID;
    public String photoURL;
    public String establishment;
    public double latitude;
    public double longitude;
    public String description;
    public float rating;

    public PoolTable() {
    }

    public String getEstablishment() {
        return establishment;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getReview() {
        return rating;
    }
}
