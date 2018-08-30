package yau.celine.golocal.utils.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import yau.celine.golocal.R;
import yau.celine.golocal.utils.interfaces.OnClickListener;
import yau.celine.golocal.utils.objects.OrderObject;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context mContext;
    private ArrayList<OrderObject> mOrderList;
    private OnClickListener mListener;

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }

    public OrderAdapter (Context context, ArrayList<OrderObject> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_history_order,
                parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderObject order = mOrderList.get(position);

        holder.mOrderId.setText(String.valueOf(order.getId()));
        holder.mCreatedDate.setText(order.getCreatedDateString());
        holder.mShop.setText(order.getShop().getName());
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView mCreatedDate;
        public TextView mShop;
        public TextView mOrderId;
        public TextView mCompleted;

        public OrderViewHolder(View orderView) {
            super(orderView);

            mOrderId = orderView.findViewById(R.id.order_id);
            mCreatedDate = orderView.findViewById(R.id.order_created);
            mShop = orderView.findViewById(R.id.order_shop);

            orderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null){
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
