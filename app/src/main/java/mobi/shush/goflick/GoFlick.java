package mobi.shush.goflick;

import android.app.Application;

import mobi.shush.goflick.utils.User;

/**
 * Created by karim on 11/9/15.
 */
public class GoFlick extends Application {
    User mUser;
    /**
     * Called when the application starts
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mUser = User.init(this);
    }
}
