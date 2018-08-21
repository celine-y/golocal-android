package yau.celine.golocal.utils.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Celine on 2018-06-11.
 */

public class User {

    private int id;
    private String token;
    private String username;
    private String fullName;
    private String profilePhotoUrl;

    public User(int id, String token, String username, String fullName,
                String profilePhotoUrl) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public User(JSONObject jsonResponse) throws JSONException{
        JSONObject userJson = jsonResponse.getJSONObject("user");
        token = jsonResponse.getString("key");
//        parsing user details
        id = userJson.getInt("id");
        username = userJson.getString("username");
        fullName = userJson.getString("full_name");
        profilePhotoUrl = userJson.getString("profile_image");
    }

    public int getId(){
        return id;
    }

    public String getToken(){
        return token;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
}
