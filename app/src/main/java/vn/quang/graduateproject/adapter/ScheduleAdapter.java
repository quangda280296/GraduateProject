package vn.quang.graduateproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.R;
import vn.quang.graduateproject.activity.CompleteActivity;
import vn.quang.graduateproject.activity.MainActivity;
import vn.quang.graduateproject.activity.ScheduleActivity;
import vn.quang.graduateproject.database.ScheduleDatabase;
import vn.quang.graduateproject.model.Schedule;
import vn.quang.graduateproject.utils.Utils;

/**
 * Created by keban on 3/26/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.RecyclerViewHolder> {

    private List<Schedule> list = new ArrayList<>();
    private Context context;

    public ScheduleAdapter(List<Schedule> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void update(int position, Schedule schedule) {
        list.set(position, schedule);
        notifyDataSetChanged();
    }

    public void addItem(Schedule data) {
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
        View itemView = inflater.inflate(R.layout.row_item_image, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    AlertDialog dialog;

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.lbl_title.setText(list.get(position).name + "\n\n" + context.getString(R.string.start) + "\n" + list.get(position).startTime);

        viewHolder.layout_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.schedule = new Schedule();
                Config.schedule.name = list.get(position).name;
                Config.schedule.start = list.get(position).start;
                Config.schedule.startTime = list.get(position).startTime;
                Config.schedule.listPOI = list.get(position).listPOI;

                if (context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.startActivity(new Intent(context, CompleteActivity.class));
                }
            }
        });

        viewHolder.layout_click.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View alertLayout = inflater.inflate(R.layout.layout_dialog, null);

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(alertLayout);

                TextView lbl_edit = alertLayout.findViewById(R.id.lbl_edit);
                lbl_edit.setText(R.string.edit);

                alertLayout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleDatabase schedule = new ScheduleDatabase(context);
                        schedule.delete(list.get(position).id + "");

                        list.remove(position);
                        notifyDataSetChanged();

                        Utils.shortToast(context, context.getString(R.string.deleted));
                        dialog.cancel();
                    }
                });

                alertLayout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Config.schedule = new Schedule();
                        Config.schedule.name = list.get(position).name;
                        Config.schedule.start = list.get(position).start;
                        Config.schedule.startTime = list.get(position).startTime;
                        Config.schedule.listPOI = list.get(position).listPOI;

                        Bundle bundle = new Bundle();
                        bundle.putInt("_id", list.get(position).id);

                        Intent intent = new Intent(context, ScheduleActivity.class);
                        intent.putExtras(bundle);

                        if (context instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity) context;
                            mainActivity.startActivity(intent);
                        }
                    }
                });

                dialog = alert.create();
                dialog.show();

                return true;
            }
        });
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout_click;
        private ImageView img_thumbnail;
        private TextView lbl_title;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            layout_click = itemView.findViewById(R.id.layout_click);
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail);
            lbl_title = itemView.findViewById(R.id.lbl_title);
        }
    }
}
