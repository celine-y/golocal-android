package yau.celine.golocal.utils.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import yau.celine.golocal.R;
import yau.celine.golocal.utils.interfaces.OnShopClickListener;
import yau.celine.golocal.utils.objects.ShopItem;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private Context mContext;
    private ArrayList<ShopItem> mShopList;
    private OnShopClickListener mListener;

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
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_shop, parent, false);
        return new ShopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopItem shop = mShopList.get(position);

        holder.mTextViewName.setText(shop.getName());
        holder.mTextViewRating.setText("Rating: "+String.valueOf(shop.getRating()));
        holder.mTextViewAddress.setText(shop.getRawAddress());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
                .load(shop.getThumbnailUrl())
                .apply(options)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mShopList.size();
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
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