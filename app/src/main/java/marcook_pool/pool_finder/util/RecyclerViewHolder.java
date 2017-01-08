package marcook_pool.pool_finder.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.activites.TableInformationActivity;

/**
 * Created by Carson on 18/09/2016.
 * Used to hold information for the Recycler View.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static final String KEY_ESTABLISHMENT = "KEY_ESTABLISHMENT";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    public static final String KEY_DISTANCE = "KEY_DISTANCE";
    public static final String KEY_RATING_BAR = "KEY_RATING_BAR";

    TextView mEstablishment;
    TextView mDescription; //description if <30 chars else 27 chars + ...
    TextView mLongDescription; //actual description if it exceeds 30 chars, if not then null
    TextView mDistance;
    RatingBar mRatingBar;

    private Context mContext;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mEstablishment = (TextView) itemView.findViewById(R.id.establishment);
        mDescription = (TextView) itemView.findViewById(R.id.description);
        mLongDescription = (TextView) itemView.findViewById(R.id.long_description);
        mDistance = (TextView) itemView.findViewById(R.id.distance);
        mRatingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        mContext = mEstablishment.getContext(); //holder has no context, can't use getContext, but TextView does
    }

    @Override
    public void onClick(View view) {
        //send them to TableInformationActivity with intent holding table information
        Intent intent = packTableInformationIntent();
        mContext.startActivity(intent);
    }

    /**
     * Checks if the table description is shortened or not.
     *
     * @param description The description shown on the card view
     * @return True if the description ends with 3 dots, else false.
     */
    private boolean descriptionContinues(String description) {
        //description continues longer if ends in ellipses
        //R.string.ellipses is a string that's just an ellipses, so charAt(0) gets char value of the ellipses
        return description.charAt(description.length() - 1) == mContext.getString(R.string.ellipses).charAt(0);
    }

    /**
     * Used to add table information to intent used to start TableInformationActivity.
     * Used to cleanup code, called from onClick().
     *
     * @return Intent holding table information and is targeting TableInformationActivity.
     */
    private Intent packTableInformationIntent() {
        Intent intent = new Intent(mContext, TableInformationActivity.class);
        intent.putExtra(KEY_ESTABLISHMENT, mEstablishment.getText().toString());
        if (descriptionContinues(mDescription.getText().toString())) {
            intent.putExtra(KEY_DESCRIPTION, mLongDescription.getText().toString());
        } else {
            intent.putExtra(KEY_DESCRIPTION, mDescription.getText().toString());
        }
        intent.putExtra(KEY_DISTANCE, mDistance.getText().toString());
        intent.putExtra(KEY_RATING_BAR, mRatingBar.getRating());
        return intent;
    }
}
