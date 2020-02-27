package vn.quang.graduateproject.utils;

import android.os.AsyncTask;
import android.text.Html;
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
import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.Interface.INavigationFinder;
import vn.quang.graduateproject.model.Distance;
import vn.quang.graduateproject.model.Duration;
import vn.quang.graduateproject.model.Route;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class MultiNavigationFinder {

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?language=" + Config.language + "&";
    private static final String GOOGLE_API_KEY = "AIzaSyAq8eYw5efNLgY9isX52QpjtvJZo-UV6ec";

    private INavigationFinder listener;
    private LatLng origin;
    private LatLng destination;
    private String mode;
    private List<LatLng> waypoints;

    public MultiNavigationFinder(INavigationFinder listener, LatLng origin, LatLng destination, String mode, List<LatLng> waypoints) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
        this.waypoints = waypoints;
    }

    public void execute() throws UnsupportedEncodingException {
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String URL = DIRECTION_URL_API + "origin=" + origin.latitude + "," + origin.longitude + "&destination="
                + destination.latitude + "," + destination.longitude + "&mode=" + mode + "&key=" + GOOGLE_API_KEY
                + "&waypoints=optimize:true|";

        for (LatLng w : waypoints) {
            URL += w.latitude + "," + w.longitude + "|";
        }

        return URL;
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        Route route = new Route();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");

        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");

            for (int j = 0; j < jsonLegs.length(); j++) {
                JSONObject jsonLeg = jsonLegs.getJSONObject(j);

                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
                JSONArray jsonSteps = jsonLeg.getJSONArray("steps");

                for (int k = 0; k < jsonSteps.length(); k++) {
                    JSONObject distance = jsonSteps.getJSONObject(k).getJSONObject("distance");
                    JSONObject duration = jsonSteps.getJSONObject(k).getJSONObject("duration");

                    route.listDistances.add(new Distance(distance.getString("text"), distance.getInt("value")));
                    route.listDurations.add(new Duration(duration.getString("text"), duration.getInt("value")));

                    route.listHtmlInstructions.add(Html.fromHtml(jsonSteps.getJSONObject(k).getString("html_instructions")));
                    route.listStepsLocations.add(new LatLng(jsonSteps.getJSONObject(k).getJSONObject("start_location").getDouble("lat"), jsonSteps.getJSONObject(k).getJSONObject("start_location").getDouble("lng")));
                }

                route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                route.endAddress = jsonLeg.getString("end_address");
                route.startAddress = jsonLeg.getString("start_address");
                route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                route.listPoints = decodePolyLine(overview_polylineJson.getString("points"));
            }
        }

        listener.onNavigationFinderSuccess(route);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
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
