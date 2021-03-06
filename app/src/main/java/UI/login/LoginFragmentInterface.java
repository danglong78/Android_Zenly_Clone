package UI.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public interface LoginFragmentInterface {
    void initView(View view);
    void saveCheckPoint();
    Bundle createBundle(boolean isSkip);
    void saveInformation();
    void loadInformation();
    void showDialog();

}
