package vn.quang.graduateproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import vn.quang.graduateproject.R;

/**
 * Created by keban on 9/12/2017.
 */

public class GuideInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    String guide;
    String distance;
    String duration;

    public GuideInfoWindowAdapter(Context context, String guide, String distance, String duration) {
        this.context = context;
        this.guide = guide;
        this.distance = distance;
        this.duration = duration;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file info_window_layout
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_guide, null);

        // Getting reference to the TextView to set latitude
        TextView lbl_guide = (TextView) v.findViewById(R.id.lbl_guide);
        lbl_guide.setText("-> " + guide);

        // Getting reference to the TextView to set longitude
        TextView lbl_duration = (TextView) v.findViewById(R.id.lbl_duration);
        lbl_duration.setText(context.getString(R.string.duration) + duration);

        TextView lbl_distance = (TextView) v.findViewById(R.id.lbl_distance);
        lbl_distance.setText(context.getString(R.string.distance) + distance);

        return v;

    }//getInfoContents

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }//getInfoWindow
}//class
