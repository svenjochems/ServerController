package be.jochems.sven.servercontroller;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by Sven on 11/07/2014.
 */
public class MuninFragment extends Fragment {

    private WebView webView;
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_munin, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        String ip = sp.getString(getString(R.string.pref_key_ip),"");

        webView = (WebView) view.findViewById(R.id.webView);
        webView.loadUrl("http://" + ip + "/munin/jochems/ubuntu.jochems/index.html");

        return view;
    }
}
