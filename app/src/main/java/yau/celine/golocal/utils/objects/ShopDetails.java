package yau.celine.golocal.utils.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopDetails implements Parcelable{
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

    public ShopDetails(Parcel in) {
        googleId = in.readString();
        formattedAddress = in.readString();
        phoneNumber = in.readString();
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

    public Double getLatitude(){
        return coordinates.latitude;
    }

    public Double getLongitude(){
        return coordinates.longitude;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(googleId);
        dest.writeString(formattedAddress);
        dest.writeString(phoneNumber);
    }

    public static final Parcelable.Creator<ShopDetails> CREATOR =
        new Parcelable.Creator<ShopDetails>() {

            @Override
            public ShopDetails createFromParcel(Parcel parcel) {
                return new ShopDetails(parcel);
            }

            @Override
            public ShopDetails[] newArray(int i) {
                return new ShopDetails[0];
            }
        };
}
