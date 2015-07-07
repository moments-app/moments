package moments.app.com;

import android.app.Application;

import com.parse.Parse;
/**
 * Created by Kevin Desai on 5/28/2015.
 */
public class Moments extends Application {


    @Override public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "AppID", "ClientID"); // Your Application ID and Client Key are defined elsewhere
    }
}
