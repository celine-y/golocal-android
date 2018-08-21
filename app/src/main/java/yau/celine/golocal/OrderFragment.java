package yau.celine.golocal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.utils.interfaces.CartChangeCallback;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.objects.OrderItemObject;
import yau.celine.golocal.utils.objects.ShopDetails;
import yau.celine.golocal.utils.adapters.OrderItemAdapter;

public class OrderFragment extends Fragment implements
        CartChangeCallback{
    private static final String TAG = "OrderFragment";

    private IMainActivity mIMainActivity;
    private Context mContext;
    private View view;

    private RecyclerView mRecyclerView;
    private OrderItemAdapter mOrderItemAdapter;

    private ShopDetails mShopDetails;
    private String mShopName;

    private TextView mOrderTitle;
    private TextView mShopAddress;
    private TextView mShopPhone;

    private FloatingActionButton mContinueButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        mIMainActivity = (IMainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        disable drawer
        mIMainActivity.lockDrawerBottom();

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order, container, false);

//            display header info
            showHeaderInfo();
//            display order
            showOrderListRecycler();
//            enable continue button
            setContinueButton();
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mIMainActivity.unlockDrawerBottom();
    }

    private void showHeaderInfo() {
//        display order number
        mOrderTitle = view.findViewById(R.id.order_num_items);
        setHeaderCartNumber(CartSingleton.getInstance().getOrderSize());

//        display shop info
        if (CartSingleton.getInstance().getShop() != null) {
            getShopAddress();
        }
    }

    private void showShopDetails() {
        mShopAddress = view.findViewById(R.id.order_shop);
        mShopPhone = view.findViewById(R.id.order_shop_phone);

        mShopAddress.setText(mShopDetails.getFormattedAddress());
        mShopPhone.setText(mShopDetails.getPhoneNumber());

        mShopAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse(
                        "geo:" + mShopDetails.getLatitude() +
                        "," + mShopDetails.getLongitude() +
                        "?q=" + mShopDetails.getLatitude() +
                        "," + mShopDetails.getLongitude() +
                        "(" + mShopName + ")"
                );
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        mShopPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPhone = new Intent(Intent.ACTION_DIAL);
                intentPhone.setData(Uri.parse("tel:"+mShopDetails.getPhoneNumber()));
                startActivity(intentPhone);
            }
        });
    }

    private void getShopAddress() {
        mShopName = CartSingleton.getInstance().getShop().getName();
        mShopDetails = CartSingleton.getInstance().getShop().getShopDetails();

        showShopDetails();
    }

    private void showOrderListRecycler(){
//        set RecyclerView
        mRecyclerView = view.findViewById(R.id.order_item_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL));
//        set Adapter
        ArrayList<OrderItemObject> itemList = CartSingleton.getInstance().getCartItemList();
        mOrderItemAdapter = new OrderItemAdapter(mContext, itemList);
        mOrderItemAdapter.setCartChangeCallbackListener(this);
        mRecyclerView.setAdapter(mOrderItemAdapter);

//        TODO: if cart is empty, prompt user to search for store

    }

    private void setHeaderCartNumber(int number) {
        String strYourOrder = "(" + number + ")";
        mOrderTitle.setText(strYourOrder);
    }

    @Override
    public void onAddOrRemoveItem(int number) {
        setHeaderCartNumber(number);
        if (number == 0) {
            mShopPhone.setText("");
            mShopPhone.setEnabled(false);
            mShopAddress.setText("");
            mShopAddress.setEnabled(false);
            hideContinueButton();
            CartSingleton.getInstance().resetCart();
        }
    }

    private void setContinueButton() {
        mContinueButton = view.findViewById(R.id.order_pay);

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIMainActivity.inflateFragment(getString(R.string.fragment_confirm_order), "");
            }
        });


        if (CartSingleton.getInstance().getOrderSize() > 0) {
            showContinueButton();
        }
    }

    private void showContinueButton() {
        mContinueButton.setVisibility(View.VISIBLE);
    }

    private void hideContinueButton() {
        mContinueButton.setVisibility(View.GONE);
    }
}
