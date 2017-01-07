package marcook_pool.pool_finder.activites;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.fragments.PoolTableReviewFragment;
import marcook_pool.pool_finder.ui.RecyclerViewHolder;

public class OnePoolLocationActivity extends AppCompatActivity {

    PoolTableReviewFragment mPoolTableReviewFragment = new PoolTableReviewFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_pool_location);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(RecyclerViewHolder.KEY_ESTABLISHMENT, intent.getStringExtra(RecyclerViewHolder.KEY_ESTABLISHMENT));
        bundle.putString(RecyclerViewHolder.KEY_DESCRIPTION, intent.getStringExtra(RecyclerViewHolder.KEY_DESCRIPTION));
        bundle.putString(RecyclerViewHolder.KEY_LOCATION, intent.getStringExtra(RecyclerViewHolder.KEY_LOCATION));
        bundle.putFloat(RecyclerViewHolder.KEY_RATING_BAR, intent.getFloatExtra(RecyclerViewHolder.KEY_RATING_BAR, 0));
        mPoolTableReviewFragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.activity_one_pool_location, mPoolTableReviewFragment);
        fragmentTransaction.commit();
    }
}
