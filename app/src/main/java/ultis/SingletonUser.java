package ultis;
import android.app.Application;

import data.models.User;

public class SingletonUser extends Application {
    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
