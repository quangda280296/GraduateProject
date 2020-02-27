package vn.quang.graduateproject.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.Interface.IGetNearbyPlace;
import vn.quang.graduateproject.R;
import vn.quang.graduateproject.model.Point;

/**
 * Created by keban on 3/30/2018.
 */

public class GetNearbyPlace extends AsyncTask {

    String json;
    private Context context;
    private LatLng lng;
    private String type;
    private String next_page_token;
    private IGetNearbyPlace listener;

    public GetNearbyPlace(Context context, LatLng lng, String type, String next_page_token, IGetNearbyPlace listener) {
        this.context = context;
        this.lng = lng;
        this.type = type;
        this.next_page_token = next_page_token;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + lng.latitude + "," + lng.longitude + "&radius=50000" +
                "&type=" + type + "&pagetoken=" + next_page_token + "&key=" + context.getResources().getString(R.string.google_maps_key);

        HttpHandler jsonParser = new HttpHandler();
        json = jsonParser.callService(URL, HttpHandler.GET);

        Log.d("urllllllllll", URL);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        //super.onPostExecute(o);

        if (json != null) {
            JSONObject jsonObj = null;

            try {
                jsonObj = new JSONObject(json);

                if (jsonObj != null) {
                    JSONArray platfform = jsonObj.getJSONArray("results");

                    for (int i = 0; i < platfform.length(); i++) {

                        JSONObject obj = (JSONObject) platfform.get(i);

                        Point addr = new Point();
                        addr.address = obj.getString("vicinity");
                        addr.name = obj.getString("name");
                        addr.latitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        addr.longitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        addr.openningTime = Config.openningTime.get(Utils.rand(0, Config.openningTime.size() - 1));
                        addr.closingTime = Config.closingTime.get((Utils.rand(0, Config.closingTime.size() - 1)));

                        //addr.image = Config.img[Utils.rand(0, Config.img.length - 1)];
                        switch (type) {
                            case "restaurant":
                                addr.image = R.mipmap.ic_location_restaurant;
                                break;

                            case "cafe":
                                addr.image = R.mipmap.ic_location_cafe;
                                break;

                            case "lodging":
                                addr.image = R.mipmap.ic_location_hotel;
                                break;

                            case "pharmacy":
                                addr.image = R.mipmap.ic_location_pharmacy;
                                break;

                            case "supermarket":
                                addr.image = R.mipmap.ic_location_supermarket;
                                break;

                            case "travel_agency":
                                addr.image = R.mipmap.ic_location_travel_agent;
                                break;
                        }

                        Config.listPoint.add(addr);
                        Log.d("weweewe", Config.listPoint.size() + "");
                    }//for

                    String next_page_token = jsonObj.getString("next_page_token");
                    new GetNearbyPlace(context, lng, type, next_page_token, listener).execute();

                }//if

            } catch (JSONException e) {
                e.printStackTrace();

                Log.d("weweewe", Config.listPoint.size() + "");
                listener.onNearbySearched(Config.listPoint);
            }//catch

        } else {
            Log.e("JSONData", "Didn't receive any data from server!");
        }//else
    }
}
