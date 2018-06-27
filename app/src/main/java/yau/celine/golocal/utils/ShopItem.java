package yau.celine.golocal.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Celine on 2018-06-11.
 */

public class ShopItem {
    private int id;
    private String name;
    private String thumbnailUrl = "";
    private ShopAddress shopAddr;
    private double rating = 0;

    public ShopItem() {
    }

    public ShopItem(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.thumbnailUrl = obj.getString("cover_image");
            this.shopAddr = new ShopAddress(obj.getJSONObject("address"));
//            TODO: get shop rating
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
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
        return shopAddr.getRawAddr();
    }

    public LatLng getLatLng() {
        return shopAddr.getCoordinates();
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
        return rating;
    }

    /**
     * Set rating
     * @param rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }


}
