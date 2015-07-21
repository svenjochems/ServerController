package be.jochems.sven.servercontroller;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sven on 10/07/2014.
 */
public class InformationFragment extends Fragment{

    ServerController application;
    private Timer timer;
    private SharedPreferences sp;
    private String mUser;
    private String mPassword;
    private String mPort;
    private String mIP;

    //Views
    private TextView sensorsView;
    private TextView informationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        sensorsView = (TextView)view.findViewById(R.id.txtSensors);
        informationView = (TextView)view.findViewById(R.id.txtInformation);

        application = (ServerController)getActivity().getApplicationContext();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        },1000,1000);
        checkLogon();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void checkLogon(){
        if (application.isLoggedOn()){
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mUser = sp.getString(getString(R.string.pref_key_username),"");
            mPassword = sp.getString(getString(R.string.pref_key_password),"");
            mIP = sp.getString(getString(R.string.pref_key_ip),"");
            mPort = sp.getString(getString(R.string.pref_key_port), "0");

            sensorsView.setEnabled(true);
        } else {
            sensorsView.setEnabled(false);
        }
    }

    public void update(){
        if (application.isLoggedOn()) {
            new Command(sensorsView).execute("Sensors.sh");
            new Command(informationView).execute("Top.sh");
        }
    }

    private class Command extends AsyncTask<String, String, String> {

        TextView view;

        public Command(TextView view){
            this.view = view;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://" + mIP + ":" + mPort + "/scripts/" + params[0]);

                String authString = mUser + ":" + mPassword;
                final String authStringEnc = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
                conn.connect();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String output = "";

                while( ( line = in.readLine()) != null){
                    output += (line + '\n');
                }
                Log.d("output", output);
                return output;

            } catch (Exception e){
                Log.d("jsch", "error" + e.getMessage());
                return ""+R.string.playError;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            view.setText(s);
            //Log.d("result",s);
        }
    }
}
