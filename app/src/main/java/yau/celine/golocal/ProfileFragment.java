package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.adapters.CategoryDataAdapter;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.utils.interfaces.OnItemClickListener;
import yau.celine.golocal.utils.objects.CategoryDataModel;
import yau.celine.golocal.utils.objects.ItemObject;
import yau.celine.golocal.utils.objects.ShopObject;
import yau.celine.golocal.utils.objects.User;

public class ProfileFragment extends Fragment implements OnItemClickListener {
    private static final String TAG = "ProfileFragment";

    private View view;

    private RelativeLayout loadingPanel;

    private IMainActivity mIMainActivity;
    private Context mContext;

    private RecyclerView mCategoryRecyclerView;
    private CategoryDataAdapter mCategoryAdapter;
    private ArrayList<CategoryDataModel> mCategoryDataModelList;

    private ShopObject mShopObject;

    private TextView mUserFavCount;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mIMainActivity = (IMainActivity) getActivity();
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIMainActivity.setToolbarTitle(getTag());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getContext()).logout();
            }
        });

//        Want to update user info and favorites each time view loads
        getUserInfo();
        getUserFavorites();

        return view;
    }

    private void getUserInfo(){
        User currentUser = SharedPrefManager.getInstance(getContext()).getUser();

        TextView mName = view.findViewById(R.id.profile_name);
        ImageView mImage = view.findViewById(R.id.profile_thumbnail);

        mUserFavCount = view.findViewById(R.id.profile_fav_count);

        mName.setText(currentUser.getFullName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getContext())
                .load(currentUser.getProfilePhotoUrl())
                .apply(options)
                .into(mImage);
    }

    private void getUserFavorites() {
        mCategoryDataModelList = new ArrayList<>();
//        recyclerview
        mCategoryRecyclerView = view.findViewById(R.id.profile_favorites);
        mCategoryRecyclerView.setHasFixedSize(true);
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mCategoryRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL));
//        set adapter for mCategoryRecyclerView
        mCategoryAdapter = new CategoryDataAdapter(mContext, mCategoryDataModelList);
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setListener(this);

        //        find loadingPanel
        loadingPanel = view.findViewById(R.id.loadingPanel);
//        request from website
        getFavObjectResponse();
    }

    private void getFavObjectResponse(){
        showProgress();
        JsonObjectRequest favoriteReq = new JsonObjectRequest(Request.Method.GET, URLs.URL_CURRENT_USER_FAV,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                hideProgress();

                if(!response.has("error")){
                    try{
//                        set new category with name favorite
                        CategoryDataModel dm = new CategoryDataModel();
                        dm.setCategoryName(getString(R.string.favorites));
                        JSONArray items = response.getJSONArray("favorites");
                        ArrayList<ItemObject> itemList = new ArrayList<>();

                        mUserFavCount.setText(String.valueOf(items.length()));
                        for (int i = 0; i < items.length(); i++){
//                            parse item values
                            JSONObject item = items.getJSONObject(i);
                            ItemObject itemObject = new ItemObject(item);
                            itemList.add(itemObject);
                        }
//                        add favorite items to category model
                        dm.setItemsInCategory(itemList);
//                        add category to data model list
                        mCategoryDataModelList.add(dm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCategoryAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
                hideProgress();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getHeaders();

                return headers;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(favoriteReq);
    }

    @Override
    public void onFragmentClickPassParcelable(Parcelable item) {
        openItemFragment((ItemObject)item);
    }

    public void openItemFragment(final ItemObject item){
        showProgress();
        String shopUrl = URLs.getShopUrlWithoutItems(item.getShopId());
        JsonObjectRequest shopReq = new JsonObjectRequest(Request.Method.GET, shopUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                hideProgress();

                if (!response.has("error")) {
                    try {
                        mShopObject = new ShopObject(response);
                        ArrayList objectsPassedToFragment = new ArrayList();
                        objectsPassedToFragment.add(mShopObject);
                        objectsPassedToFragment.add(item);
                        mIMainActivity.inflateFragment(getString(R.string.fragment_item_details), objectsPassedToFragment);
                    } catch (JSONException ex) {
                        hideProgress();
                        ex.printStackTrace();
                        Toast.makeText(mContext, "Something went wrong, please try again", Toast.LENGTH_SHORT);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
                hideProgress();
                Toast.makeText(mContext, "Something went wrong, please try again", Toast.LENGTH_SHORT);
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

    private void showProgress(){
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        loadingPanel.setVisibility(View.GONE);
    }
}
