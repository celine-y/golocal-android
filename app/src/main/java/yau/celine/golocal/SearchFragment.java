package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.ShopItem;
import yau.celine.golocal.utils.ShopListAdapter;
import yau.celine.golocal.utils.URLs;

/**
 * Created by Celine on 2018-06-13.
 */

public class SearchFragment extends Fragment {
    private View view;

    private static final String TAG = "SearchFragment";

    private RelativeLayout loadingPanel;
    private List<ShopItem> shopList = new ArrayList<>();
    private ListView mListView;
    private ShopListAdapter adapter;

    private IMainActivity mIMainActivity;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        showShopList();
        setOnClickEvents();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showShopList() {
        mListView = (ListView) view.findViewById(R.id.shop_list);
        adapter = new ShopListAdapter(getActivity(), shopList);
        mListView.setAdapter(adapter);
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

//        show progress bar
        showProgress();

//        get shops and display
        fetchShops();

//        handle shop onclick event
        setOnClickEvents();
    }

    private void setOnClickEvents(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shop_id = String.valueOf(shopList.get(position).getId());
                mIMainActivity.inflateFragment(getString(R.string.fragment_shop_details), shop_id);
            }
        });
    }

    private void fetchShops(){
        //        Fetch json array
        JsonArrayRequest shopReq = new JsonArrayRequest(URLs.URL_SHOP_LIST,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hideProgress();

//                        Parse json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                ShopItem shop = new ShopItem();
                                shop.setId(obj.getInt("id"));
                                shop.setName(obj.getString("name"));
                                shop.setAddress(obj.getString("location"));
//                                TODO: get shop rating, get thumbnail

//                                add shop to shop array
                                shopList.add(shop);
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        Log.i(TAG, "size: "+shopList.size());

//                        notify list adapter about data changes
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(TAG, "Error : "+error.getMessage());
                hideProgress();
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

    private void showProgress(){
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        loadingPanel.setVisibility(View.GONE);
    }

}
