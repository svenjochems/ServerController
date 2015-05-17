package be.jochems.sven.servercontroller;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sven on 11/07/2014.
 */
public class CameraFragment extends Fragment {

    private ImageView imgCamera1;
    private ImageView imgCamera2;

    private Timer timer;
    private boolean lock;
    private boolean camera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        lock = false;
        camera = true;

        imgCamera1 = (ImageView)view.findViewById(R.id.imgCamera1);
        imgCamera2 = (ImageView)view.findViewById(R.id.imgCamera2);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        },300,300);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void update() {
        if (!lock) {
            if (camera) //camera reload take turn
                new ImageLoadTask("http://192.168.0.50:8888/Snapshot.cgi?user=jochems&pwd=jochems52", imgCamera1).execute();
            else
                new ImageLoadTask("http://192.168.0.51:8889/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=jochems&pwd=jochems52", imgCamera2).execute();
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lock = true;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.d("camera",url);
                return myBitmap;
            } catch (Exception e) {
                Log.d("camera", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            camera = !camera;
            lock = false;
        }

    }
}
