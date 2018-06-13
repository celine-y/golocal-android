package yau.celine.golocal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainActivity {
    private DrawerLayout drawer;

    private static final String TAG = "BaseActivity";
    private TextView mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_base_layout);

//        Set up toolbar
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            init();
            navigationView.setCheckedItem(R.id.nav_search);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                SearchFragment fragment = new SearchFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_search), false, "");
                break;
            case R.id.nav_current_order:
//                TODO: link to current order fragment
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
            case R.id.nav_settings:
//                TODO: link to settings fragment
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

    private void init(){
        SearchFragment fragment = new SearchFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_search), false, "");
    }

    @Override
    public void setToolbarTitle(String fragmentTitle) {
        mToolbarTitle.setText(fragmentTitle);
    }

    @Override
    public void inflateFragment(String fragmentTag, String message) {
        if(fragmentTag.equals(getString(R.string.fragment_shop_details))) {
            ShopFragment fragment = new ShopFragment();
            doFragmentTransaction(fragment, fragmentTag, true, message);
        }
    }
}
