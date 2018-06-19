package yau.celine.golocal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.MenuItem;

public class ItemFragment extends Fragment {
    private static final String TAG = "ItemFragment";

    private View view;

    private IMainActivity mIMainActivity;
    private MenuItem item;

    private TextView itemName;

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
        }
        return view;
    }

    private void showItemDetails(){
        itemName = view.findViewById(R.id.item_name);

        itemName.setText(item.getName());
    }
}
