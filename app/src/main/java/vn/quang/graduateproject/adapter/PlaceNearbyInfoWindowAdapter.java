package vn.quang.graduateproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import vn.quang.graduateproject.R;

/**
 * Created by keban on 9/12/2017.
 */

public class PlaceNearbyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    String name;
    String address;
    String openningTime;
    String closingTime;
    int image;

    public PlaceNearbyInfoWindowAdapter(Context context, String name, String address, String openningTime, String closingTime, int image) {
        this.context = context;
        this.name = name;
        this.address = address;
        this.openningTime = openningTime;
        this.closingTime = closingTime;
        this.image = image;
    }

    @Override
    public View getInfoContents(Marker marker) {

        // Getting view from the layout file info_window_layout
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_info, null);

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

        ImageView img_avatar = (ImageView) v.findViewById(R.id.img_avatar);
        img_avatar.setImageResource(image);

        // Setting the latitude
        lbl_latitude.setText(context.getString(R.string.latitude) + latLng.latitude);

        // Setting the longitude
        lbl_longitude.setText(context.getString(R.string.longitude) + latLng.longitude);

        TextView lbl_openning_time = (TextView) v.findViewById(R.id.lbl_openning_time);
        TextView lbl_closing_time = (TextView) v.findViewById(R.id.lbl_closing_time);

        lbl_openning_time.setText(context.getString(R.string.openning_time) + openningTime);
        lbl_closing_time.setText(context.getString(R.string.closing_time) + closingTime);

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
