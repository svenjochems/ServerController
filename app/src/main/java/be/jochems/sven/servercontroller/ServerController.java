package be.jochems.sven.servercontroller;

import android.app.Application;
import android.util.Log;

/**
 * Created by Sven on 10/07/2014.
 */
public class ServerController extends Application {

    private boolean loggedOn = false;

    public boolean isLoggedOn(){
        Log.d("application","get: " + loggedOn);
        return loggedOn;
    }

    public void setLoggedOn(boolean loggedOn){
        Log.d("application","set: " + loggedOn);
        this.loggedOn = loggedOn;
    }


}
