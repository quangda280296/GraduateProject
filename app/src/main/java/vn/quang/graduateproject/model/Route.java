package vn.quang.graduateproject.model;

import android.text.Spanned;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class Route {

    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<Spanned> listHtmlInstructions = new ArrayList<>();
    public List<Distance> listDistances = new ArrayList<>();
    public List<Duration> listDurations = new ArrayList<>();

    public List<LatLng> listStepsLocations = new ArrayList<>();
    public List<LatLng> listPoints;

    public Route() {

    }
}
