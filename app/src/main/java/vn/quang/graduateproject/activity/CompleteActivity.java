package vn.quang.graduateproject.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.Interface.IDistanceCaculator;
import vn.quang.graduateproject.R;
import vn.quang.graduateproject.listener.OnTouchClickListener;
import vn.quang.graduateproject.model.POI;
import vn.quang.graduateproject.utils.MultiDistanceCaculator;

public class CompleteActivity extends ListActivity implements IDistanceCaculator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        TextView lbl_address = findViewById(R.id.lbl_address);
        String address = "";
        try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> street = gc.getFromLocation(Config.schedule.start.latitude, Config.schedule.start.longitude, 1);

            if (street.size() > 0) {
                address = street.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        lbl_address.setText(address);

        TextView lbl_name = findViewById(R.id.lbl_name);
        lbl_name.setText(Config.schedule.name);

        TextView lbl_startDate = findViewById(R.id.lbl_startDate);
        lbl_startDate.setText(Config.schedule.startTime);

        List<String> items = new ArrayList<>();
        List<LatLng> list = new ArrayList<>();

        for (POI poi : Config.schedule.listPOI) {
            items.add(poi.point.name);
            list.add(new LatLng(poi.point.latitude, poi.point.longitude));
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

        try {
            new MultiDistanceCaculator(this, Config.schedule.start, list, "driving").execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDistanceCaculating(LatLng latLng) {
        Config.schedule.end = latLng;

        findViewById(R.id.btn_go).setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("navigation", true);

                Intent intent = new Intent(CompleteActivity.this, MapsActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
            }
        }, 20, getApplicationContext()));
    }
}
