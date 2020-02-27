package vn.quang.graduateproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import vn.quang.graduateproject.Interface.IDirectionFinder;
import vn.quang.graduateproject.R;

/**
 * Created by keban on 9/12/2017.
 */

public class PlaceInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    String name;
    String address;

    LatLng lng;
    IDirectionFinder listener;

    public PlaceInfoWindowAdapter(Context context, String name, String address, LatLng lng, IDirectionFinder listener) {
        this.context = context;
        this.name = name;
        this.address = address;
        this.lng = lng;
        this.listener = listener;
    }

    @Override
    public View getInfoContents(Marker marker) {

        // Getting view from the layout file info_window_layout
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_place, null);

        // Getting the position from the marker
        LatLng latLng = marker.getPosition();

        // Getting reference to the TextView to set latitude
        TextView lbl_latitude = (TextView) v.findViewById(R.id.lbl_latitude);

        // Getting reference to the TextView to set longitude
        TextView lbl_longitude = (TextView) v.findViewById(R.id.lbl_longitude);

        TextView lbl_name = (TextView) v.findViewById(R.id.lbl_name);
        lbl_name.setText(name);

        TextView lbl_address = (TextView) v.findViewById(R.id.lbl_address);
        lbl_address.setText(address);

        // Setting the latitude
        lbl_latitude.setText(context.getString(R.string.latitude) + latLng.latitude);

        // Setting the longitude
        lbl_longitude.setText(context.getString(R.string.longitude) + latLng.longitude);

        /*v.findViewById(R.id.layout_navigate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DirectionFinder(listener, lng, latLng, "driving").execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });*/

        return v;
    }//getInfoContents

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }//getInfoWindow
}//class
