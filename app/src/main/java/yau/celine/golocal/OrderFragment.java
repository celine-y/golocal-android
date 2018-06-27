package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.OrderItemAdapter;
import yau.celine.golocal.utils.OrderMenuItem;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.URLs;

public class OrderFragment extends Fragment {
    private static final String TAG = "OrderFragment";

    private IMainActivity mIMainActivity;
    private View view;

    private RecyclerView mRecyclerView;
    private OrderItemAdapter mOrderItemAdapter;

    private TextView mOrderTitle;
    private TextView mShopAddress;

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
            view = inflater.inflate(R.layout.fragment_order, container, false);

//            display header info
            showHeaderInfo();
//            display order
            showOrderListRecycler();
        }

        return view;
    }

    private void showHeaderInfo() {
//        display order number
        mOrderTitle = view.findViewById(R.id.order_num_items);
        String strYourOrder = "(" + CartSingleton.getInstance().getOrderSize() + ")";
        mOrderTitle.setText(strYourOrder);

//        display shop info
        if (CartSingleton.getInstance().getShopId() >= 0) {
            showShopAddress();
        }
    }

    private void showShopAddress() {
        mShopAddress = view.findViewById(R.id.order_shop);

        String shopUrl = URLs.getShopLocationUrl(CartSingleton.getInstance().getShopId());
        JsonObjectRequest shopLocReq = new JsonObjectRequest(Request.Method.GET, shopUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                if (!response.has("error")) {
                    try {
                        mShopAddress.setText(response.getString("location"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                String token = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getKeyToken();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Token "+ token);

                return headers;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(shopLocReq);
    }

    private void showOrderListRecycler(){
//        set RecyclerView
        mRecyclerView = view.findViewById(R.id.order_item_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
//        set Adapter
        ArrayList<OrderMenuItem> itemList = CartSingleton.getInstance().getCartItemList();
        mOrderItemAdapter = new OrderItemAdapter(getContext(), itemList);
        mRecyclerView.setAdapter(mOrderItemAdapter);

    }
}
