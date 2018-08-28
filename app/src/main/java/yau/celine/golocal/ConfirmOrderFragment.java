package yau.celine.golocal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.app.ConfirmOrderCalculator;
import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.adapters.ConfirmOrderItemAdapter;
import yau.celine.golocal.utils.interfaces.CartChangeCallback;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.objects.ItemObject;
import yau.celine.golocal.utils.objects.OrderItemObject;
import yau.celine.golocal.utils.objects.ShopDetails;
import yau.celine.golocal.utils.objects.ShopObject;

public class ConfirmOrderFragment extends Fragment {
    private static final String TAG="ConfirmOrderFragment";

    private IMainActivity mIMainActivity;
    private Context mContext;
    private View view;

    private RecyclerView mRecyclerView;
    private ConfirmOrderItemAdapter mConfirmOrderAdapter;

    private TextView totalAmount;
    private FloatingActionButton paymentButton;

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
//        disable drawer
        mIMainActivity.lockDrawerBottom();

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
//            display header info
            showHeaderInfo();
//            display order
            showOrder();
//            TODO: display tax info

//            TODO: fix total info
            showTotalInfo();

//            clicking on next button
            setConfirmPaymentButton();
        }


        return view;
    }

    public void showHeaderInfo() {
        final ShopObject shop = CartSingleton.getInstance().getShop();
//        Shop Name
        TextView shopName = view.findViewById(R.id.confirm_store_name);
        shopName.setText(shop.getName());
//        Shop Address
        TextView shopAddress = view.findViewById(R.id.confirm_store_address);
        shopAddress.setText(shop.getFormattedAddress());
        shopAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopDetails shopDetails = shop.getShopDetails();
                Uri gmmIntentUri = Uri.parse(
                        "geo:" + shopDetails.getLatitude() +
                                "," + shopDetails.getLongitude() +
                                "?q=" + shopDetails.getLatitude() +
                                "," + shopDetails.getLongitude() +
                                "(" + shop.getName() + ")"
                );
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
//        Phone Number
        TextView shopPhone = view.findViewById(R.id.confirm_store_phone);
        shopPhone.setText(shop.getPhoneNumber());
        shopPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPhone = new Intent(Intent.ACTION_DIAL);
                intentPhone.setData(Uri.parse("tel:"+shop.getPhoneNumber()));
                startActivity(intentPhone);
            }
        });
    }

    private void showOrder() {
        ArrayList<OrderItemObject> orderItems = CartSingleton.getInstance().getCartItemList();
        if (orderItems.size() > 0) {
//        set recyclerview
            mRecyclerView = view.findViewById(R.id.confirm_items);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(
                new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL) {
//                    TODO: remove divider on last item, code bellow does not work

                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);

//                        hide divider for last child
                        if (position == parent.getAdapter().getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                });
//        set adapter
            mConfirmOrderAdapter = new ConfirmOrderItemAdapter(mContext, orderItems);
            mRecyclerView.setAdapter(mConfirmOrderAdapter);
        }
    }

    private void showTotalInfo() {
        totalAmount = view.findViewById(R.id.confirm_total);
        ConfirmOrderCalculator calculator = new ConfirmOrderCalculator();

        totalAmount.setText(calculator.getFormattedTotal());
    }

    private void setConfirmPaymentButton() {
        paymentButton = view.findViewById(R.id.confirm_order_pay);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOrderToAPI(getCartJSONParams());
            }
        });
    }

    private JSONObject getCartJSONParams() {
        JSONObject params = new JSONObject();

        CartSingleton cart = CartSingleton.getInstance();

        ShopObject shop = cart.getShop();
        try {
            params.put("shop", shop.getId());

//            put items in json array
            JSONArray orderitem_set = new JSONArray();

            ArrayList<OrderItemObject> cartItemList = cart.getCartItemList();
            for (int i = 0; i < cartItemList.size(); i++){
                JSONObject itemJson = new JSONObject();
                OrderItemObject currentOrderItem = cartItemList.get(i);

                itemJson.put("item", currentOrderItem.getId());
                itemJson.put("customization", currentOrderItem.getRequests());

                orderitem_set.put(itemJson);
            }

            params.put("orderitem_set", orderitem_set);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return params;
    }

    private void sendOrderToAPI(JSONObject cartJson) {
        JsonObjectRequest orderJsonRequest = new JsonObjectRequest(Request.Method.POST,
                URLs.URL_ORDER, cartJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                if (!response.has("error")) {
                    CartSingleton.getInstance().resetCart();
                    ((CartChangeCallback)mContext).onAddOrRemoveItem(0);

                    ArrayList objectsPassedToFragment = new ArrayList();

                    try {
                        JSONArray itemsOrdered = response.getJSONArray("orderitem_set");

                        for (int i = 0; i < itemsOrdered.length(); i++){
                        }

                        objectsPassedToFragment.add(response.getInt("id"));
                        objectsPassedToFragment.add(response.getString("created_at"));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    mIMainActivity.inflateFragment(getString(R.string.fragment_placed_order),
                            objectsPassedToFragment);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+ error.getMessage());
                Toast.makeText(mContext, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getHeaders();

                return headers;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(orderJsonRequest);
    }
}
