package yau.celine.golocal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yau.celine.golocal.utils.interfaces.IMainActivity;

public class ConfirmOrderFragment extends Fragment {
    private static final String TAG="ConfirmOrderFragment";

    private IMainActivity mIMainActivity;
    private Context mContext;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        mIMainActivity = (IMainActivity)getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mIMainActivity.unlockDrawerBottom();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        }


//        disable drawer
        mIMainActivity.lockDrawerBottom();

        return view;
    }
}
