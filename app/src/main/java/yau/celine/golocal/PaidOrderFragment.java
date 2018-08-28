package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;

import yau.celine.golocal.utils.interfaces.IMainActivity;

public class PaidOrderFragment extends Fragment {
    private static final String TAG = "PaidOrderFragment";

    private View view;

    private IMainActivity mIMainActivity;
    private Context mContext;
    private int orderId;
    private Date createdDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            try {
                ArrayList objects = bundle.getParcelableArrayList(getString(R.string.intent_message));

                Log.d(TAG, objects.toString());

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIMainActivity.lockDrawer();

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_paid, container, false);

//            set all order button
            setAllOrderButton();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mIMainActivity.unlockDrawer();
    }

    private void setAllOrderButton() {
        Button btnAllOrders = view.findViewById(R.id.continue_to_order);
        btnAllOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIMainActivity.inflateFragment(getString(R.string.fragment_history), "");
            }
        });
    }
}
