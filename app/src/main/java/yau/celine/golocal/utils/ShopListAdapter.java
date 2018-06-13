package yau.celine.golocal.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import yau.celine.golocal.R;
import yau.celine.golocal.app.AppController;

/**
 * Created by Celine on 2018-06-11.
 */

public class ShopListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ShopItem> shopItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ShopListAdapter(Activity activity, List<ShopItem> shopItems) {
        this.activity = activity;
        this.shopItems = shopItems;
    }

    @Override
    public int getCount() {
        return shopItems.size();
    }

    @Override
    public Object getItem(int position) {
        return shopItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return shopItems.get(position).getId();
//        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shop_row, null);
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        NetworkImageView thumbnail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);

//        get shop data for row
        ShopItem shop = shopItems.get(position);

//        Thumbnail image
        thumbnail.setImageUrl(shop.getThumbnailUrl(), imageLoader);

//        Name
        name.setText(shop.getName());

//        Rating
        rating.setText("Rating: "+String.valueOf(shop.getRating()));

//        Address
        address.setText(shop.getAddress());

        return convertView;
    }
}
