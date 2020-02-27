package vn.quang.graduateproject.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.Interface.IDirectionFinder;
import vn.quang.graduateproject.Interface.IGPSHandler;
import vn.quang.graduateproject.Interface.IGetNearbyPlace;
import vn.quang.graduateproject.Interface.INavigationFinder;
import vn.quang.graduateproject.R;
import vn.quang.graduateproject.adapter.GPSInfoWindowAdapter;
import vn.quang.graduateproject.adapter.GuideAdapter;
import vn.quang.graduateproject.adapter.GuideInfoWindowAdapter;
import vn.quang.graduateproject.adapter.PlaceInfoWindowAdapter;
import vn.quang.graduateproject.adapter.PlaceNearbyInfoWindowAdapter;
import vn.quang.graduateproject.model.POI;
import vn.quang.graduateproject.model.Point;
import vn.quang.graduateproject.model.Route;
import vn.quang.graduateproject.utils.DirectionFinder;
import vn.quang.graduateproject.utils.GPSHandler;
import vn.quang.graduateproject.utils.GetNearbyPlace;
import vn.quang.graduateproject.utils.MultiNavigationFinder;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, IGPSHandler, IGetNearbyPlace,
        View.OnClickListener, IDirectionFinder, INavigationFinder {

    boolean ok = false;
    Handler handler = new Handler();
    int PLACE_PICKER_REQUEST = 1;
    List<Marker> listMarker = new ArrayList<>();
    List<Marker> stepsLocation = new ArrayList<>();
    List<Point> list = new ArrayList<>();
    List<Polyline> polylinePaths = new ArrayList<>();
    Marker m;
    LatLng lng;
    Place place;
    Route route;
    String type;

    List<LatLng> listLatLng;

    Runnable gps = new Runnable() {
        @Override
        public void run() {
            if (!ok) {
                new GPSHandler(MapsActivity.this, MapsActivity.this).execute();
                handler.postDelayed(this, 500);
            }
        }
    };

    Location myLocation;
    String myAddress;
    private GoogleMap mMap;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(160);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    //findViewById(R.id.btn_navigation).setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_search).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                //findViewById(R.id.btn_navigation).setVisibility(View.INVISIBLE);
                findViewById(R.id.layout_search).setVisibility(View.INVISIBLE);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.layout_search).setOnClickListener(this);
        //findViewById(R.id.img_voice).setOnClickListener(this);
        //findViewById(R.id.btn_navigation).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            return;

        if (!bundle.containsKey("navigation")) {
            handler.post(gps);
            return;
        }

        listLatLng = new ArrayList<>();
        for (POI poi : Config.schedule.listPOI) {
            listLatLng.add(new LatLng(poi.point.latitude, poi.point.longitude));
        }

        try {
            new MultiNavigationFinder(this, Config.schedule.start, Config.schedule.end, "driving", listLatLng).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!statusOfGPS)
                    turnOnGPS();

                return false;
            }
        });
    }

    @Override
    public void onGPS(Location location, String address) {
        ok = true;

        myLocation = location;
        myAddress = address;

        LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
        this.lng = lng;

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();

        if (myBundle != null && myBundle.containsKey("type")) {
            //extract the individual data parts of the bundle
            type = myBundle.getString("type");
            Config.listPoint.clear();
            new GetNearbyPlace(getApplicationContext(), lng, type, "", MapsActivity.this).execute();
        }

        //Thêm MarkerOption cho Map:
        MarkerOptions option = new MarkerOptions();
        option.position(lng);
        option.snippet("GPS");
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        Marker marker = mMap.addMarker(option);

        //thiết lập Info cho Map
        mMap.setInfoWindowAdapter(new GPSInfoWindowAdapter(this, "I'm here", address, R.mipmap.img_travel));

        //Hiển thị vòng tròn bán kính
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(lng);

        //radius in meters
        circleOptions.radius(50000);
        circleOptions.fillColor(getResources().getColor(R.color.circle_on_map));
        circleOptions.strokeColor(getResources().getColor(R.color.circle_on_map));
        circleOptions.strokeWidth(0);

        mMap.addCircle(circleOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lng)                // Sets the center of the map to location user
                .zoom(15) // Sets the zoom
//                          .bearing(90)       // Sets the orientation of the camera to east
                .tilt(40)                      // Sets the tilt of the camera to 30 degrees
                .build();                      // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Tiến hành hiển thị lên Custom marker option lên Map:
        marker.showInfoWindow();
    }

    @Override
    public void onNearbySearched(List<Point> list) {
        this.list = list;
        listMarker = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            //Thêm MarkerOption cho Map:
            MarkerOptions option = new MarkerOptions();
            option.snippet(i + "");
            Bitmap bitmap;

            switch (type) {
                case "restaurant":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location_restaurant);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    option.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;

                case "cafe":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location_cafe);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    option.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;

                case "lodging":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location_hotel);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    option.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;

                case "pharmacy":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location_pharmacy);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    option.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;

                case "supermarket":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location_supermarket);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    option.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;

                case "travel_agency":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location_travel_agent);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    option.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;
            }

            LatLng lng = new LatLng(list.get(i).latitude, list.get(i).longitude);

            option.position(lng);
            Marker m = mMap.addMarker(option);
            this.listMarker.add(m);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getSnippet().equals("GPS")) {
            //thiết lập Info cho Map
            mMap.setInfoWindowAdapter(new GPSInfoWindowAdapter
                    (MapsActivity.this, "I'm here",
                            myAddress, R.mipmap.img_travel));

            //Tiến hành hiển thị lên Custom marker option lên Map:
            marker.showInfoWindow();
            return true;
        } //if

        else if (marker.getSnippet().equals("place")) {
            //thiết lập Info cho Map
            mMap.setInfoWindowAdapter(new PlaceInfoWindowAdapter
                    (MapsActivity.this,
                            place.getName().toString(),
                            place.getAddress().toString(), lng, this));

            //Tiến hành hiển thị lên Custom marker option lên Map:
            marker.showInfoWindow();
            return true;

        } else if (marker.getSnippet().equals("step")) {
            //thiết lập Info cho Map
            int title = Integer.parseInt(marker.getTitle());
            for (int i = 0; i < route.listStepsLocations.size(); i++) {
                if (i == title) {
                    mMap.setInfoWindowAdapter(new GuideInfoWindowAdapter(
                            MapsActivity.this,
                            route.listHtmlInstructions.get(i).toString(),
                            route.listDistances.get(i).text,
                            route.listDurations.get(i).text));

                    //Tiến hành hiển thị lên Custom marker option lên Map:
                    marker.showInfoWindow();
                    return true;
                }
            }

        } else {
            int snippet = Integer.parseInt(marker.getSnippet());
            for (int i = 0; i < list.size(); i++) {
                if (i == snippet) {
                    //thiết lập Info cho Map
                    mMap.setInfoWindowAdapter(new PlaceNearbyInfoWindowAdapter
                            (MapsActivity.this,
                                    list.get(i).name,
                                    list.get(i).address,
                                    list.get(i).openningTime,
                                    list.get(i).closingTime,
                                    list.get(i).image));

                    //Tiến hành hiển thị lên Custom marker option lên Map:
                    marker.showInfoWindow();

                    if (polylinePaths != null)
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }

                    if (stepsLocation != null) {
                        try {
                            for (Marker m : stepsLocation) {
                                stepsLocation.remove(m);
                                m.remove();
                            }

                        } catch (Exception e) {

                        }
                    }

                    try {
                        lng = new LatLng(list.get(i).latitude, list.get(i).longitude);
                        new DirectionFinder(this, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                                lng, "driving").execute();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    return true;
                }//if
            }//for
        }

        return false;
    }

    public void turnOnGPS() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        builder.setAlwaysShow(true); // this is the key ingredient

        PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback() {

            @Override
            public void onResult(Result result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set fullscreen
        findViewById(R.id.root).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.layout_search:
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.please_wait, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                View view = snack.getView();
                view.setBackgroundColor(Color.BLACK);
                ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
                snack.show();

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

            /*case R.id.img_voice:
                break;*/

            /*case R.id.btn_navigation:
                findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                setupBottomSheet();
                break;*/
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                findViewById(R.id.root).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                place = PlacePicker.getPlace(this, data);
                //Thêm MarkerOption cho Map:
                MarkerOptions option = new MarkerOptions();
                option.snippet("place");

                if (m != null)
                    m.remove();

                if (listMarker != null) {
                    try {
                        for (Marker marker : listMarker) {
                            marker.remove();
                        }

                    } catch (Exception e) {

                    }
                }

                if (polylinePaths != null)
                    for (Polyline polyline : polylinePaths) {
                        polyline.remove();
                    }

                if (stepsLocation != null) {
                    try {
                        for (Marker marker : stepsLocation) {
                            stepsLocation.remove(marker);
                            marker.remove();
                        }

                    } catch (Exception e) {

                    }
                }

                lng = place.getLatLng();
                option.position(lng);
                m = mMap.addMarker(option);

                try {
                    new DirectionFinder(this, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), lng, "driving").execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startFinding(String mode) {
        if (polylinePaths != null)
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }

        if (stepsLocation != null) {
            try {
                for (Marker marker : stepsLocation) {
                    stepsLocation.remove(marker);
                    marker.remove();
                }

            } catch (Exception e) {

            }
        }

        try {
            new DirectionFinder(this, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), lng, mode).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderSuccess(Route route) {
        findViewById(R.id.root).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        List<String> items = new ArrayList<>();
        this.route = route;

        findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_driving:
                        startFinding("driving");
                        return true;

                    case R.id.navigation_walking:
                        startFinding("walking");
                        return true;
                }

                return false;
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
        ((TextView) findViewById(R.id.lbl_duration)).setText(route.duration.text);
        ((TextView) findViewById(R.id.lbl_distance)).setText(route.distance.text);

        for (int i = 0; i < route.listStepsLocations.size(); i++)
            stepsLocation.add(mMap.addMarker(new MarkerOptions()
                    .snippet("step")
                    .title(i + "")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                    .position(route.listStepsLocations.get(i))));

        PolylineOptions mainPolylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);

        mainPolylineOptions.add(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        for (int i = 0; i < route.listPoints.size(); i++)
            mainPolylineOptions.add(route.listPoints.get(i));

        polylinePaths.add(mMap.addPolyline(mainPolylineOptions));

        Log.d("ppppppp", route.listHtmlInstructions.size() + " list");

        for (int i = 0; i < route.listHtmlInstructions.size(); i++) {
            items.add(route.listHtmlInstructions.get(i).toString());
        }

        RecyclerView recycler = findViewById(R.id.recycler);
        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);
        // Setting the LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setNestedScrollingEnabled(false);

        NestedScrollView scrollView = findViewById(R.id.bottom_sheet);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0, 0);

        GuideAdapter a = new GuideAdapter(MapsActivity.this, route, mMap, items, mBottomSheetBehavior, stepsLocation, scrollView);
        recycler.setAdapter(a);
    }

    public void startMuti(String mode) {
        try {
            new MultiNavigationFinder(this, Config.schedule.start, Config.schedule.end, mode, listLatLng).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationFinderSuccess(Route route) {
        findViewById(R.id.root).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        List<String> items = new ArrayList<>();
        this.route = route;

        findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_driving:
                        startMuti("driving");
                        return true;

                    case R.id.navigation_walking:
                        startMuti("walking");
                        return true;
                }

                return false;
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
        ((TextView) findViewById(R.id.lbl_duration)).setText(route.duration.text);
        ((TextView) findViewById(R.id.lbl_distance)).setText(route.distance.text);

        for (int i = 0; i < route.listStepsLocations.size(); i++)
            stepsLocation.add(mMap.addMarker(new MarkerOptions()
                    .snippet("step")
                    .title(i + "")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                    .position(route.listStepsLocations.get(i))));

        PolylineOptions mainPolylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);

        mainPolylineOptions.add(route.startLocation);
        for (int i = 0; i < route.listPoints.size(); i++)
            mainPolylineOptions.add(route.listPoints.get(i));

        polylinePaths.add(mMap.addPolyline(mainPolylineOptions));

        Log.d("ppppppp", route.listHtmlInstructions.size() + " list");

        for (int i = 0; i < route.listHtmlInstructions.size(); i++) {
            items.add(route.listHtmlInstructions.get(i).toString());
        }

        RecyclerView recycler = findViewById(R.id.recycler);
        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);
        // Setting the LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setNestedScrollingEnabled(false);

        NestedScrollView scrollView = findViewById(R.id.bottom_sheet);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0, 0);

        GuideAdapter a = new GuideAdapter(MapsActivity.this, route, mMap, items, mBottomSheetBehavior, stepsLocation, scrollView);
        recycler.setAdapter(a);
    }
}
