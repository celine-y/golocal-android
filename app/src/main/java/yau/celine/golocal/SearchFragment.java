package yau.celine.golocal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.interfaces.IMainActivity;
import yau.celine.golocal.utils.interfaces.OnShopClickListener;
import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.utils.adapters.ShopAdapter;
import yau.celine.golocal.utils.objects.ShopObject;
import yau.celine.golocal.utils.URLs;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Celine on 2018-06-13.
 */

public class SearchFragment extends Fragment implements
        OnShopClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "SearchFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static final int DEFAULT_ZOOM = 14;
    private static final int ZOOM_CITY = 11;

    private View view;

    private RelativeLayout loadingPanel;

    private IMainActivity mIMainActivity;

    private Boolean mLocationPermissionGranted;
    private Boolean mGpsEnabled;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

//    entry points to Places API
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
//    entry point to Fused Location Provider
    private FusedLocationProviderClient mFusedLocationProviderClient;

//    Default location
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

//    storing activity state keys
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private RecyclerView mRecyclerView;
    private ShopAdapter mShopAdapter;
    private ArrayList<ShopObject> mShopList = new ArrayList<>();

    private Location mLastKnownLocation;
    private LocationManager mLocationManager;
    private SupportMapFragment mMapFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

//        Construct GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(getActivity());
//        Construct PlaceDetectionClient
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity());
//        Construct FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

//        setup toolbar
        setHasOptionsMenu(true);
        mIMainActivity.setToolbarTitle(getTag());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mMapFragment.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);

            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                            .build();
                    Intent autocompleteIntent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(getActivity());
                    startActivityForResult(autocompleteIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("Exception: {%s}", e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e("Exception: {%s}", e.getMessage());
                    Toast.makeText(getContext(), "Google play services unavailable", Toast.LENGTH_LONG);
                }
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getContext(), data);
                    Log.i(TAG, "User clicked place: " + place.getName());

//                    Move map camera to selected place
                    moveCamera(place.getLatLng(), ZOOM_CITY);
//                    Search for stores in area
                    getStores();
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getContext(), data);
                    Log.e(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
//                    TODO: user cancelled operation
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        prompt for permission
        getLocationPermission();

//        prompt for GPS
        getGPSEnabled();

//        turn on location layer and related controls
        updateLocationUI();

//        get current location of devices and set position of map
        getDeviceLocation();

//        set Map settings
        mMap.setMinZoomPreference(ZOOM_CITY);
        mMap.setMaxZoomPreference(DEFAULT_ZOOM + 1);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
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

    private void setUpMapIfNeeded() {
        if (mMapFragment == null) {
            android.support.v4.app.FragmentManager fm = getChildFragmentManager();
            mMapFragment =  SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mMapFragment).commit();
            mMapFragment.getMapAsync(this);
        }
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpMapIfNeeded();
    }

    /**
     * Get best and most recent location of devices,
     * which may be null in rare cases when a location is not available
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
//                            Move map's camera position to current location
                            mLastKnownLocation = location;
                            moveCamera(mLastKnownLocation, DEFAULT_ZOOM);
//                            Search for stores in visible area
                            getStores();
                        } else {
                            setDefaultLocation();
                        }
                    }
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        setDefaultLocation();
                        Log.e(TAG, "Exception: %s", ex);
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {
        Log.d(TAG, "Current location is null. Using defaults.");
        moveCamera(mDefaultLocation, DEFAULT_ZOOM);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    /**
     * Enable My Location layer if fine location permission granted
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getGPSEnabled() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGPS();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
            break;
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void moveCamera(Location location, int zoomLevel) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(),
                        location.getLongitude()),
                zoomLevel
                ));
    }

    private void moveCamera(LatLng latLng, int zoomLevel) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, zoomLevel));
    }

    private void getStores() {
//        setup store recyclers/adapters
        showShopListRecycler();

//        show progress bar
        showProgress();

//        get shops and display
        getShopFromApi();
    }


    private void showShopListRecycler() {
        if (mRecyclerView == null) {
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
        } else {
            mShopAdapter.setSelectedItem(-1);
            mShopList.clear();
            mMap.clear();
        }
    }

    private void getShopFromApi() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLngBounds latLngBounds = visibleRegion.latLngBounds;
        String url = URLs.getShopUrl(latLngBounds);

        JsonArrayRequest shopReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hideProgress();

//                        Parse json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                ShopObject shop = new ShopObject(obj);
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
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = SharedPrefManager
                        .getInstance(getActivity().getApplicationContext()).getHeaders();

                return headers;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(shopReq);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        final LatLng markerPosition = marker.getPosition();
        int selectedMarker = -1;
        for (int i = 0; i < mShopList.size(); i++) {
            if (markerPosition.latitude == mShopList.get(i).getLatLng().latitude
                    && markerPosition.longitude == mShopList.get(i).getLatLng().longitude) {
                selectedMarker = i;
            }
        }

        selectItem(selectedMarker);

        return false;
    }

    private void selectItem(final int itemId){
        mRecyclerView.smoothScrollToPosition(itemId);

        mShopAdapter.setSelectedItem(itemId);
        mShopAdapter.notifyDataSetChanged();
    }
}
