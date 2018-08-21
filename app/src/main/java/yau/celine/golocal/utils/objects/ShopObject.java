package yau.celine.golocal.utils.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Celine on 2018-06-11.
 */

public class ShopObject implements Parcelable {
    private int id;
    private String name;
    private String thumbnailUrl = "";
    private ShopDetails shopDetails;

    public ShopObject(){}

    public ShopObject(JSONObject obj) throws JSONException {
        this.id = obj.getInt("id");
        this.name = obj.getString("name");
        this.thumbnailUrl = obj.getString("cover_image");
        this.shopDetails = new ShopDetails(obj.getJSONObject("place"));
    }

    public ShopObject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        shopDetails = in.readParcelable(ShopDetails.class.getClassLoader());
    }

    /**
     * Get store id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set store id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name of shop
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of shop
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get address of shop
     * @return address
     */
    public String getRawAddress() {
        return shopDetails.getRawAddr();
    }

    public LatLng getLatLng() {
        return shopDetails.getCoordinates();
    }

    /**
     * Get thumbnail url
     * @return thumbnailUrl
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Set thumbnail url
     * @param thumbnailUrl
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * Get rating
     * @return rating
     */
    public double getRating() {
        return shopDetails.getRating();
    }

    public String getFormattedAddress() {
        return shopDetails.getFormattedAddress();
    }

    public String getPhoneNumber() {
        return shopDetails.getPhoneNumber();
    }

    public ShopDetails getShopDetails() {
        return shopDetails;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
//        write properties to parcel
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(thumbnailUrl);
        dest.writeParcelable(shopDetails, i);
    }

//    CREATOR - used when un-parceling
    public static final Parcelable.Creator<ShopObject> CREATOR
        = new Parcelable.Creator<ShopObject>() {

        @Override
        public ShopObject createFromParcel(Parcel parcel) {
            return new ShopObject(parcel);
        }

        @Override
        public ShopObject[] newArray(int i) {
            return new ShopObject[0];
        }
    };
}
