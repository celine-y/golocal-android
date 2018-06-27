package yau.celine.golocal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import yau.celine.golocal.R;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder>{

    private Context mContext;
    private ArrayList<OrderMenuItem> mItemList;

    public OrderItemAdapter(Context context, ArrayList<OrderMenuItem> itemList) {
        mContext = context;
        mItemList = itemList;
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_orderitem, parent, false);
        return new OrderItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, final int position) {
        OrderMenuItem item = mItemList.get(position);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        String priceText = numberFormat.format(item.getPrice());

        holder.mOrderItemName.setText(item.getName());
        holder.mOrderItemRequest.setText(item.getRequests());
        holder.mOrderItemPrice.setText(priceText);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
                .load(item.getImageUrl())
                .apply(options)
                .into(holder.mImageView);

//        Set add/remove buttons
        holder.mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemList.add(mItemList.get(position));
                notifyDataSetChanged();
            }
        });

        holder.mRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder{
        public CircleImageView mImageView;
        public TextView mOrderItemName;
        public TextView mOrderItemRequest;
        public TextView mOrderItemPrice;

        public ImageButton mAddItem;
        public ImageButton mRemoveItem;

        public OrderItemHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.orderitem_image);
            mOrderItemName = itemView.findViewById(R.id.orderitem_name);
            mOrderItemRequest = itemView.findViewById(R.id.orderitem_request);
            mOrderItemPrice = itemView.findViewById(R.id.orderitem_price);

            mAddItem = itemView.findViewById(R.id.add_item);
            mRemoveItem = itemView.findViewById(R.id.remove_item);


        }
    }
}
