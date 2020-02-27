package vn.quang.graduateproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.R;
import vn.quang.graduateproject.adapter.CameraAdapter;
import vn.quang.graduateproject.adapter.ScheduleAdapter;
import vn.quang.graduateproject.database.CameraDatabase;
import vn.quang.graduateproject.database.ScheduleDatabase;
import vn.quang.graduateproject.fragment.MainFragment_1;
import vn.quang.graduateproject.fragment.MainFragment_2;
import vn.quang.graduateproject.fragment.MainFragment_3;
import vn.quang.graduateproject.fragment.MainFragment_4;
import vn.quang.graduateproject.fragment.MainFragment_5;
import vn.quang.graduateproject.fragment.MainFragment_6;
import vn.quang.graduateproject.fragment.MainFragment_7;
import vn.quang.graduateproject.fragment.MainFragment_8;
import vn.quang.graduateproject.model.POI;
import vn.quang.graduateproject.model.Photo;
import vn.quang.graduateproject.model.Schedule;
import vn.quang.graduateproject.utils.PermissionUtils;
import vn.quang.graduateproject.utils.Utils;

import static vn.quang.graduateproject.Config.CAMERA_IMAGE_REQUEST;
import static vn.quang.graduateproject.Config.CAMERA_PERMISSIONS_REQUEST;
import static vn.quang.graduateproject.Config.FILE_NAME;
import static vn.quang.graduateproject.Config.GALLERY_IMAGE_REQUEST;
import static vn.quang.graduateproject.Config.GALLERY_PERMISSIONS_REQUEST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static List<Photo> photoList = new ArrayList<>();

    public static CameraDatabase camera;
    public static CameraAdapter cameraAdapter;
    public static ScheduleAdapter scheduleAdapter;
    Handler handler = new Handler();
    int index = 0;
    private ImageView img_poster_1;
    private ImageView img_poster_2;
    Runnable code_2 = new Runnable() {
        @Override
        public void run() {
            if (index == 7)
                index = 0;
            else
                index++;

            img_poster_1.setImageResource(Config.img_dulich[index]);
            img_poster_1.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.SlideOutLeft)
                    .duration(1000)
                    .playOn(findViewById(R.id.img_poster_2));

            YoYo.with(Techniques.SlideInRight)
                    .duration(1000)
                    .playOn(findViewById(R.id.img_poster_1));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_poster_2.setVisibility(View.GONE);
                }
            }, 1000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.post(code_1);
                }
            }, 2000);
        }
    };
    Runnable code_1 = new Runnable() {
        @Override
        public void run() {
            if (index == 7)
                index = 0;
            else
                index++;

            img_poster_2.setImageResource(Config.img_dulich[index]);
            img_poster_2.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.SlideOutLeft)
                    .duration(1000)
                    .playOn(findViewById(R.id.img_poster_1));

            YoYo.with(Techniques.SlideInRight)
                    .duration(1000)
                    .playOn(findViewById(R.id.img_poster_2));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_poster_1.setVisibility(View.GONE);
                }
            }, 1000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.post(code_2);
                }
            }, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout_main = findViewById(R.id.layout_main);
        LinearLayout layout_map = findViewById(R.id.layout_map);
        RelativeLayout layout_schedule = findViewById(R.id.layout_schedule);
        RelativeLayout layout_camera = findViewById(R.id.layout_camera);

        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_gallery).setOnClickListener(this);

        findViewById(R.id.btn_place_picker).setOnClickListener(this);

        setupMap();
        setupAdapter();
        setupCameraAdapter();
        setupScheduleAdapter();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        layout_main.setVisibility(View.VISIBLE);
                        layout_map.setVisibility(View.GONE);
                        layout_schedule.setVisibility(View.GONE);
                        layout_camera.setVisibility(View.GONE);
                        return true;

                    case R.id.navigation_map:
                        layout_main.setVisibility(View.GONE);
                        layout_map.setVisibility(View.VISIBLE);
                        layout_schedule.setVisibility(View.GONE);
                        layout_camera.setVisibility(View.GONE);
                        return true;

                    case R.id.navigation_schedule:
                        layout_main.setVisibility(View.GONE);
                        layout_map.setVisibility(View.GONE);
                        layout_schedule.setVisibility(View.VISIBLE);
                        layout_camera.setVisibility(View.GONE);
                        return true;

                    case R.id.navigation_camera:
                        layout_main.setVisibility(View.GONE);
                        layout_map.setVisibility(View.GONE);
                        layout_schedule.setVisibility(View.GONE);
                        layout_camera.setVisibility(View.VISIBLE);
                        return true;
                }

                return false;
            }
        });

        img_poster_1 = findViewById(R.id.img_poster_1);
        img_poster_2 = findViewById(R.id.img_poster_2);
    }

    public void setupAdapter() {
        if (Locale.getDefault().getLanguage().equals("vi")) {
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("Kênh 14", MainFragment_1.class)
                    .add("Âu Việt Travel", MainFragment_2.class)
                    .add("Zing", MainFragment_3.class)
                    .add("Saigon Tourist", MainFragment_4.class)
                    .create());

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);

            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
        } else {
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("Hanoi", MainFragment_5.class)
                    .add("Travel", MainFragment_6.class)
                    .add("New York Times", MainFragment_7.class)
                    .add("Emeralda Resort", MainFragment_8.class)
                    .create());

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);

            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
        }
    }

    public void setupMap() {
        findViewById(R.id.layout_go_to_map).setOnClickListener(this);

        findViewById(R.id.restaurant).setOnClickListener(this);
        findViewById(R.id.cafe).setOnClickListener(this);
        findViewById(R.id.hotel).setOnClickListener(this);
        findViewById(R.id.pharmacy).setOnClickListener(this);
        findViewById(R.id.supermarket).setOnClickListener(this);
        findViewById(R.id.travel_agent).setOnClickListener(this);

        new Handler().postDelayed(code_1, 1000);
    }

    public void setupCameraAdapter() {
        this.photoList = new ArrayList<>();

        camera = new CameraDatabase(getApplicationContext());
        Cursor cursor = camera.getAll();

        for (int i = 0; i < cursor.getCount(); i++) {
            Photo photo = new Photo();

            cursor.moveToPosition(i);
            photo.id = cursor.getInt(0);
            photo.name = cursor.getString(1);
            photo.date = Utils.convertStringToDate(cursor.getString(2), "EEE MMM dd HH:mm:ss z yyyy");
            photo.uri = Uri.parse(cursor.getString(3));

            this.photoList.add(photo);
        }

        RecyclerView recycler = findViewById(R.id.recycler_camera);
        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);

        // Setting the LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        // Setting the adapter.
        cameraAdapter = new CameraAdapter(this.photoList, MainActivity.this);
        recycler.setAdapter(cameraAdapter);
    }

    public void setupScheduleAdapter() {
        List<Schedule> list = new ArrayList<>();

        ScheduleDatabase database = new ScheduleDatabase(getApplicationContext());
        Cursor cursor = database.getAll();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            Schedule schedule = new Schedule();
            schedule.id = cursor.getInt(0);
            schedule.name = cursor.getString(1);
            schedule.start = new LatLng(cursor.getDouble(2), cursor.getDouble(3));
            schedule.startTime = cursor.getString(4);

            schedule.listPOI = new ArrayList<>();
            int id = cursor.getInt(0);
            Cursor c = database.getAllPOI(id);

            for (int j = 0; j < c.getCount(); j++) {
                c.moveToPosition(j);
                POI poi = new POI();
                poi.point.latitude = cursor.getDouble(2);
                poi.point.longitude = cursor.getDouble(3);

                String name = "";
                try {
                    Geocoder gc = new Geocoder(this, Locale.getDefault());
                    List<Address> street = gc.getFromLocation(poi.point.latitude, poi.point.longitude, 1);

                    if (street.size() > 0) {
                        name = street.get(0).getAddressLine(0);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                poi.point.name = name;

                schedule.listPOI.add(poi);
            }

            list.add(schedule);
        }

        RecyclerView recycler = findViewById(R.id.recycler_schedule);
        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);

        // Setting the LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        // Setting the adapter.
        scheduleAdapter = new ScheduleAdapter(list, MainActivity.this);
        recycler.setAdapter(scheduleAdapter);
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle bundle = new Bundle();
            bundle.putString("uri", data.getData().toString());
            Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());

            Bundle bundle = new Bundle();
            bundle.putString("uri", photoUri.toString());
            Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();

        /*Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Vui lòng đợi...", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View view = snackbar.getView();
        view.setBackgroundColor(Color.BLACK);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);*/

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.no_gps), Snackbar.LENGTH_SHORT).setAction("Bật", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnGPS();
            }
        });

        View view_ = snack.getView();
        view_.setBackgroundColor(Color.BLACK);
        ((TextView) view_.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) view_.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);

        switch (id) {
            case R.id.btn_place_picker:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                break;

            case R.id.layout_go_to_map:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                startActivity(intent);
                break;

            case R.id.btn_camera:
                startCamera();
                break;

            case R.id.btn_gallery:
                startGalleryChooser();
                break;

            case R.id.restaurant:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                bundle.putString("type", "restaurant");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.cafe:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                bundle.putString("type", "cafe");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.hotel:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                bundle.putString("type", "lodging");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.pharmacy:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                bundle.putString("type", "pharmacy");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.supermarket:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                bundle.putString("type", "supermarket");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.travel_agent:
                if (!statusOfGPS) {
                    snack.show();
                    return;
                }

                bundle.putString("type", "travel_agency");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
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
                            status.startResolutionForResult(MainActivity.this, 1000);
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
}
