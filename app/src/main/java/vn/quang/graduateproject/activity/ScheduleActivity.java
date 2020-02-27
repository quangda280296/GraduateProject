package vn.quang.graduateproject.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.Interface.IGPSHandler;
import vn.quang.graduateproject.R;
import vn.quang.graduateproject.adapter.SchedulePlaceAdapter;
import vn.quang.graduateproject.database.ScheduleDatabase;
import vn.quang.graduateproject.listener.OnTouchClickListener;
import vn.quang.graduateproject.model.POI;
import vn.quang.graduateproject.model.Point;
import vn.quang.graduateproject.model.Schedule;
import vn.quang.graduateproject.utils.GPSHandler;
import vn.quang.graduateproject.utils.Utils;

import static vn.quang.graduateproject.activity.MainActivity.scheduleAdapter;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener, IGPSHandler {

    LatLng start;
    Handler handler = new Handler();
    boolean isFoundMyLocation = false;
    boolean isNew = true;
    int id = -1;
    List<POI> listPOI = new ArrayList<>();
    SchedulePlaceAdapter adapter;
    Calendar calendar_1 = Calendar.getInstance();
    Calendar calendar_2 = Calendar.getInstance();
    int PLACE_PICKER_REQUEST = 1;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isFoundMyLocation)
                return;

            new GPSHandler(ScheduleActivity.this, ScheduleActivity.this).execute();
            handler.postDelayed(this, 500);
        }
    };
    private TextView lbl_address;
    private TextView lbl_change;
    private TextView lbl_add;
    private EditText txt_name;
    private RecyclerView recycler;
    private Button btn_date;
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar_1.set(Calendar.YEAR, year);
            calendar_1.set(Calendar.MONTH, monthOfYear);
            calendar_1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            DateFormat fmtDateAndTime = DateFormat.getDateInstance();
            btn_date.setText(fmtDateAndTime.format(calendar_1.getTime()));
        }
    };
    private Button btn_time;

    /*TimePickerDialog.OnTimeSetListener t3 = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            Button btn_break_start = findViewById(R.id.btn_break_start);
            String n;
            int m = myCalendar.get(Calendar.MINUTE);
            if (m < 10)
                n = m + "0";
            else
                n = String.valueOf(myCalendar.get(Calendar.MINUTE));

            btn_break_start.setText(myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + n);
        }
    };

    TimePickerDialog.OnTimeSetListener t4 = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            Button btn_break_end = findViewById(R.id.btn_break_end);
            String n;
            int m = myCalendar.get(Calendar.MINUTE);
            if (m < 10)
                n = m + "0";
            else
                n = String.valueOf(myCalendar.get(Calendar.MINUTE));

            btn_break_end.setText(myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + n);
        }
    };*/
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar_2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar_2.set(Calendar.MINUTE, minute);

            String n;
            int m = calendar_2.get(Calendar.MINUTE);
            if (m < 10)
                n = m + "0";
            else
                n = String.valueOf(calendar_2.get(Calendar.MINUTE));

            btn_time.setText(calendar_2.get(Calendar.HOUR_OF_DAY) + ":" + n);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        lbl_address = findViewById(R.id.lbl_address);
        lbl_address.setText(R.string.finding_location);

        lbl_change = findViewById(R.id.lbl_change);
        lbl_change.setOnClickListener(this);

        lbl_add = findViewById(R.id.lbl_add);
        lbl_add.setOnClickListener(this);

        txt_name = findViewById(R.id.txt_name);

        btn_date = findViewById(R.id.btn_date);
        btn_time = findViewById(R.id.btn_time);

        btn_date.setOnClickListener(this);
        btn_time.setOnClickListener(this);
        /*findViewById(R.id.btn_break_start).setOnClickListener(this);
        findViewById(R.id.btn_break_end).setOnClickListener(this);*/

        recycler = findViewById(R.id.recycler);
        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);

        // Setting the LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setNestedScrollingEnabled(false);

        // Setting the adapter.
        adapter = new SchedulePlaceAdapter(listPOI, ScheduleActivity.this);
        recycler.setAdapter(adapter);

        findViewById(R.id.btn_apply).setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (adapter.getItemCount() == 0) {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.choose_one_place, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    View view = snack.getView();
                    view.setBackgroundColor(Color.BLACK);
                    ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                    ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
                    snack.show();
                    return;
                }

                if (btn_date.getText().equals(getString(R.string.choose_date))) {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.need_date, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    View view = snack.getView();
                    view.setBackgroundColor(Color.BLACK);
                    ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                    ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
                    snack.show();
                    return;
                }

                if (btn_time.getText().equals(getString(R.string.choose_hour))) {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.need_hours, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    View view = snack.getView();
                    view.setBackgroundColor(Color.BLACK);
                    ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                    ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
                    snack.show();
                    return;
                }

                apply();*/

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
                    startActivityForResult(builder.build(ScheduleActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        }, 20, getApplicationContext()));

        //showSpotlight(findViewById(R.id.btn_apply));

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            handler.post(runnable);
            return;
        }

        id = bundle.getInt("_id", -1);
        isNew = false;

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

        txt_name.setText(Config.schedule.name);

        /*Date d = Utils.convertStringToDate(Config.schedule.startTime, "dd-MM-YYYY HH:mm");
        String date = "";
        String time = "";

        int day_of_month = d.getDate();
        if (day_of_month < 10)
            date += "0" + day_of_month + "-";
        else
            date += day_of_month + "-";

        int month = d.getMonth();
        if (month < 10)
            date += "0" + month + "-";
        else
            date += month + "-";

        int year = d.getYear();
        if (year < 10)
            date += "0" + year;
        else
            date += year;

        int hours = d.getHours();
        if (hours < 10)
            time += "0" + hours + ":";
        else
            time += hours + ":";

        int minutes = d.getMinutes();
        if (minutes < 10)
            time += "0" + minutes;
        else
            time += minutes;

        btn_date.setText(date);
        btn_time.setText(time);*/

        listPOI = Config.schedule.listPOI;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_date:
                new DatePickerDialog(this, d, calendar_1.get(Calendar.YEAR), calendar_1.get(Calendar.MONTH), calendar_1.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.btn_time:
                new TimePickerDialog(this, t, 8, 0, true).show();
                break;

            /*case R.id.btn_break_start:
                new TimePickerDialog(this, t3, 9, 0, true).show();
                break;

            case R.id.btn_break_end:
                new TimePickerDialog(this, t4, 9, 0, true).show();
                break;*/

            case R.id.lbl_add:
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

            case R.id.lbl_change:
                PlacePicker.IntentBuilder build = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(build.build(this), 0);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                Point point = new Point();
                point.name = place.getName().toString();
                point.address = place.getAddress().toString();
                point.latitude = place.getLatLng().latitude;
                point.longitude = place.getLatLng().longitude;

                POI poi = new POI();
                poi.point = point;

                listPOI.add(poi);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                lbl_address.setText(place.getName());
                start = place.getLatLng();
            }
        }
    }

    @Override
    public void onGPS(Location location, String address) {
        isFoundMyLocation = true;
        lbl_address.setText(address);
        start = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void apply() {
        Config.schedule = new Schedule();

        String name = txt_name.getText().toString();
        String startTime = btn_date.getText() + " " + btn_time.getText();
        List<LatLng> list = new ArrayList<>();
        for (POI l : listPOI) {
            list.add(new LatLng(l.point.latitude, l.point.longitude));
        }

        Config.schedule.name = txt_name.getText().toString();
        Config.schedule.start = start;
        Config.schedule.startTime = startTime;
        Config.schedule.listPOI = listPOI;

        ScheduleDatabase database = new ScheduleDatabase(getApplicationContext());

        if (isNew) {
            database.insert(name, startTime, start, list);
            scheduleAdapter.addItem(Config.schedule);
            scheduleAdapter.notifyDataSetChanged();
        } else {
            database.update(id + "", name, startTime, start, list);
            scheduleAdapter.update(id, Config.schedule);
            scheduleAdapter.notifyDataSetChanged();
        }

        startActivity(new Intent(ScheduleActivity.this, CompleteActivity.class));
        finish();
    }

    public void showSpotlight(View view) {
        TapTargetView.showFor(this, TapTarget.forView(view, getString(R.string.confirm), getString(R.string.apply_if_done))
                        // All options below are optional
                        .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .transparentTarget(true),           // Specify whether the target is transparent (displays the content underneath)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {

                        if (adapter.getItemCount() == 0) {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.choose_one_place, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            View v = snack.getView();
                            v.setBackgroundColor(Color.BLACK);
                            ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                            ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
                            snack.show();

                            return;
                        }

                        apply();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Config.schedule = new Schedule();
    }
}
