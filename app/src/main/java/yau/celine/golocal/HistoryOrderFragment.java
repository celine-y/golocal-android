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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.adapters.OrderAdapter;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.interfaces.OnClickListener;
import yau.celine.golocal.utils.objects.OrderObject;

public class HistoryOrderFragment extends Fragment implements
        OnClickListener {

    private static final String TAG = "HistoryOrderFragment";

    private IMainActivity mIMainActivity;
    private Context mContext;

    private View view;

    private RecyclerView mOrderRecyclerView;

    private ArrayList<OrderObject> mOrderList = new ArrayList<>();
    private OrderAdapter mOrderAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_history_order, container, false);
//            TODO: get previous orders
            attachPreviousOrders();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void attachPreviousOrders(){
        if (mOrderRecyclerView == null) {
//            set recycler view
            mOrderRecyclerView = view.findViewById(R.id.previous_orders_recyclerview);
            mOrderRecyclerView.setHasFixedSize(true);
            mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mOrderRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                    DividerItemDecoration.VERTICAL));

//            set adapter
            mOrderAdapter = new OrderAdapter(getContext(), mOrderList);
            mOrderRecyclerView.setAdapter(mOrderAdapter);
//            set on click for order item
            mOrderAdapter.setListener(this);
        }

        getPreviousOrders();
    }

    private void getPreviousOrders() {
        String orderUrl = URLs.URL_ORDER_LIST;
        JsonArrayRequest orderReq = new JsonArrayRequest(orderUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
//                        TODO: hide progress
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonOrder = response.getJSONObject(i);
                                OrderObject order = new OrderObject(jsonOrder);
                                mOrderList.add(order);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }

//                        notify list adapter about data changes
                        mOrderAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+ error.getMessage());
//                TODO: hide progress
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getHeaders();

                return headers;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(orderReq);
    }

    @Override
    public void onFragmentClick(int position) {
        String orderId = String.valueOf(mOrderList.get(position).getId());

        mIMainActivity.inflateFragment(getString(R.string.fragment_order_details), orderId);
    }
}
