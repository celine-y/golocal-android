package yau.celine.golocal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.IMainActivity;
import yau.celine.golocal.utils.OnShopClickListener;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.ShopAdapter;
import yau.celine.golocal.utils.ShopItem;
import yau.celine.golocal.utils.URLs;

/**
 * Created by Celine on 2018-06-13.
 */

public class SearchFragment extends Fragment implements
        OnShopClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{
    private static final String TAG = "SearchFragment";
    private static final int LOC_REQ_CODE = 1;
    private static final int CON_REQ_CODE = 2;
    private static int UPDATE_INTERNAL=5000;
    private static int FASTEST_INTERVAL=3000;
    private static int ZOOM_LEVEL=13;

    private View view;

    private RelativeLayout loadingPanel;

    private IMainActivity mIMainActivity;

    private RecyclerView mRecyclerView;
    private ShopAdapter mShopAdapter;
    private ArrayList<ShopItem> mShopList = new ArrayList<>();

    private GoogleMap mMap;
    private MapView mMapView;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng mLastLatLng;
    private LocationManager mLocationManager;
    private Criteria mCriteria;
    private String bestProvider;
    private Location mLastLocation;
    private SupportMapFragment mMapFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIMainActivity.setToolbarTitle(getTag());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);

            showShopOnMap();
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGPS();
        }
    }

    private void buildAlertMessageNoGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This app works best with GPS enabled")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showShopOnMap() {
        showShopListRecycler();
    }

    private void setUpMapIfNeeded() {
        if (mMapFragment == null) {
            mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mMapFragment.getMapAsync(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
        }
    }

    private void setMarkers() {
        //        add markers from each shop
        for (int i = 0; i < mShopList.size(); i++) {
            MarkerOptions options = new MarkerOptions()
                    .position(mShopList.get(i).getLatLng());

            mMap.addMarker(options);
        }

        mMap.setOnMarkerClickListener(this);
    }

    private void showShopListRecycler() {
//        set RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
//        set Adapter
        mShopAdapter = new ShopAdapter(getContext(), mShopList);
        mRecyclerView.setAdapter(mShopAdapter);
//        set on click for shop item
        mShopAdapter.setListener(this);
//        find loadingPanel
        loadingPanel = view.findViewById(R.id.loadingPanel);

//        show progress bar
        showProgress();

//        get shops and display
        parseJSON();
    }

    private void parseJSON() {
        JsonArrayRequest shopReq = new JsonArrayRequest(URLs.URL_SHOP_LIST,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hideProgress();

//                        Parse json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                ShopItem shop = new ShopItem(obj);
//                                add shop to shop array
                                mShopList.add(shop);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.i(TAG, "size: " + mShopList.size());

//                        notify list adapter about data changes
                        mShopAdapter.notifyDataSetChanged();
//                        show markers
                        if (!mShopList.isEmpty()) {
                            setMarkers();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error : " + error.getMessage());
                hideProgress();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String token = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getKeyToken();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Token " + token);

                return headers;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(shopReq);
    }

    private void showProgress() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        loadingPanel.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentClick(int position) {
        String shop_id = String.valueOf(mShopList.get(position).getId());
        mIMainActivity.inflateFragment(getString(R.string.fragment_shop_details), shop_id);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        TODO: highlight store clicked
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpMapIfNeeded();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        Enable current location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                permission granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                TODO: show rationale
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOC_REQ_CODE);
            } else {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOC_REQ_CODE);
            }

            return false;
        } else {
            return true;
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOC_REQ_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERNAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLastLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
}
