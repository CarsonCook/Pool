package marcook_pool.pool_finder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.ui.PoolTable;
import marcook_pool.pool_finder.ui.RecyclerViewAdapter;
import marcook_pool.pool_finder.ui.SimpleDividerItemDecoration;

/**
 * Created by Carson on 17/09/2016.
 */

public class PoolLocationsFragment extends Fragment {

    private final String TAG = "PoolLocationsFragment";

    private DatabaseReference mDatabase;

    public RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pool_locations, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Verified Tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PoolTable> list = new ArrayList<PoolTable>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PoolTable curTable = snapshot.getValue(PoolTable.class);
                    if (curTable == null) {
                        // User is null, error out
                        Log.e(TAG, "Pool table is unexpectedly null");
                    } else {
                        list.add(curTable);
                        mAdapter = new RecyclerViewAdapter(list, getContext());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "PoolTableLocations: ", databaseError.toException());
            }
        });
        return v;
    }
}
