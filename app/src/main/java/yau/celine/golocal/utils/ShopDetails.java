package yau.celine.golocal.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopDetails {
    private String googleId;
    private String rawAddr;
    private String formattedAddress;
    private String phoneNumber;
    private Double rating;
    private LatLng coordinates;

    public ShopDetails(JSONObject obj) {
        try {
            this.googleId = obj.getString("google_id");
//            get google_details
            JSONObject googleDetailsJson = obj.getJSONObject("google_details");
            this.formattedAddress = googleDetailsJson.getString("formatted_address");
            this.phoneNumber = googleDetailsJson.getString("formatted_phone_number");
            this.rating = googleDetailsJson.getDouble("rating");
//            raw address
            this.rawAddr = obj.getString("raw");
//            get coordinates
            JSONObject coordinateJson = obj.getJSONObject("coordinates");
            coordinates = new LatLng(coordinateJson.getDouble("latitude"),
                    coordinateJson.getDouble("longitude"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getRawAddr() {
        return rawAddr;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Double getRating() {
        return rating;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

}
