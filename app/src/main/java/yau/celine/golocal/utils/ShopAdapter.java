package yau.celine.golocal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import yau.celine.golocal.R;
import yau.celine.golocal.app.AppController;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private Context mContext;
    private ArrayList<ShopItem> mShopList;
    private OnShopClickListener mListener;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public void setListener (OnShopClickListener listener) {
        mListener = listener;
    }

    public ShopAdapter (Context context, ArrayList<ShopItem> shopList) {
        mContext = context;
        mShopList = shopList;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.shop_row, parent, false);
        return new ShopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopItem shop = mShopList.get(position);

        holder.mTextViewName.setText(shop.getName());
        holder.mTextViewRating.setText("Rating: "+String.valueOf(shop.getRating()));
        holder.mTextViewAddress.setText(shop.getAddress());
        holder.mImageView.setImageUrl(shop.getThumbnailUrl(), imageLoader);
    }

    @Override
    public int getItemCount() {
        return mShopList.size();
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder{
        public NetworkImageView mImageView;
        public TextView mTextViewName;
        public TextView mTextViewRating;
        public TextView mTextViewAddress;

        public ShopViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.thumbnail);
            mTextViewName = itemView.findViewById(R.id.name);
            mTextViewRating = itemView.findViewById(R.id.rating);
            mTextViewAddress = itemView.findViewById(R.id.address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onFragmentClick(position);
                        }
                    }
                }
            });
        }
    }
}
