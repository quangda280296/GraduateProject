package vn.quang.graduateproject.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import vn.quang.graduateproject.Interface.IGPSHandler;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by keban on 9/11/2017.
 */

public class GPSHandler extends AsyncTask {

    private Context context;
    private IGPSHandler listener;
    private Location myLocation;

    public GPSHandler(Context context, IGPSHandler listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        super.onPostExecute(o);
        if (myLocation != null) {
            try {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                List<Address> street = gc.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);

                if (street.size() > 0) {
                    String address = street.get(0).getAddressLine(0);
                    listener.onGPS(myLocation, address);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Log.d("popopo", "loiiiii");
    }
}
