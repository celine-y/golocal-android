package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.User;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private View view;

    private RelativeLayout loadingPanel;

    private IMainActivity mIMainActivity;

    private RecyclerView mRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIMainActivity.setToolbarTitle(getTag());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);

//            TODO: show profile info
            getUserInfo();

            Button logout = view.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPrefManager.getInstance(getContext()).logout();
                }
            });
        }
        return view;
    }

    private void getUserInfo(){
        User currentUser = SharedPrefManager.getInstance(getContext()).getUser();

        TextView mName = view.findViewById(R.id.profile_name);
        ImageView mImage = view.findViewById(R.id.profile_thumbnail);

        mName.setText(currentUser.getFullName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getContext())
                .load(currentUser.getProfilePhotoUrl())
                .apply(options)
                .into(mImage);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
