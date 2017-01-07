package marcook_pool.pool_finder.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import marcook_pool.pool_finder.R;

/**
 * Created by Carson on 18/09/2016.
 * Used to manage the Recycler View used to show the pool tables in the database.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private final int MAX_DESCRIPTION_LENGTH = 30;
    private Context mContext;

    private List<PoolTable> mPoolTables; //list of each person's data

    public RecyclerViewAdapter(List<PoolTable> poolTable, Context context) {
        this.mPoolTables = poolTable;
        this.mContext = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create the view for one item in the card layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_layout, parent, false);
        return (new RecyclerViewHolder(view));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //set fields in the card...e.g. location, rating etc.
        holder.mEstablishment.setText(mPoolTables.get(position).getEstablishment());
        setDescription(holder, position);
        holder.mLocation.setText(mPoolTables.get(position).getLocation());
        holder.mRatingBar.setRating(mPoolTables.get(position).getReview());
    }

    @Override
    public int getItemCount() {
        return this.mPoolTables.size();
    }

    /**
     * Sets the description field in the cards for the pool tables.
     * Used to cleanup code, called from onBindViewHolder().
     *
     * @param holder   The RecyclerViewHolder instance.
     * @param position The position in the list of mPoolTables that we are dealing with.
     */
    private void setDescription(RecyclerViewHolder holder, int position) {
        String description = mPoolTables.get(position).getDescription();
        if (description.length() <= MAX_DESCRIPTION_LENGTH) {
            holder.mDescription.setText(description);
        } else { //if description is too long just show a preview with ".." concatenated.
            String shortDescription = "";
            for (int i = 0; i < MAX_DESCRIPTION_LENGTH - 3; i++) { //leave room for 3 dots, so -3
                shortDescription += description.charAt(i);
            }
            shortDescription += mContext.getString(R.string.ellipses);
            holder.mDescription.setText(shortDescription);
            holder.mLongDescription.setText(description);
        }
    }
}
