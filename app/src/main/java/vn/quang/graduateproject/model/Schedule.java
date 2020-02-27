package vn.quang.graduateproject.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by keban on 3/26/2018.
 */

public class Schedule {

    public int id;
    public String name;
    public LatLng start;
    public LatLng end;
    public String startTime;
    public List<POI> listPOI;

    public Schedule() {

    }
}
