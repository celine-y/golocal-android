package yau.celine.golocal.utils;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Locale;

/**
 * Created by Celine on 2018-06-11.
 */

public class URLs {
    private static final String ROOT_URL = "http://10.0.2.2:8000/api/v1/";
//    private static final String ROOT_URL = "http://cafeproject-dev.us-east-1.elasticbeanstalk.com/api/v1/";

//    AUTHENTICATION URLS
    private static final String ROOT_AUTH = "rest-auth/";
    public static final String URL_LOGIN = ROOT_URL + ROOT_AUTH + "login/";
    public static final String URL_REGISTER = ROOT_URL + ROOT_AUTH + "registration/";

//    SHOP URLS
    public static final String URL_SHOP_LIST = ROOT_URL + "shops/";
//    SHOP PARAMS
    public static final String SHOP_LOCATION_PARAMS = "?fields=place,name";
    public static final String SHOP_RECTANGLE_PARAMS = "?rectangle=%f,%f,%f,%f";

    public static String getShopUrl(int id){
        return URL_SHOP_LIST + String.valueOf(id) + "/";
    }
    public static String getShopUrl(LatLngBounds latLngBounds) {
        String params = String.format(Locale.getDefault(), SHOP_RECTANGLE_PARAMS,
                latLngBounds.southwest.latitude,
                latLngBounds.southwest.longitude,
                latLngBounds.northeast.latitude,
                latLngBounds.northeast.longitude);
        return URL_SHOP_LIST + params;
    }
    public static String getShopLocationUrl (int id) {
        return URL_SHOP_LIST + String.valueOf(id) + "/" + SHOP_LOCATION_PARAMS;
    }
}
