package yau.celine.golocal.utils.objects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemObject implements Parcelable {
    private int id = 0;
    private String name;
    private String description;
    private int shopId;
    private double price = 0.00;
    private String imageUrl;
    private boolean userFav = false;

    public ItemObject() {}

    public ItemObject(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        description = jsonObject.getString("description");
        price = jsonObject.getDouble("price");
        imageUrl = jsonObject.getString("photo");
        shopId = jsonObject.getInt("shop");
        userFav = jsonObject.getBoolean("favorited_by_user");
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

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    Parcel
    public ItemObject(Parcel in){
//        read and set values from parcel
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        shopId = in.readInt();
        price = in.readDouble();
        imageUrl = in.readString();
        userFav = in.readByte() != 0;     //userFav == true if byte != 0
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
        dest.writeInt(shopId);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (userFav ? 1 : 0));     //if userFav == true, byte == 1
    }

//    CREATOR - used when un-parceling (creating object)
    public static final Parcelable.Creator<ItemObject> CREATOR =
        new Parcelable.Creator<ItemObject>(){

        @Override
        public ItemObject createFromParcel(Parcel parcel) {
            return new ItemObject(parcel);
        }

        @Override
        public ItemObject[] newArray(int i) {
            return new ItemObject[0];
        }
    };

    public boolean isUserFav() {
        return userFav;
    }
}
