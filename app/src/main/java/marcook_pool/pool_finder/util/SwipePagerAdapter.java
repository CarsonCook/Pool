package marcook_pool.pool_finder.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import marcook_pool.pool_finder.activites.MainActivity;
import marcook_pool.pool_finder.fragments.SubmitTableFragment;
import marcook_pool.pool_finder.fragments.TablesListFragment;

/**
 * Created by Carson on 17/09/2016.
 * Used to help implement tabs in MainActivity.
 */
public class SwipePagerAdapter extends FragmentPagerAdapter {
    public SwipePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.TABLES_LIST_POS:
                fragment = new TablesListFragment();
                break;
            case MainActivity.SUBMIT_TABLE_POS:
                fragment = new SubmitTableFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return MainActivity.NUM_TABS; //two tabs
    }
}