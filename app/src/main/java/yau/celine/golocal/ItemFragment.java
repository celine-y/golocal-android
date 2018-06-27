package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
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

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.ItemNotBelongToShopException;
import yau.celine.golocal.utils.MenuItem;
import yau.celine.golocal.utils.OrderMenuItem;

public class ItemFragment extends Fragment {
    private static final String TAG = "ItemFragment";

    private View view;

    private IMainActivity mIMainActivity;
    private MenuItem item;

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;

    private FloatingActionButton btnAddItem;
    private EditText itemRequest;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIMainActivity.setToolbarTitle(getTag());

        Bundle bundle = this.getArguments();
        if (bundle != null){
            item = bundle.getParcelable(getString(R.string.intent_message));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_item_detail, container, false);

//            display details
            showItemDetails();

//            set add item button
            setBtnAddItem();

        }
        return view;
    }

    private void setBtnAddItem() {
        btnAddItem = view.findViewById(R.id.add_to_order);
        itemRequest = view.findViewById(R.id.item_request);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Selected "+item.getName());
//                get request message
//                cast to OrderMenuItem
                OrderMenuItem orderItem = new OrderMenuItem(item);
                if (itemRequest.getText() != null)
                    orderItem.setRequests(itemRequest.getText().toString());
//                save item to cart
                try {
                    CartSingleton.getInstance().addMenuItem(orderItem);
                } catch (ItemNotBelongToShopException ex) {
                    Toast.makeText(getContext(),
                            getString(R.string.item_not_belong_shop),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
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
        Glide.with(getContext())
                .load(item.getImageUrl())
                .apply(options)
                .into(itemImage);

        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
    }
}
