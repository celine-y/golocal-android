package yau.celine.golocal.utils.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import yau.celine.golocal.R;
import yau.celine.golocal.utils.objects.CategoryDataModel;
import yau.celine.golocal.utils.interfaces.OnItemClickListener;
import yau.celine.golocal.utils.interfaces.OnShopClickListener;
import yau.celine.golocal.utils.objects.ShopObject;

public class CategoryDataAdapter extends RecyclerView.Adapter<CategoryDataAdapter.CategoryRowHolder>
implements OnShopClickListener {

    private ShopObject mShop;

    private ArrayList<CategoryDataModel> categoryDataList;
    private Context mContext;
    private OnItemClickListener mListener;
    private ItemListDataAdapter mItemListAdapter;

    public CategoryDataAdapter(Context context, ArrayList<CategoryDataModel> categoryDataList) {
        this.categoryDataList = categoryDataList;
        this.mContext = context;
    }

    public void setListener (OnItemClickListener listener) {
        mListener = listener;
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


        mItemListAdapter = new ItemListDataAdapter(mContext, singleMenuItems);

        mItemListAdapter.setListener(mListener);

        categoryRowHolder.recycler_view_item_list.setHasFixedSize(true);
        categoryRowHolder.recycler_view_item_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        categoryRowHolder.recycler_view_item_list.setAdapter(mItemListAdapter);
    }

    @Override
    public int getItemCount() {
        return (null != categoryDataList ? categoryDataList.size() : 0);
    }

    @Override
    public void onFragmentClick(int position) {

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
