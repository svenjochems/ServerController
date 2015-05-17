package be.jochems.sven.servercontroller;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Sven on 18/4/2015.
 */
public class SolarFragment extends Fragment {

    //Variables
    ServerController application;

    //Views
    private Button oldSolarButton;
    private Button newSolarButton;
    private TextView solarDataText;
    private RelativeLayout relLaProgress;

    //Connection with server
    private Session session;

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
            session = application.getSession();
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
                new GetSolarData().execute("/home/sven/scripts/currentSolarData.sh oud");
                break;
            case 1:
                new GetSolarData().execute("/home/sven/scripts/currentSolarData.sh nieuw");
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
                // SSH Channel
                ChannelExec channelssh = (ChannelExec)session.openChannel("exec");
                InputStream inputStream = channelssh.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                // Execute command
                channelssh.setCommand(params[0]);
                channelssh.connect();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
                }
                channelssh.disconnect();

                return stringBuilder.toString();
            } catch (Exception e){
                Log.d("jsch", "error" + e.getMessage());
                return ""+R.string.playError;
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
