package be.jochems.sven.servercontroller;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Sven on 9/07/2014.
 */
public class RadioFragment extends Fragment {

    //variables
    private String[] radioUrl;
    private String[] stations;
    private ArrayAdapter<String> arrayAdapter;
    private SharedPreferences sp;
    private String mUser;
    private String mPassword;
    private String mPort;
    private String mIP;
    ServerController application;

    //Views
    private Button alarmButton;
    private ListView radioListView;
    private TextView statusText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radio, container, false);

        statusText = (TextView)view.findViewById(R.id.txtStatus);
        alarmButton = (Button)view.findViewById(R.id.btnAlarm);
        radioListView = (ListView) view.findViewById(R.id.lstRadio);

        application = (ServerController)getActivity().getApplicationContext();
        checkLogon();

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "alarm initiated", Toast.LENGTH_SHORT).show();
                new Play().execute("Alarm");
                //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                //String username = sp.getString(getString(R.string.pref_key_username),null);
                //Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
            }
        });

        stations = getResources().getStringArray(R.array.radioStations);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,stations );
        radioListView.setAdapter(arrayAdapter);
        radioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                radioUrl = getResources().getStringArray(R.array.radioUrl);

                if(radioUrl[i].equals("URL")){
                    Log.d("Custom URL", "Ask user for custom URL");

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            // Set an EditText view to get user input
                            final EditText input = new EditText(getActivity());
                            alert.setView(input);
                            alert.setTitle(R.string.URLDialogTitle);
                            alert.setPositiveButton(R.string.positiveResponse, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String url = input.getText().toString();
                                    new Play().execute(url);
                                }
                            });
                            alert.setNegativeButton(R.string.negativeResponse, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            alert.show();
                }else{
                    Log.d("Station", "call command: " + radioUrl[i]);
                    new Play().execute(radioUrl[i]);
                }

            }
        });

        new Play().execute("Print");

        return view;
    }

    public void checkLogon(){
        if (application.isLoggedOn()){
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mUser = sp.getString(getString(R.string.pref_key_username),"");
            mPassword = sp.getString(getString(R.string.pref_key_password),"");
            mIP = sp.getString(getString(R.string.pref_key_ip),"");
            mPort = sp.getString(getString(R.string.pref_key_port), "0");

            alarmButton.setEnabled(true);
            radioListView.setEnabled(true);
        } else {
            alarmButton.setEnabled(false);
            radioListView.setEnabled(false);
        }
    }

    private class Play extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://" + mIP + ":" + mPort + "/scripts/Radio.sh?param=" + params[0]);

                String authString = mUser + ":" + mPassword;
                final String authStringEnc = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
                conn.connect();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str = in.readLine();
                Log.d("output",str);
                return str;

            } catch (Exception e){
                Log.d("jsch","error" + e.getMessage());
                return getString(R.string.playError);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            statusText.setText(s);
            Log.d("result",s);
        }
    }
}
