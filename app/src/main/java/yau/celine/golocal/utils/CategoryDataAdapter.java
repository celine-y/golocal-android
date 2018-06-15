package yau.celine.golocal.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import yau.celine.golocal.R;

public class CategoryDataAdapter extends RecyclerView.Adapter<CategoryDataAdapter.CategoryRowHolder> {

    private ArrayList<CategoryDataModel> categoryDataList;
    private Context mContext;

    public CategoryDataAdapter(Context context, ArrayList<CategoryDataModel> categoryDataList) {
        this.categoryDataList = categoryDataList;
        this.mContext = context;
    }

    @Override
    public CategoryRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_section, null);
        CategoryRowHolder mh = new CategoryRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(CategoryRowHolder categoryRowHolder, int i) {

        final String sectionName = categoryDataList.get(i).getCategoryName();

        ArrayList singleMenuItems = categoryDataList.get(i).getAllItemsInCategory();

        categoryRowHolder.categoryName.setText(sectionName);

        ItemListDataAdapter itemListDataAdapter = new ItemListDataAdapter(mContext, singleMenuItems);

        categoryRowHolder.recycler_view_item_list.setHasFixedSize(true);
        categoryRowHolder.recycler_view_item_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        categoryRowHolder.recycler_view_item_list.setAdapter(itemListDataAdapter);

       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != categoryDataList ? categoryDataList.size() : 0);
    }

    public class CategoryRowHolder extends RecyclerView.ViewHolder {

        protected TextView categoryName;
        protected RecyclerView recycler_view_item_list;

        public CategoryRowHolder(View view) {
            super(view);

            this.categoryName = view.findViewById(R.id.category_name);
            this.recycler_view_item_list = view.findViewById(R.id.recycler_view_item_list);


        }

    }

}
