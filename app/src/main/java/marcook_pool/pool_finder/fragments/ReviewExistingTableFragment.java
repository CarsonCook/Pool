package marcook_pool.pool_finder.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import marcook_pool.pool_finder.R;
import marcook_pool.pool_finder.util.RecyclerViewHolder;

/**
 * Created by Carson on 17/10/2016.
 * Screen for user to review an existing pool table in the database.
 * Attached to TableInformationActivity.
 */
public class ReviewExistingTableFragment extends Fragment {

    private TextView mTable;
    private EditText mDescription;
    private Button mPhoto;
    private Button mSubmit;
    private RatingBar mRating;
    private ImageView mImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_existing, container, false);
        setViews(v);
        setOnClickListeners();
        return v;
    }

    /**
     * Used to attach the UI variables to elements in the XML layout.
     * Used to cleanup code, called from onCreateView().
     *
     * @param v View used to find the XML layout's elements.
     */
    private void setViews(View v) {
        //get views from XML
        mTable = (TextView) v.findViewById(R.id.establishment);
        mDescription = (EditText) v.findViewById(R.id.description);
        mPhoto = (Button) v.findViewById(R.id.add_photo_button);
        mSubmit = (Button) v.findViewById(R.id.submit);
        mRating = (RatingBar) v.findViewById(R.id.rating_bar);
        mImage = (ImageView) v.findViewById(R.id.photo);

        //set the title of the table
        mTable.setText(getArguments().getString(RecyclerViewHolder.KEY_ESTABLISHMENT));
    }

    /**
     * Used to set onClickListeners for each button.
     * Used to cleanup code, called from onCreateView().
     */
    private void setOnClickListeners() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDescription.getText().length() <= 0) {
                    Toast.makeText(getContext(), getString(R.string.no_descrip), Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().finish();
                }
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
