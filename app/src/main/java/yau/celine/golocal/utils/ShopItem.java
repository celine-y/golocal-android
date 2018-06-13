package yau.celine.golocal.utils;

/**
 * Created by Celine on 2018-06-11.
 */

public class ShopItem {
    private int id;
    private String name;
    private String address = "";
    private String thumbnailUrl = "";
    private double rating = 0;

    public ShopItem() {
    }

    public ShopItem(int id, String name, String address, double rating, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.thumbnailUrl = thumbnailUrl;
    }

    public ShopItem(int id, String name) {
        this.id = id;
        this.name = name;
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
    public String getAddress() {
        return address;
    }

    /**
     * Set address of shop
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
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
