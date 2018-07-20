package yau.celine.golocal.utils.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import yau.celine.golocal.R;
import yau.celine.golocal.utils.objects.OrderItemObject;

public class ConfirmOrderItemAdapter extends RecyclerView.Adapter<ConfirmOrderItemAdapter.ConfirmOrderItemHolder> {
    private ArrayList<OrderItemObject> mOrderItemList;
    private Context mContext;

    public ConfirmOrderItemAdapter(Context context, ArrayList<OrderItemObject> items) {
        mContext = context;
        mOrderItemList = items;
    }

    @NonNull
    @Override
    public ConfirmOrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(mContext).inflate(R.layout.layout_confirm_orderitem,
               parent, false);
       return new ConfirmOrderItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmOrderItemHolder holder, int position) {
        OrderItemObject item = mOrderItemList.get(position);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        holder.mConfirmItemName.setText(item.getName());
        holder.mConfirmItemRequest.setText(item.getRequests());
        String priceString = numberFormat.format(item.getPrice());
        holder.mConfirmItemPrice.setText(priceString);
    }

    @Override
    public int getItemCount() {
        return mOrderItemList.size();
    }

    public class ConfirmOrderItemHolder extends RecyclerView.ViewHolder {
        public TextView mConfirmItemName;
        public TextView mConfirmItemRequest;
        public TextView mConfirmItemPrice;

        public ConfirmOrderItemHolder(View itemView) {
            super(itemView);
            mConfirmItemName = itemView.findViewById(R.id.confirm_item_name);
            mConfirmItemRequest = itemView.findViewById(R.id.confirm_item_requests);
            mConfirmItemPrice = itemView.findViewById(R.id.confirm_item_price);
        }
    }
}
