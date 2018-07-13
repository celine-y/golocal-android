package yau.celine.golocal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
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
        CartChangeCallback,
        FragmentManager.OnBackStackChangedListener{
    private static final String TAG = "BaseActivity";
    private static final int NAV_DRAWER_NONE = -1;

    private int mCurrentNavMenuItem;
    private int mCurrentBottomMenuItem;

    private FragmentManager mFragmentManager;

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

//        Framelayout for fragments
        mFrameLayout = findViewById(R.id.fragment_container);

//        Set up bottom navigation
        setupBottomNav();


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

//        set FragmentManager
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);


//        Set default menu item if no previous state
        if (savedInstanceState == null) {
            init();
        }
    }

    /**
     * Method to set menu items, styles for bottom nav bar
     */
    private void setupBottomNav() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_bottombar);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigation);

//        colors
        mBottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.lightest));
        mBottomNavigation.setAccentColor(fetchColor(R.color.darkBlueGrey));
        mBottomNavigation.setInactiveColor(fetchColor(R.color.medium));
        mBottomNavigation.setForceTint(true);

        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        mBottomNavigation.setOnTabSelectedListener(this);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private void init(){
        setCurrentItemUsingDrawer(R.id.nav_search);
        SearchFragment fragment = new SearchFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_search),
                false, "", false);
    }

    private void setupHeader(){
//        header onclick
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProfileFragment();
                drawer.closeDrawer(GravityCompat.START);
            }
        });
//        set icon next to profile name
        profileName = headerView.findViewById(R.id.profile_name);
        profileName.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_chevron_right, 0);
//        set profile details (name, favs, photos, etc)
        User currentUser = SharedPrefManager.getInstance(this).getUser();
        profileName.setText(currentUser.getFullName());
    }

    @Override
    public void setToolbarTitle(String fragmentTitle) {
        getSupportActionBar().setTitle(fragmentTitle);
    }

    /**
     * Handling the actual fragment inflations
     */
    private void doFragmentTransaction(Fragment fragment, String tag,
                                       boolean addToBackStack, String message, boolean slideAnimation){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (!message.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.intent_message), message);
            fragment.setArguments(bundle);
        }

        commitTransaction(transaction, fragment, tag, addToBackStack, slideAnimation);
    }

    private void doFragmentTransaction(Fragment fragment, String tag,
                                       boolean addToBackStack, Parcelable parcelable, boolean slideAnimation){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.intent_message), parcelable);
        fragment.setArguments(bundle);

        commitTransaction(transaction, fragment, tag, addToBackStack, slideAnimation);
    }

    private void doFragmentTransaction(Fragment fragment, String tag,
                                       boolean addToBackStack, ArrayList<Parcelable> arrayList, boolean slideAnimation){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

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
            transaction.addToBackStack(tag);

        }

        transaction.commit();
    }

    /**
     * Inflating fragment callbacks
     */

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


    /**
     * Set correct active menu items on any change in fragment
     */
    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getCurrentFragment();
        setToolbarTitle(currentFragment.getTag());
        if (currentFragment instanceof SearchFragment) {
            setCurrentItemUsingDrawer(R.id.nav_search);
        } else if (currentFragment instanceof OrderFragment) {
            setCurrentItemUsingDrawer(R.id.nav_current_order);
        } else if (currentFragment instanceof ProfileFragment) {
            setCurrentItemUsingDrawer(R.id.nav_profile);
        } else {
            setCurrentItemUsingDrawer(NAV_DRAWER_NONE);
        }
//        TODO: else statements for other fragments
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When item selected on bottom navigation
     * @param position
     * @param wasSelected
     * @return
     */
    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
//        checks if current item is already opened
        if (position != mCurrentBottomMenuItem) {
            switch (position) {
                case 0:
                    loadSearchFragment();
                    break;
                case 1:
                    loadProfileFragment();
                    break;
                case 2:
                    loadCurrentOrderFragment();
                    break;
            }
        }
        return true;
    }

    /**
     * Item selected using navigation drawer
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != mCurrentNavMenuItem) {
            switch (item.getItemId()) {
                case R.id.nav_search:
                    loadSearchFragment();
                    break;
                case R.id.nav_current_order:
                    loadCurrentOrderFragment();
                    break;
                case R.id.nav_history:
//                TODO: link to history fragment
                    break;
                case R.id.nav_rewards:
//                TODO: link to rewards fragment
                    break;
                case R.id.nav_payment:
//                TODO: link to payment fragment
                    break;
                case R.id.nav_profile:
                    loadProfileFragment();
                    break;
                case R.id.nav_settings:
//                TODO: link to settings fragment
                    break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Methods to help with setting navigation drawers
     */

    private void setCurrentItemUsingDrawer(int navMenuId) {
        mCurrentNavMenuItem = navMenuId;
        if (mCurrentNavMenuItem == R.id.nav_search) {
            mCurrentBottomMenuItem = 0;
        } else if (mCurrentNavMenuItem == R.id.nav_profile) {
            mCurrentBottomMenuItem = 1;
        } else if (mCurrentNavMenuItem == R.id.nav_current_order) {
            mCurrentBottomMenuItem = 2;
        } else {
            mCurrentBottomMenuItem = AHBottomNavigation.CURRENT_ITEM_NONE;
        }

        navigationView.setCheckedItem(mCurrentNavMenuItem);
        mBottomNavigation.setCurrentItem(mCurrentBottomMenuItem);
    }

    /**
     * Functions to help with loading common fragments between
     * Bottom Navigation Bar and Navigation Drawer
     */
    private void loadSearchFragment() {
        SearchFragment searchFragment = new SearchFragment();
        doFragmentTransaction(searchFragment, getString(R.string.fragment_search),
                true, "", false);
    }

    private void loadProfileFragment() {
        ProfileFragment fragment = new ProfileFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_profile),
                true, "", false);
    }

    private void loadCurrentOrderFragment() {
        OrderFragment orderFragment = new OrderFragment();
        doFragmentTransaction(orderFragment, getString(R.string.fragment_current_order),
                true, "", true);
    }

    /**
     * Adding items to cart will update the badge on bottom nav bar
     * @param number
     */
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

    /**
     * Adding and Removing Drawers
     */

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

    private Fragment getCurrentFragment() {
        return mFragmentManager.findFragmentById(R.id.fragment_container);
    }
}
