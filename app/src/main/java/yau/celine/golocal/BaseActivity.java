package yau.celine.golocal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.User;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainActivity {
    private static final String TAG = "BaseActivity";

    private Toolbar mToolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View headerView;
    private TextView profileName;
    private TextView profileFavCount;
    private TextView profilePhotoCount;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        check if user is not logged in
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        setContentView(R.layout.activity_base);

//        Set up toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


//        Set up header
        headerView = navigationView.getHeaderView(0);
        setupHeader();


        if (savedInstanceState == null) {
            init();
            navigationView.setCheckedItem(R.id.nav_search);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                SearchFragment searchFragment = new SearchFragment();
                doFragmentTransaction(searchFragment, getString(R.string.fragment_search), false, "");
                navigationView.setCheckedItem(R.id.nav_search);
                break;
            case R.id.nav_current_order:
                OrderFragment orderFragment = new OrderFragment();
                doFragmentTransaction(orderFragment, getString(R.string.fragment_current_order), false, "");
                Log.d(TAG, CartSingleton.getInstance().getCartItemList().toString());
                navigationView.setCheckedItem(R.id.nav_current_order);
                break;
            case R.id.nav_history:
//                TODO: link to history fragment
                navigationView.setCheckedItem(R.id.nav_history);
                break;
            case R.id.nav_rewards:
//                TODO: link to rewards fragment
                navigationView.setCheckedItem(R.id.nav_rewards);
                break;
            case R.id.nav_payment:
//                TODO: link to payment fragment
                navigationView.setCheckedItem(R.id.nav_payment);
                break;
            case R.id.nav_profile:
                openProfileFragment();
            case R.id.nav_settings:
//                TODO: link to settings fragment
                navigationView.setCheckedItem(R.id.nav_settings);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(!message.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.intent_message), message);
            fragment.setArguments(bundle);
        }

        transaction.replace(R.id.fragment_container, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, Parcelable parcelable){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.intent_message), parcelable);
        fragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void init(){
        SearchFragment fragment = new SearchFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_search), false, "");
    }

    private void setupHeader(){
//        header onclick
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileFragment();
            }
        });
//        set icon next to profile name
        profileName = headerView.findViewById(R.id.profile_name);
        profileName.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_chevron_right, 0);
//        set profile details (name, favs, photos, etc)
        User currentUser = SharedPrefManager.getInstance(this).getUser();
        profileName.setText(currentUser.getFullName());
    }

    private void openProfileFragment(){
        ProfileFragment fragment = new ProfileFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_profile), true, "");
        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    public void setToolbarTitle(String fragmentTitle) {
        mToolbar.setTitle(fragmentTitle);
    }

    @Override
    public void inflateFragment(String fragmentTag, String message) {
        if(fragmentTag.equals(getString(R.string.fragment_shop_details))) {
            ShopFragment fragment = new ShopFragment();
            doFragmentTransaction(fragment, fragmentTag, true, message);
        }
    }

    @Override
    public void inflateFragment(String fragmentTag, Parcelable object) {
        if (fragmentTag.equals(getString(R.string.fragment_item_details))) {
            ItemFragment fragment = new ItemFragment();
            doFragmentTransaction(fragment, fragmentTag, true, object);
        }
    }
}
