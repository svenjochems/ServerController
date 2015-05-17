package be.jochems.sven.servercontroller;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sven on 10/07/2014.
 */
public class InformationFragment extends Fragment{

    ServerController application;
    private Timer timer;

    //Views
    private TextView sensorsView;
    private TextView informationView;

    //Connection with server
    private Session session;

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
            session = application.getSession();
            sensorsView.setEnabled(true);
        } else {
            sensorsView.setEnabled(false);
        }
    }

    public void update(){
        if (application.isLoggedOn()) {
            new Command(sensorsView).execute("sensors");
            new Command(informationView).execute("top -b -n1 | head -5 | tail -4");
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
            view.setText(s);
            //Log.d("result",s);
        }
    }
}
