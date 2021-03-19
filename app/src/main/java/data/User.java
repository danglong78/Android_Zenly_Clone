package data;

import android.graphics.Bitmap;

public class User {
    Bitmap avatar;
    String name;
    public User (Bitmap avatar,String name) {
        this.avatar = avatar;
        this.name = name;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }
}
