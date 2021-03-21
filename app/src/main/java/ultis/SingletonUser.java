package ultis;
import android.app.Application;

import data.User;

public class SingletonUser extends Application {
    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
