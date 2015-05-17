package be.jochems.sven.servercontroller;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ConnectionFragment())
                .commit();
    }

    public static class ConnectionFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private EditTextPreference username;
        private EditTextPreference password;
        private EditTextPreference ip;
        private EditTextPreference port;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            username = (EditTextPreference)findPreference(getString(R.string.pref_key_username));
            username.setSummary(sp.getString(getString(R.string.pref_key_username),""));
            password = (EditTextPreference)findPreference(getString(R.string.pref_key_password));
            ip = (EditTextPreference)findPreference(getString(R.string.pref_key_ip));
            ip.setSummary(sp.getString(getString(R.string.pref_key_ip),""));
            port = (EditTextPreference)findPreference(getString(R.string.pref_key_port));
            port.setSummary(sp.getString(getString(R.string.pref_key_port),""));


        }
        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d("pref",key);
            Preference pref = findPreference(key);
            if (!(key == getString(R.string.pref_key_password)))
                pref.setSummary(sharedPreferences.getString(key,"none"));
        }
    }
}
