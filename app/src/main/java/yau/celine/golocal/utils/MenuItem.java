package yau.celine.golocal.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String shopUrl;
    private double price;
    private String categoryUrl;
    private String imageUrl;

    public MenuItem() {}

    public MenuItem(int id, String name, String description, String shopUrl, double price){
        this.id = id;
        this.name = name;
        this.description = description;
        this.shopUrl = shopUrl;
        this.price = price;
    }

    public MenuItem(int id, String name, String description, String shopUrl, double price, String categoryUrl){
        this.id = id;
        this.name = name;
        this.description = description;
        this.shopUrl = shopUrl;
        this.price = price;
        this.categoryUrl = categoryUrl;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getShopUrl(){
        return shopUrl;
    }

    public void setShopUrl(String shopUrl){
        this.shopUrl = shopUrl;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl){
        this.categoryUrl = categoryUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    Parcel
    public MenuItem(Parcel in){
//        read and set values from parcel
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        shopUrl = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
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
        dest.writeString(description);
        dest.writeString(shopUrl);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
    }

//    CREATOR - used when un-parceling (creating object)
    public static final Parcelable.Creator<MenuItem> CREATOR =
        new Parcelable.Creator<MenuItem>(){

        @Override
        public MenuItem createFromParcel(Parcel parcel) {
            return new MenuItem(parcel);
        }

        @Override
        public MenuItem[] newArray(int i) {
            return new MenuItem[0];
        }
    };

}
