package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.objects.OrderObject;

public class OrderDetailFragment extends Fragment {
    private static final String TAG = "OrderDetailFragment";

    private View view;
    private IMainActivity mIMainActivity;
    private Context mContext;

    private int orderId;

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
        if (bundle != null) {
            orderId = Integer.parseInt(bundle.getString(getString(R.string.intent_message)));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_detail, container, false);

//            get order details
            getOrderDetails();
        }

        return view;
    }

    private void getOrderDetails() {
        String orderUrl = URLs.getOrderUrl(orderId);
        JsonObjectRequest orderReq = new JsonObjectRequest(Request.Method.GET, orderUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                if (!response.has("error")) {
                    try {
                        OrderObject order = new OrderObject(response);
                        order.setOrderItemSet(response.getJSONArray("orderitem_set"));

                        setOrderDetailView(order);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
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
    }

    private void setOrderDetailView(OrderObject order) {
//        TODO: setOrderDetailView
    }
}
