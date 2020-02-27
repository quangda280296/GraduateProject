package vn.quang.graduateproject.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vn.quang.graduateproject.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by keban on 9/1/2017.
 */

public class Check {

    public static boolean checkGPS(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS) {
            Utils.shortToast(context, context.getString(R.string.no_gps));
            return false;
        }

        return true;
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Utils.shortToast(context, context.getString(R.string.no_internet));
            return false;
        }

        if (!networkInfo.isConnected()) {
            Utils.shortToast(context, context.getString(R.string.no_internet));
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Utils.shortToast(context, context.getString(R.string.no_internet));
            return false;
        }

        if (!networkInfo.isConnectedOrConnecting()) {
            Utils.shortToast(context, context.getString(R.string.no_internet));
            return false;
        }

        return true;
    }
}
