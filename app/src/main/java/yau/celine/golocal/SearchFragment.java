package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;

import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.OnShopClickListener;
import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.ShopAdapter;
import yau.celine.golocal.utils.ShopItem;
import yau.celine.golocal.utils.URLs;

/**
 * Created by Celine on 2018-06-13.
 */

public class SearchFragment extends Fragment implements OnShopClickListener {
    private static final String TAG = "SearchFragment";

    private View view;

    private RelativeLayout loadingPanel;

    private IMainActivity mIMainActivity;

    private RecyclerView mRecyclerView;
    private ShopAdapter mShopAdapter;
    private ArrayList<ShopItem> mShopList = new ArrayList<>();

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
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);

            showShopListRecycler();
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showShopListRecycler() {
//        set RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
//        set Adapter
        mShopAdapter = new ShopAdapter(getContext(), mShopList);
        mRecyclerView.setAdapter(mShopAdapter);
//        set on click for shop item
        mShopAdapter.setListener(this);
//        find loadingPanel
        loadingPanel = view.findViewById(R.id.loadingPanel);

//        show progress bar
        showProgress();

//        get shops and display
        parseJSON();
    }

    private void parseJSON() {
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
                                shop.setThumbnailUrl(obj.getString("cover_image"));
//                                TODO: get shop rating, get thumbnail

//                                add shop to shop array
                                mShopList.add(shop);
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        Log.i(TAG, "size: "+mShopList.size());

//                        notify list adapter about data changes
                        mShopAdapter.notifyDataSetChanged();
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

    @Override
    public void onFragmentClick(int position) {
        String shop_id = String.valueOf(mShopList.get(position).getId());
        mIMainActivity.inflateFragment(getString(R.string.fragment_shop_details), shop_id);
    }
}
