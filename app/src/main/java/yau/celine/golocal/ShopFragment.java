package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.CategoryDataAdapter;
import yau.celine.golocal.utils.CategoryDataModel;
import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.MenuItem;
import yau.celine.golocal.utils.OnItemClickListener;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.URLs;

/**
 * Created by Celine on 2018-06-13.
 */

public class ShopFragment extends Fragment implements OnItemClickListener {
    private static final String TAG = "ShopFragment";

    private IMainActivity mIMainActivity;

    private View view;

    private int shopId = -1;

    private RelativeLayout loadingPanel;

    private ImageView shopCoverImage;
    private TextView shopTextViewName;
    private TextView shopTextViewDescription;

    private RecyclerView mCategoryRecyclerView;
    private CategoryDataAdapter mCategoryAdapter;

    private ArrayList<CategoryDataModel> menuCategoryList;


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
        mIMainActivity = (IMainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shop, container, false);

//        Set shop details
            getShopDetails();
        }
        return view;
    }

    private void getShopDetails(){
//        connect front end elements to variables
        shopTextViewName = view.findViewById(R.id.shop_name);
        shopTextViewDescription = view.findViewById(R.id.shop_description);
        shopCoverImage = view.findViewById(R.id.shop_cover_image);

        menuCategoryList = new ArrayList<>();
//        set recycler view for categories
        mCategoryRecyclerView = view.findViewById(R.id.recycler_view_category_list);
        mCategoryRecyclerView.setHasFixedSize(true);
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCategoryRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
//        set adapter for mCategoryRecyclerView
        mCategoryAdapter = new CategoryDataAdapter(getContext(), menuCategoryList);
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setListener(this);
//        find loadingPanel
        loadingPanel = view.findViewById(R.id.loadingPanel);


        if(shopId >= 0){
            showProgress();
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
                        hideProgress();

                        if (!response.has("error")){
                            try {
                                shopTextViewName.setText(response.getString("name"));
                                shopTextViewDescription.setText(response.getString("description"));
//                                load cover image
                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                                Glide.with(getContext())
                                        .load(response.getString("cover_image"))
                                        .apply(options)
                                        .into(shopCoverImage);
                                JSONArray categorySet = response.getJSONArray("category_set");
                                if (categorySet.length() > 0) {
//                                get categories
                                    for (int c = 0; c < categorySet.length(); c++) {
                                        JSONObject category = categorySet.getJSONObject(c);
//                                    parse category values
                                        CategoryDataModel dm = new CategoryDataModel();
                                        dm.setCategoryName(category.getString("name"));

//                                    get items within category
                                        JSONArray items = category.getJSONArray("item_set");
                                        ArrayList<MenuItem> itemList = new ArrayList<>();
                                        for (int i = 0; i < items.length(); i++) {
//                                        parse item values
                                            JSONObject item = items.getJSONObject(i);
                                            MenuItem menuItem = new MenuItem();
                                            menuItem.setName(item.getString("name"));
                                            menuItem.setDescription(item.getString("description"));
                                            menuItem.setPrice(item.getDouble("price"));
                                            menuItem.setImageUrl(item.getString("photo"));
                                            menuItem.setShopId(shopId);

                                            itemList.add(menuItem);
                                        }

                                        dm.setItemsInCategory(itemList);
                                        menuCategoryList.add(dm);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        mCategoryAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
                hideProgress();
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(shopReq);
    }

    @Override
    public void onFragmentClick(Parcelable object) {
        mIMainActivity.inflateFragment(getString(R.string.fragment_item_details), object);
    }

    private void showProgress(){
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        loadingPanel.setVisibility(View.GONE);
    }
}
