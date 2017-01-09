package marcook_pool.pool_finder.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.util.PoolTable;
import marcook_pool.pool_finder.util.RecyclerViewAdapter;
import marcook_pool.pool_finder.util.SimpleDividerItemDecoration;
import marcook_pool.pool_finder.util.managers.TableLocationManager;

/**
 * Created by Carson on 17/09/2016.
 * Used to show all of the pool tables in the Verified Tables table in Firebase Cloud Database.
 * Shows tables in a Recycler View.
 * Attached to MainActivity.
 */
public class TablesListFragment extends Fragment {

    private final String TAG = "TablesListFragment";
    private final String VERIFIED_POOL_TABLES = "Verified Tables";

    public RecyclerView mRecyclerView; //public to allow adapter to use the Recycler View
    private RecyclerViewAdapter mAdapter;
    private TableLocationManager mTableLocationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pool_locations, container, false);
        mTableLocationManager = new TableLocationManager(getContext());
        setRecyclerView(v);
        accessDatabase();
        return v;
    }

    /**
     * Sets the recycler view used to show the pool tables. Used for code cleanup, called from onCreateView().
     *
     * @param v View of the fragment, used to find the recycler view XML layout
     */
    private void setRecyclerView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        //makes separation of items in recycler view look good
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        //get linear layout manager so that management of recycler view is similar to a list view
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
    }

    /**
     * Gets verified pool tables from Firebase Cloud Database.
     * Gets data when fragment first created, then whenever in fragment and data in the Verified Tables changes.
     * Used for code cleanup, called from onCreateView().
     */
    private void accessDatabase() {
        FirebaseDatabase.getInstance().getReference()
                .child(VERIFIED_POOL_TABLES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PoolTable> list = new ArrayList<>();
                //loops through children of dataSnapShop, snapshot IS each child
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PoolTable curTable = snapshot.getValue(PoolTable.class); //gets each table from each snapshot
                    if (curTable == null) {
                        Log.e(TAG, "Pool table is unexpectedly null");
                    } else {
                        list.add(curTable);
                        getUserLocation();
                        mAdapter = new RecyclerViewAdapter(list, getContext(),
                                mTableLocationManager.getLatitude(),mTableLocationManager.getLongitude());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "PoolTableLocations: ", databaseError.toException());
            }
        });
    }

    /**
     * Used to get current location to calculate distance to a pool table.
     * Used for code cleanup, called from onDataChange() in accessDatabase().
     */
    private void getUserLocation() {
        mTableLocationManager.getLocation();
        Toast.makeText(getActivity(), getString(R.string.location_recorded),
                Toast.LENGTH_SHORT).show();
    }
}
