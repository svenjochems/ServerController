package be.jochems.sven.servercontroller;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
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

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Sven on 9/07/2014.
 */
public class RadioFragment extends Fragment {

    //variables
    private String[] radioUrl;
    private String[] stations;
    private ArrayAdapter<String> arrayAdapter;
    ServerController application;

    //Views
    private Button alarmButton;
    private ListView radioListView;
    private TextView statusText;

    //Connection with server
    private Session session;

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
                new Play().execute("/home/sven/scripts/play Alarm");
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
                                    new Play().execute("/home/sven/scripts/play " + url);
                                }
                            });
                            alert.setNegativeButton(R.string.negativeResponse, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            alert.show();
                }else{
                    Log.d("Station", "call command: /home/sven/scripts/play " + radioUrl[i]);
                    new Play().execute("/home/sven/scripts/play " + radioUrl[i]);
                }

            }
        });

        new Play().execute("/home/sven/scripts/play Print");

        return view;
    }

    public void checkLogon(){
        if (application.isLoggedOn()){
            session = application.getSession();
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
                Log.d("jsch","error" + e.getMessage());
                return ""+R.string.playError;
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
