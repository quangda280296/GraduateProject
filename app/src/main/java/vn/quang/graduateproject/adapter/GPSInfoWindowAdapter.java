package vn.quang.graduateproject.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.widget.CircleImage;

/**
 * Created by keban on 9/12/2017.
 */

public class GPSInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    String name;
    String address;
    int image;

    public GPSInfoWindowAdapter(Context context, String name, String address, int image) {

        this.context = context;
        this.name = name;
        this.address = address;
        this.image = image;
    }

    @Override
    public View getInfoContents(Marker marker) {

        // Getting view from the layout file info_window_layout
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_gps, null);

        // Getting the position from the marker
        LatLng latLng = marker.getPosition();

        // Getting reference to the TextView to set latitude
        TextView lbl_latitude = (TextView) v.findViewById(R.id.lbl_latitude);

        // Getting reference to the TextView to set longitude
        TextView lbl_longitude = (TextView) v.findViewById(R.id.lbl_longitude);

        TextView lbl_name = (TextView) v.findViewById(R.id.lbl_name);
        lbl_name.setText(name);

        Typeface myNewFace = Typeface.createFromAsset(context.getAssets(), "fonts/mistral.TTF");
        lbl_name.setTypeface(myNewFace);

        TextView lbl_address = (TextView) v.findViewById(R.id.lbl_address);
        lbl_address.setText(address);

        CircleImage img_avatar = (CircleImage) v.findViewById(R.id.img_avatar);
        img_avatar.setImageResource(image);

        // Setting the latitude
        lbl_latitude.setText(context.getString(R.string.latitude) + latLng.latitude);

        // Setting the longitude
        lbl_longitude.setText(context.getString(R.string.longitude) + latLng.longitude);

        return v;
    }//getInfoContents

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }//getInfoWindow
}//class
