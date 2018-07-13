package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.utils.interfaces.CartChangeCallback;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.ItemNotBelongToShopException;
import yau.celine.golocal.utils.objects.MenuItem;
import yau.celine.golocal.utils.objects.OrderMenuItem;
import yau.celine.golocal.utils.objects.ShopItem;

public class ItemFragment extends Fragment {
    private static final String TAG = "ItemFragment";

    private View view;

    private IMainActivity mIMainActivity;
    private Context mContext;
    private MenuItem item;
    private ShopItem shop;

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;

    private FloatingActionButton btnAddItem;
    private EditText itemRequest;

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
                shop = (ShopItem) objects.get(0);
                item = (MenuItem) objects.get(1);
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

//                cast to OrderMenuItem
                OrderMenuItem orderItem = new OrderMenuItem(item);
                if (itemRequest.getText() != null)
                    orderItem.setRequests(itemRequest.getText().toString());
                try {
//                    save item to cart
                    CartSingleton.getInstance().addMenuItem(orderItem);
                    animateAddToCart();
                } catch (ItemNotBelongToShopException ex) {
                    Toast.makeText(mContext, getString(R.string.cannot_add_to_cart),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
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
}
