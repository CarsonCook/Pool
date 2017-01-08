package marcook_pool.pool_finder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.util.RecyclerViewHolder;

/**
 * Created by Carson on 18/10/2016.
 * Fragment used to show detailed table information when a table in the Recycler view is clicked on.
 * Shows information of the table such as rating, title, description etc.
 * Attached to TableInformationActivity.
 */
public class TableInfoFragment extends Fragment {

    private TextView mEstablishment;
    private TextView mDescription;
    private TextView mDistance;
    private RatingBar mRatingBar;
    private Button mLeaveReview;

    private ReviewExistingTableFragment mReviewExistingFragment = new ReviewExistingTableFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pool_table_review, container, false);
        setViews(v);
        setViewValues();
        return v;
    }

    /**
     * Used to attach the UI variables to elements in the XML layout.
     * Used to cleanup code, called from onCreateView().
     *
     * @param v View used to find the XML layout's elements.
     */
    private void setViews(View v) {
        mEstablishment = (TextView) v.findViewById(R.id.establishment);
        mDescription = (TextView) v.findViewById(R.id.description);
        mDistance = (TextView) v.findViewById(R.id.distance);
        mRatingBar = (RatingBar) v.findViewById(R.id.rating_bar);
        mLeaveReview = (Button) v.findViewById(R.id.leave_review);
    }

    /**
     * Used to set what is shown in UI elements.
     * This information is passed to fragment from activity using a bundle.
     * Used to cleanup code, called from onCreateView().
     */
    private void setViewValues() {
        Bundle bundle = getArguments();
        mEstablishment.setText(bundle.getString(RecyclerViewHolder.KEY_ESTABLISHMENT));
        mDescription.setText(bundle.getString(RecyclerViewHolder.KEY_DESCRIPTION));
        mDistance.setText(bundle.getString(RecyclerViewHolder.KEY_DISTANCE));
        mRatingBar.setRating(bundle.getFloat(RecyclerViewHolder.KEY_RATING_BAR, 0));
        mLeaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewExistingTable();
            }
        });
    }

    /**
     * Used to start fragment to allow user to review this existing pool table.
     * Send the table name using a bundle.
     * Used to cleanup code, called when user clicks Leave Review button.
     */
    private void reviewExistingTable() {
        //put title of table into bundle so ReviewExistingFragment can access it
        Bundle bundle = new Bundle();
        bundle.putString(mEstablishment.getText().toString(), RecyclerViewHolder.KEY_ESTABLISHMENT);
        mReviewExistingFragment.setArguments(bundle);
        //start ReviewExistingFragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.activity_one_pool_location, mReviewExistingFragment);
        fragmentTransaction.commit();
    }
}
