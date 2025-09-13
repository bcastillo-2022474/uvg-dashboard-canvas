package models;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String canvasToken;
    private Date tokenExpiry;
    private boolean isActive;

    public User(String id, String name, String email, String avatar, String canvasToken, Date tokenExpiry, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.canvasToken = canvasToken;
        this.tokenExpiry = tokenExpiry;
        this.isActive = isActive;
    }

    public String getToken() {
        return canvasToken;
    }
}
