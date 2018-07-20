package yau.celine.golocal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.interfaces.CartChangeCallback;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.ItemNotBelongToShopException;
import yau.celine.golocal.utils.objects.ItemObject;
import yau.celine.golocal.utils.objects.OrderItemObject;
import yau.celine.golocal.utils.objects.ShopObject;

public class ItemFragment extends Fragment {
    private static final String TAG = "ItemFragment";

    private View view;

    private IMainActivity mIMainActivity;
    private Context mContext;
    private ItemObject item;
    private ShopObject shop;

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;

    private FloatingActionButton btnAddItem;
    private EditText itemRequest;

    private ToggleButton favoriteButton;

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
        if (bundle != null){
            try {
                ArrayList<Parcelable> objects = bundle.getParcelableArrayList(getString(R.string.intent_message));
                shop = (ShopObject) objects.get(0);
                item = (ItemObject) objects.get(1);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIMainActivity.lockDrawer();

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_item_detail, container, false);

//            show shop name
            showShopDetails();
//            display details
            showItemDetails();
//            set add item button
            setBtnAddItem();
//            set favorite item button
            setFavoriteButton();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mIMainActivity.unlockDrawer();
    }

    private void setBtnAddItem() {
        btnAddItem = view.findViewById(R.id.add_to_order);
        itemRequest = view.findViewById(R.id.item_request);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Selected "+item.getName());

//                cast to OrderItemObject
                OrderItemObject orderItem = new OrderItemObject(item);
                if (itemRequest.getText() != null) {
                    orderItem.setRequests(itemRequest.getText().toString());
                }
                addToCart(orderItem);
            }
        });
    }

    private void addToCart(OrderItemObject item) {
        CartSingleton cart = CartSingleton.getInstance();

        try {
            if (!cart.addMenuItem(item)) {
                cart.addFirstItem(item, shop);
            }
            animateAddToCart();
        } catch (ItemNotBelongToShopException ex) {
            Toast.makeText(mContext, getString(R.string.cannot_add_to_cart),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void animateAddToCart() {
        Toast.makeText(mContext, getString(R.string.added_to_cart),
                Toast.LENGTH_SHORT).show();
        ((CartChangeCallback)mContext).onAddOrRemoveItem(
                CartSingleton.getInstance().getOrderSize()
        );
    }

    private void showItemDetails(){
        itemImage = view.findViewById(R.id.item_image);
        itemName = view.findViewById(R.id.item_name);
        itemDescription = view.findViewById(R.id.item_description);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
                .load(item.getImageUrl())
                .apply(options)
                .into(itemImage);

        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
    }

    private void showShopDetails(){
        TextView shopName = view.findViewById(R.id.item_shop);
        shopName.setText(shop.getName());
    }

    private void setFavoriteButton() {
        favoriteButton = view.findViewById(R.id.item_favorite);

        favoriteButton.setChecked(item.isUserFav());
        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                hitFavUrl();
            }
        });
    }

    private void hitFavUrl() {
        String favUrl = URLs.toggleFavItem(item.getId());
        JsonObjectRequest favItemReq = new JsonObjectRequest(Request.Method.GET, favUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                if (response.has("error")) {
                    Toast.makeText(mContext, "Sorry something went wrong", Toast.LENGTH_SHORT);
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
                HashMap<String, String> headers = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getHeaders();

                return headers;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(favItemReq);
    }

    private Drawable getDrawable(@DrawableRes int drawable) {
        return ContextCompat.getDrawable(mContext, drawable);
    }
}
