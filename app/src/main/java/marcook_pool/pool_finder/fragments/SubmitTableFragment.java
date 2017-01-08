package marcook_pool.pool_finder.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.util.managers.TableLocationManager;
import marcook_pool.pool_finder.util.PoolTable;

/**
 * Created by Carson on 17/09/2016.
 * Used to hold UI for the submit a new location aspect of app.
 * Shows buttons and fields to add a photo, location, title, description etc.
 * Attached to MainActivity.
 */
public class SubmitTableFragment extends Fragment {

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private final String UNVERIFIED_TABLES = "Unverified Tables";

    private DatabaseReference mDatabase;
    private TableLocationManager mTableLocationManager;
    private String mCoordinates; //"latitude"+' '+"longitude", or NULL
    //used instead of getLat/Long so you can check if no location was added (NULL)
    private Button mSubmitButton;
    private Button mLocationButton;
    private Button mPhotoButton;
    private EditText mEstablishment;
    private EditText mDescription;
    private RatingBar mRating;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_submit_locations, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTableLocationManager = new TableLocationManager(getContext());
        setViews(v);
        setClickListeners();
        return v;
    }

    /**
     * Used to attach the UI variables to elements in the XML layout.
     * Used to cleanup code, called from onCreateView().
     *
     * @param v View used to find the XML layout's elements.
     */
    private void setViews(View v) {
        mSubmitButton = (Button) v.findViewById(R.id.submit);
        mLocationButton = (Button) v.findViewById(R.id.location);
        mPhotoButton = (Button) v.findViewById(R.id.add_photo_button);
        mEstablishment = (EditText) v.findViewById(R.id.establishment);
        mDescription = (EditText) v.findViewById(R.id.description);
        mRating = (RatingBar) v.findViewById(R.id.rating_bar);
    }

    /**
     * Used to set onClickListeners for each button.
     * Used to cleanup code, called from onCreateView().
     */
    private void setClickListeners() {
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to submit need at least title and a rating
                if (mEstablishment.getText().toString().isEmpty() || mRating.getRating() < 0.5f) {
                    Toast.makeText(getContext(), getString(R.string.invalid_entry), Toast.LENGTH_SHORT).show();
                    return;
                }

                PoolTable curTable = new PoolTable();
                setTableInfo(curTable);

                //add to database and give UI confirmation
                mDatabase.child(UNVERIFIED_TABLES).child(curTable.establishment).setValue(curTable);
                Toast.makeText(getContext(), getString(R.string.submitted_table), Toast.LENGTH_SHORT).show();

                resetUI();
            }
        });
    }

    /**
     * Used to get user location with TableLocationManager.
     * Used for code cleanup, called when Add Location button is clicked.
     */
    private void getUserLocation() {
        if (!mTableLocationManager.haveLocationPermission()) { //request location permissions if do not have it
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else if (!mTableLocationManager.canGetLocation()) { //have permission but location service not on
            mTableLocationManager.promptTurnOnGps();
        } else if (mTableLocationManager.canGetLocation() && mTableLocationManager.haveLocationPermission()) {//can get location
            //record coordinates and have UI confirmation
            mCoordinates = mTableLocationManager.getCoordinates();
            Toast.makeText(getActivity(), getString(R.string.location_recorded),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets information for a table to be added to the database.
     * Used for code cleanup, called when Submit button is clicked, after checking that the info is useable
     *
     * @param table The table information is added to.
     */
    private void setTableInfo(PoolTable table) {
        table.ID = 0;
        table.description = mDescription.getText().toString();
        table.establishment = mEstablishment.getText().toString();
        table.rating = mRating.getRating();
        table.photoURL = "";
        table.location = mCoordinates;
    }

    /**
     * Removes any visual changes such as adding a title or a description.
     * Used for code cleanup, called when processing done after user pressing Submit button.
     */
    private void resetUI() {
        mDescription.setText("");
        mEstablishment.setText("");
        mRating.setRating(0);
        mTableLocationManager.stopUsingGPS();
    }
}
