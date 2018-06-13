package yau.celine.golocal.utils;

/**
 * Created by Celine on 2018-06-11.
 */

public class User {

    private int id;
    private String token;

    public User(int id, String token) {
        this.id = id;
        this.token = token;
    }

    public int getId(){
        return id;
    }

    public String getToken(){
        return token;
    }
}
