package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.objects.ShopObject;

public class PaidOrderFragment extends Fragment {
    private static final String TAG = "PaidOrderFragment";

    private View view;

    private IMainActivity mIMainActivity;
    private Context mContext;
    private int orderId;
    private Date createdDate;
    private ShopObject mShop;

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
                orderId = (int) objects.get(0);
                String dateStr = String.valueOf(objects.get(1));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                createdDate = formatter.parse(dateStr);
                mShop = (ShopObject) objects.get(2);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mIMainActivity.lockDrawer();

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_paid, container, false);

//            set all order button
            setAllOrderButton();
//            show order info
            setOrderInfo();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        mIMainActivity.unlockDrawer();
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

    private void setOrderInfo() {
        TextView orderNumTextView = view.findViewById(R.id.order_id);
        TextView orderDateTextView = view.findViewById(R.id.order_created);
        TextView orderShopTextView = view.findViewById(R.id.order_shop);

        orderNumTextView.setText(String.valueOf(orderId));
        orderDateTextView.setText(createdDate.toString());
//        add space before shop name so that it does not merge into sentence before
        orderShopTextView.setText(" " + mShop.getName());
    }
}
