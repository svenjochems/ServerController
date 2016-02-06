package be.jochems.sven.servercontroller;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Sven on 18/4/2015.
 */
public class SolarFragment extends Fragment {

    //Variables
    ServerController application;
    private SharedPreferences sp;
    private String mUser;
    private String mPassword;
    private String mPort;
    private String mIP;

    //Views
    private Button oldSolarButton;
    private Button newSolarButton;
    private TextView solarDataText;
    private RelativeLayout relLaProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solar, container, false);

        solarDataText = (TextView)view.findViewById(R.id.txtSolar);
        oldSolarButton = (Button)view.findViewById(R.id.btnSolarOld);
        newSolarButton = (Button)view.findViewById(R.id.btnSolarNew);
        relLaProgress = (RelativeLayout)view.findViewById(R.id.solarHeaderProgress);

        application = (ServerController)getActivity().getApplicationContext();
        checkLogon();

        oldSolarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(0);
            }
        });

        newSolarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });

        oldSolarButton.performClick();

        return view;
    }

    public void checkLogon(){
        if (application.isLoggedOn()){
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mUser = sp.getString(getString(R.string.pref_key_username),"");
            mPassword = sp.getString(getString(R.string.pref_key_password),"");
            mIP = sp.getString(getString(R.string.pref_key_ip),"");
            mPort = sp.getString(getString(R.string.pref_key_port), "0");

            solarDataText.setEnabled(true);
            oldSolarButton.setEnabled(true);
            newSolarButton.setEnabled(true);
        } else {
            solarDataText.setEnabled(false);
            oldSolarButton.setEnabled(false);
            newSolarButton.setEnabled(false);
        }
    }

    public void getData(int ID){
        switch (ID){
            case 0:
                new GetSolarData().execute("oud");
                break;
            case 1:
                new GetSolarData().execute("nieuw");
                break;
        }

    }

    private class GetSolarData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            relLaProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://" + mIP + ":" + mPort + "/script.php?solar=" + params[0]);

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
                return getString(R.string.playError);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            solarDataText.setText(s);

            relLaProgress.setVisibility(View.INVISIBLE);
            relLaProgress.setBackgroundColor(Color.parseColor("#80B0B0B0"));
            Log.d("result",s);
        }
    }
}
