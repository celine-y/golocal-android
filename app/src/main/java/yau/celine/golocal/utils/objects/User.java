package yau.celine.golocal.utils.objects;

/**
 * Created by Celine on 2018-06-11.
 */

public class User {

    private int id;
    private String token;
    private String username;
    private String fullName;
    private String profilePhotoUrl;

    public User(int id, String token, String username, String fullName, String profilePhotoUrl) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.profilePhotoUrl = profilePhotoUrl;
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
