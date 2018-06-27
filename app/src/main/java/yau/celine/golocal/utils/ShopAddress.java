package yau.celine.golocal.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopAddress {
    private String rawAddr;
    private LatLng coordinates;

    public ShopAddress(JSONObject obj) {
        try {
            this.rawAddr = obj.getString("raw");
            coordinates = new LatLng(obj.getDouble("latitude"),
                    obj.getDouble("longitude"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getRawAddr() {
        return rawAddr;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
}
