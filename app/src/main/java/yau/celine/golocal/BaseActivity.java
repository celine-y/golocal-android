package yau.celine.golocal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import java.util.ArrayList;

import yau.celine.golocal.app.CartSingleton;
import yau.celine.golocal.utils.interfaces.CartChangeCallback;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.objects.User;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IMainActivity, AHBottomNavigation.OnTabSelectedListener,
        CartChangeCallback {
    private static final String TAG = "BaseActivity";

    private Toolbar mToolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View headerView;
    private TextView profileName;
    private TextView profileFavCount;
    private TextView profilePhotoCount;

    private FrameLayout mFrameLayout;


    private AHBottomNavigation mBottomNavigation;


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

//        Framelayout for fragments
        mFrameLayout = findViewById(R.id.fragment_container);

//        Set up bottom navigation
        setupBottomNav();

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


//        Set default menu item if no previous state
        if (savedInstanceState == null) {
            init();
            navigationView.setCheckedItem(R.id.nav_search);
            mBottomNavigation.setCurrentItem(0);
        }
    }

    private void setupBottomNav() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);

//        create items
        AHBottomNavigationItem searchItem = new AHBottomNavigationItem(
                getString(R.string.fragment_search),
                R.drawable.ic_search
        );
        AHBottomNavigationItem profileItem = new AHBottomNavigationItem(
                getString(R.string.fragment_profile),
                R.drawable.ic_person
        );
        AHBottomNavigationItem orderItem = new AHBottomNavigationItem(
                getString(R.string.fragment_current_order),
                R.drawable.ic_shop
        );

//        add items
        mBottomNavigation.addItem(searchItem);
        mBottomNavigation.addItem(profileItem);
        mBottomNavigation.addItem(orderItem);

//        colors
        mBottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.lightest));
        mBottomNavigation.setAccentColor(fetchColor(R.color.darkBlueGrey));
        mBottomNavigation.setInactiveColor(fetchColor(R.color.medium));
//        mBottomNavigation.setForceTint(true);

        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        mBottomNavigation.setOnTabSelectedListener(this);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switchToFragment(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchToFragment(int menuId) {
        switch (menuId) {
            case R.id.nav_search:
                SearchFragment searchFragment = new SearchFragment();
                doFragmentTransaction(searchFragment, getString(R.string.fragment_search),
                        true, "", false);
                navigationView.setCheckedItem(menuId);
                mBottomNavigation.setCurrentItem(0);
                break;
            case R.id.nav_current_order:
                OrderFragment orderFragment = new OrderFragment();
                doFragmentTransaction(orderFragment, getString(R.string.fragment_current_order),
                        true, "", true);
                Log.d(TAG, CartSingleton.getInstance().getCartItemList().toString());
                navigationView.setCheckedItem(menuId);
                mBottomNavigation.setCurrentItem(2);
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
                break;
            case R.id.nav_settings:
//                TODO: link to settings fragment
                navigationView.setCheckedItem(R.id.nav_settings);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void doFragmentTransaction(Fragment fragment, String tag,
           boolean addToBackStack, String message, boolean slideAnimation){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(!message.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.intent_message), message);
            fragment.setArguments(bundle);
        }

        commitTransaction(transaction, fragment, tag, addToBackStack, slideAnimation);
    }

    private void doFragmentTransaction(Fragment fragment, String tag,
           boolean addToBackStack, Parcelable parcelable, boolean slideAnimation){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.intent_message), parcelable);
        fragment.setArguments(bundle);

        commitTransaction(transaction, fragment, tag, addToBackStack, slideAnimation);
    }

    private void doFragmentTransaction(Fragment fragment, String tag,
           boolean addToBackStack, ArrayList<Parcelable> arrayList, boolean slideAnimation){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.intent_message), arrayList);
        fragment.setArguments(bundle);

        commitTransaction(transaction, fragment, tag, addToBackStack, slideAnimation);
    }

    private void commitTransaction(FragmentTransaction transaction, Fragment fragment, String tag,
           boolean addToBackStack, boolean slideAnimation) {
        if (slideAnimation) {
            transaction.setCustomAnimations(R.anim.slide_in_up, 0, 0, R.anim.slide_out_down);
        }
            transaction.replace(R.id.fragment_container, fragment, tag);

        if (addToBackStack) {
//            TODO: do not allow the same fragment to be added to backstack
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void init(){
        SearchFragment fragment = new SearchFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_search),
                false, "", false);
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
        loadProfileFragment();
        mBottomNavigation.setCurrentItem(1);
    }

    private void loadProfileFragment() {
        ProfileFragment fragment = new ProfileFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_profile),
                true, "", false);
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
            mBottomNavigation.setCurrentItem(AHBottomNavigation.CURRENT_ITEM_NONE);
            doFragmentTransaction(fragment, fragmentTag,
                    true, message, false);
        } else if (fragmentTag.equals(getString(R.string.fragment_confirm_order))) {
            ConfirmOrderFragment fragment = new ConfirmOrderFragment();
            doFragmentTransaction(fragment, fragmentTag,
                    true, message, false);
        }
    }

    @Override
    public void inflateFragment(String fragmentTag, Parcelable object) {
//        Not used yet
    }

    @Override
    public void inflateFragment(String fragmentTag, ArrayList<Parcelable> objects) {
        if (fragmentTag.equals(getString(R.string.fragment_item_details))) {
            ItemFragment fragment = new ItemFragment();
            mBottomNavigation.setCurrentItem(AHBottomNavigation.CURRENT_ITEM_NONE);
            doFragmentTransaction(fragment, fragmentTag,
                    true, objects, false);
        }
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        switch (position) {
            case 0:
                SearchFragment searchFragment = new SearchFragment();
                doFragmentTransaction(searchFragment, getString(R.string.fragment_search),
                        true, "", false);
                navigationView.setCheckedItem(R.id.nav_search);
                break;
            case 1:
                loadProfileFragment();
                break;
            case 2:
                OrderFragment orderFragment = new OrderFragment();
                doFragmentTransaction(orderFragment, getString(R.string.fragment_current_order),
                        true, "", true);
                Log.d(TAG, CartSingleton.getInstance().getCartItemList().toString());
                navigationView.setCheckedItem(R.id.nav_current_order);
                break;
        }
        return true;
    }

//    TODO: fix backpress menu highlight correct item

    @Override
    public void onAddOrRemoveItem(int number) {
        if (number == 0) {
            mBottomNavigation.setNotification("", 2);
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText(String.valueOf(number))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.darkBrown))
                    .setTextColor(ContextCompat.getColor(this, R.color.lightest))
                    .build();
            mBottomNavigation.setNotification(notification, 2);
        }
    }

    @Override
    public void lockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        this.getSupportActionBar().hide();

    }

    @Override
    public void unlockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        this.getSupportActionBar().show();
    }

    @Override
    public void lockDrawerBottom() {
        lockDrawer();
        mBottomNavigation.hideBottomNavigation();
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) mFrameLayout.getLayoutParams();
        params.setMargins(0,0,0,0);
        mFrameLayout.setLayoutParams(params);
    }

    @Override
    public void unlockDrawerBottom() {
        unlockDrawer();
        mBottomNavigation.restoreBottomNavigation();
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) mFrameLayout.getLayoutParams();
        int bottom = (int) getResources().getDimension(R.dimen.bottom_nav_height);
        params.setMargins(0, 0,0, bottom);
        mFrameLayout.setLayoutParams(params);
    }
}
