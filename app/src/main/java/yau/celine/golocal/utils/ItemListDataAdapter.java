package yau.celine.golocal.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import yau.celine.golocal.R;

public class ItemListDataAdapter extends RecyclerView.Adapter<ItemListDataAdapter.MenuItemRowHolder> {
    private static final String TAG = "ItemListDataAdapter";

    private ArrayList<MenuItem> itemsList;
    private Context mContext;

    public ItemListDataAdapter(Context context, ArrayList<MenuItem> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public MenuItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, null);
        MenuItemRowHolder mh = new MenuItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(MenuItemRowHolder holder, int i) {
        Log.d(TAG, "onBindViewHolder: called");
        MenuItem singleItem = itemsList.get(i);

        holder.itemName.setText(singleItem.getName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL);


       Glide.with(mContext)
               .load(singleItem.getImageUrl())
               .apply(options)
               .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class MenuItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemName;
        protected CircleImageView itemImage;


        public MenuItemRowHolder(View view) {
            super(view);

            this.itemName = view.findViewById(R.id.item_name);
            this.itemImage = view.findViewById(R.id.item_image);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(v.getContext(), itemName.getText(), Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

}