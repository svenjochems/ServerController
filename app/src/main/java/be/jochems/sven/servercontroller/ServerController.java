package be.jochems.sven.servercontroller;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Created by Sven on 10/07/2014.
 */
public class ServerController extends Application {

    //Connection with server
    private JSch jsch;
    private Session session;

    private boolean loggedOn = false;

    public Session getSession(){
        return session;
    }

    public void setSession(Session session){
        this.session = session;
    }

    public boolean isLoggedOn(){
        Log.d("application","get: " + loggedOn);
        return loggedOn;
    }

    public void setLoggedOn(boolean loggedOn){
        Log.d("application","set: " + loggedOn);
        this.loggedOn = loggedOn;
    }
}
