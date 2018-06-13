package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.URLs;

/**
 * Created by Celine on 2018-06-13.
 */

public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";

    private IMainActivity mIMainActivity;

    private View view;

    private int shopId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIMainActivity.setToolbarTitle(getTag());

        Bundle bundle = this.getArguments();
        if (bundle != null){
            shopId = Integer.parseInt(bundle.getString(getString(R.string.intent_message)));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_shop, container, false);

//        Set shop details
        getShopDetails();
        return view;
    }

    private void getShopDetails(){
        if(shopId >= 0){
            setJsonShopDetails();
        }
    }

    private void setJsonShopDetails() {
//        Fetch json shop object
        String shopUrl = URLs.getShopUrl(shopId);
        JsonObjectRequest shopReq = new JsonObjectRequest(Request.Method.GET, shopUrl,
                null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

//                        TODO: display shop details
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String token = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getKeyToken();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Token "+ token);

                return headers;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(shopReq);
    }
}
