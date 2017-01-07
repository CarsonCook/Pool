package marcook_pool.pool_finder.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import marcook_pool.pool_finder.managers.GpsManager;
import marcook_pool.pool_finder.ui.PoolTable;

/**
 * Created by Carson on 17/09/2016.
 */

public class SubmitLocationFragment extends Fragment {

    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private DatabaseReference mDatabase;
    private GpsManager mGpsManager;
    private String mCoordinates; //"latitude"+' '+"longitude", or NULL

    private Button mSubmitButton;
    private Button mLocationButton;
    private Button mPhotoButton;
    private EditText mEstablishment;
    private EditText mDescription;
    private RatingBar mRating;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit_locations, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGpsManager = new GpsManager(getContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setViews();
        setClickListeners();
    }

    private void setViews() {
        mSubmitButton = (Button) getView().findViewById(R.id.submit);
        mLocationButton = (Button) getView().findViewById(R.id.location);
        mPhotoButton = (Button) getView().findViewById(R.id.add_photo_button);
        mEstablishment = (EditText) getView().findViewById(R.id.establishment);
        mDescription = (EditText) getView().findViewById(R.id.description);
        mRating = (RatingBar) getView().findViewById(R.id.rating_bar);
    }

    private void setClickListeners() {
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGpsManager.haveGpsPermission()) { //request locaiton permissions
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                } else if (!mGpsManager.canGetLocation()) { //have permission but location service not on
                    mGpsManager.promptTurnOnGps();
                } else if (mGpsManager.canGetLocation() && mGpsManager.haveGpsPermission()) { //have permission, gps on, good to go
                    mCoordinates = mGpsManager.getCoordinates();
                    Toast.makeText(getActivity(), getString(R.string.location_recorded),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PoolTable curTable = new PoolTable();
                curTable.ID = 0;
                curTable.description = mDescription.getText().toString();
                if (mEstablishment.getText().toString().isEmpty() || mRating.getRating() < 0.5f) {
                    Toast.makeText(getContext(), getString(R.string.invalid_entry), Toast.LENGTH_SHORT).show();
                    return;
                }
                curTable.establishment = mEstablishment.getText().toString();
                curTable.rating = mRating.getRating();
                curTable.photoURL = "";
                curTable.location = mCoordinates; //used in case mGpsManager is never instantiated,
                                                    // check for null when used later
                mDatabase.child("Unverified Tables").child(curTable.establishment).setValue(curTable);
                Toast.makeText(getContext(), getString(R.string.submitted_table), Toast.LENGTH_SHORT).show();
                mDescription.setText("");
                mEstablishment.setText("");
                mRating.setRating(0);

                mGpsManager.stopUsingGPS();
            }
        });
    }
}
