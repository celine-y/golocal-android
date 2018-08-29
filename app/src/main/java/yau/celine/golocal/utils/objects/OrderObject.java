package yau.celine.golocal.utils.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderObject {
    private int id;
    private ShopObject shop;
    private Date createdDate;
    private Date updatedDate;
    private Boolean isCompleted;

    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

    public OrderObject(JSONObject obj) throws JSONException {
        this.id = obj.getInt("id");
        this.shop = new ShopObject(obj.getJSONObject("shop"));
        String created = obj.getString("created_at");
        String updated = obj.getString("updated_at");
        try {
            createdDate = parseDateString(created);
            updatedDate = parseDateString(updated);
        } catch (Exception e){
            e.printStackTrace();
            createdDate = new Date();
            updatedDate = new Date();
        }
        this.isCompleted = obj.getBoolean("completed");
    }

    private Date parseDateString(String dateStr) throws Exception {
        return mFormatter.parse(dateStr);
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getCreatedDateString() {
        return mFormatter.format(createdDate);
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public String getUpdatedDateString() {
        return mFormatter.format(updatedDate);
    }

    public int getId() {
        return id;
    }

    public ShopObject getShop() {
        return shop;
    }

}
