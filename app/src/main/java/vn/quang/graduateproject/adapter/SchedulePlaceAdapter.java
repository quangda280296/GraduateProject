package vn.quang.graduateproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.model.POI;

/**
 * Created by keban on 3/26/2018.
 */

public class SchedulePlaceAdapter extends RecyclerView.Adapter<SchedulePlaceAdapter.RecyclerViewHolder> {

    private List<POI> list = new ArrayList<>();
    private Context context;

    public SchedulePlaceAdapter(List<POI> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void updateList(List<POI> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addItem(POI data) {
        list.add(data);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.row_item_schedule, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.lbl_place.setText("+ " + list.get(position).point.name);
        viewHolder.lbl_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

        /*viewHolder.btn_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(position, viewHolder);
            }
        });*/
    }

    public void displayDialog(int position, RecyclerViewHolder viewHolder) {
        final int[] time = {0};

        LayoutInflater inflater = LayoutInflater.from(context);
        View alertLayout = inflater.inflate(R.layout.dialog_travel_time, null);

        alertLayout.findViewById(R.id.layout_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLayout.findViewById(R.id.rg_time).setVisibility(View.GONE);
                alertLayout.findViewById(R.id.layout_input).setVisibility(View.VISIBLE);
            }
        });

        RadioGroup rg_time = alertLayout.findViewById(R.id.rg_time);
        rg_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_time_1:
                        list.get(position).duration.value = 30;
                        time[0] = 30;
                        break;

                    case R.id.rb_time_2:
                        list.get(position).duration.value = 6;
                        time[0] = 60;
                        break;

                    case R.id.rb_time_3:
                        list.get(position).duration.value = 90;
                        time[0] = 90;
                        break;

                    case R.id.rb_time_4:
                        list.get(position).duration.value = 120;
                        time[0] = 120;
                        break;
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(alertLayout);
        AlertDialog alertDialog = alert.create();

        AlertDialog finalAlertDialog = alertDialog;
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText txt_time = alertLayout.findViewById(R.id.txt_time);

                if (!txt_time.getText().toString().equals(""))
                    time[0] = Integer.parseInt(txt_time.getText().toString());

                //viewHolder.btn_date_start.setText(time[0] + " minutes");
                list.get(position).duration.value = (time[0]);

                finalAlertDialog.hide();
            }
        });

        alertDialog = alert.create();
        alertDialog.show();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView lbl_place;
        private TextView lbl_change;
        //private Button btn_date_start;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            lbl_place = itemView.findViewById(R.id.lbl_place);
            lbl_change = itemView.findViewById(R.id.lbl_search);
            //btn_date_start = itemView.findViewById(R.id.btn_date_start);
        }
    }
}
