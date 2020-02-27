package vn.quang.graduateproject.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.Interface.IDistanceCaculator;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class MultiDistanceCaculator {

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/distancematrix/json?language=" + Config.language + "&";
    private static final String GOOGLE_API_KEY = "AIzaSyAq8eYw5efNLgY9isX52QpjtvJZo-UV6ec";

    private IDistanceCaculator listener;
    private LatLng origin;
    private List<LatLng> destinations;
    private String mode;

    public MultiDistanceCaculator(IDistanceCaculator listener, LatLng origin, List<LatLng> destinations, String mode) {
        this.listener = listener;
        this.origin = origin;
        this.destinations = destinations;
        this.mode = mode;
    }

    public void execute() throws UnsupportedEncodingException {
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String URL = DIRECTION_URL_API + "origins=" + origin.latitude + "," + origin.longitude
                + "&mode=" + mode + "&key=" + GOOGLE_API_KEY + "&destinations=";

        for (LatLng d : destinations) {
            URL += d.latitude + "," + d.longitude + "|";
        }

        Log.d("uuuuuuuuu", URL);

        return URL;
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRows = jsonData.getJSONArray("rows");

        int max = 0;
        int index = 0;

        for (int i = 0; i < jsonRows.length(); i++) {
            JSONObject jsonRow = jsonRows.getJSONObject(i);

            JSONArray jsonElements = jsonRow.getJSONArray("elements");

            for (int j = 0; j < jsonElements.length(); j++) {
                JSONObject jsonElement = jsonElements.getJSONObject(j);

                JSONObject jsonDistance = jsonElement.getJSONObject("distance");
                int distance = jsonDistance.getInt("value");

                if (distance > max) {
                    max = distance;
                    index = i;
                }
            }
        }

        listener.onDistanceCaculating(destinations.get(index));
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);

                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
