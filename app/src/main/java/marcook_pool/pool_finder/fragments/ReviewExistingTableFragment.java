package marcook_pool.pool_finder.fragments;

import android.annotation.TargetApi;
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
import marcook_pool.pool_finder.ui.RecyclerViewHolder;

/**
 * Created by Carson on 17/10/2016.
 */

@TargetApi(23)
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
        return inflater.inflate(R.layout.fragment_review_existing, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setViews();
        setOnClickListeners();
    }

    void setViews() {
        mTable = (TextView) getView().findViewById(R.id.establishment);
        mDescription = (EditText) getView().findViewById(R.id.description);
        mPhoto = (Button) getView().findViewById(R.id.add_photo_button);
        mSubmit = (Button) getView().findViewById(R.id.submit);
        mRating = (RatingBar) getView().findViewById(R.id.rating_bar);
        mImage = (ImageView) getView().findViewById(R.id.photo);

        mTable.setText(getArguments().getString(RecyclerViewHolder.KEY_ESTABLISHMENT));
    }

    void setOnClickListeners() {
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
