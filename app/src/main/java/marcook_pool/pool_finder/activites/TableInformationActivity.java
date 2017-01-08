package marcook_pool.pool_finder.activites;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.fragments.TableInfoFragment;
import marcook_pool.pool_finder.util.RecyclerViewHolder;

public class TableInformationActivity extends AppCompatActivity {

    TableInfoFragment mTableInfoFragment = new TableInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_pool_location);

        Intent intent = getIntent(); //holds info sent from MainActivity to here
        //create bundle to send to fragment holding pool table info
        Bundle bundle = new Bundle();
        bundle.putString(RecyclerViewHolder.KEY_ESTABLISHMENT, intent.getStringExtra(RecyclerViewHolder.KEY_ESTABLISHMENT));
        bundle.putString(RecyclerViewHolder.KEY_DESCRIPTION, intent.getStringExtra(RecyclerViewHolder.KEY_DESCRIPTION));
        bundle.putString(RecyclerViewHolder.KEY_DISTANCE, intent.getStringExtra(RecyclerViewHolder.KEY_DISTANCE));
        bundle.putFloat(RecyclerViewHolder.KEY_RATING_BAR, intent.getFloatExtra(RecyclerViewHolder.KEY_RATING_BAR, 0));
        mTableInfoFragment.setArguments(bundle);
        //start fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.activity_one_pool_location, mTableInfoFragment);
        fragmentTransaction.commit();
    }
}
