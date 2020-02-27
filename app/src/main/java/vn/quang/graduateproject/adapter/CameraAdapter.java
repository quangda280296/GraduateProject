package vn.quang.graduateproject.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.activity.AnalysisActivity;
import vn.quang.graduateproject.activity.MainActivity;
import vn.quang.graduateproject.database.CameraDatabase;
import vn.quang.graduateproject.model.Photo;
import vn.quang.graduateproject.utils.Utils;

/**
 * Created by keban on 3/26/2018.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.RecyclerViewHolder> {

    private List<Photo> list = new ArrayList<>();
    private Context context;

    public CameraAdapter(List<Photo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void update(int position, Photo photo) {
        list.set(position, photo);
        notifyDataSetChanged();
    }

    public void addItem(Photo data) {
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
        viewHolder.layout_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uri", list.get(position).uri.toString());
                bundle.putBoolean("new", false);
                Intent intent = new Intent(context, AnalysisActivity.class);
                intent.putExtras(bundle);

                if (context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.startActivity(intent);
                }
            }
        });

        viewHolder.layout_click.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View alertLayout = inflater.inflate(R.layout.layout_dialog, null);

                CameraDatabase camera = new CameraDatabase(context);

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(alertLayout);

                alertLayout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        camera.delete(list.get(position).id + "");

                        list.remove(position);
                        notifyDataSetChanged();

                        Utils.shortToast(context, context.getString(R.string.deleted));
                        dialog.cancel();
                    }
                });

                alertLayout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertLayout.findViewById(R.id.layout_choice).setVisibility(View.GONE);
                        alertLayout.findViewById(R.id.layout_edit).setVisibility(View.VISIBLE);
                    }
                });

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText txt_name = alertLayout.findViewById(R.id.txt_name);
                        String name = txt_name.getText().toString();

                        if (name.equals("")) {
                            dialog.cancel();
                            return;
                        }

                        list.get(position).name = name;
                        notifyDataSetChanged();
                        camera.update(list.get(position).id + "", name);
                    }
                });

                dialog = alert.create();
                dialog.show();

                return true;
            }
        });

        String string = Utils.convertDateToString(list.get(position).date, "EEE, dd MMM yyyy HH:mm:ss");
        Glide.with(context).load(list.get(position).uri).into(viewHolder.img_thumbnail);

        if (list.get(position).name.equals(""))
            viewHolder.lbl_title.setText(context.getString(R.string.image) + " #" + position + "\n\n" + string);
        else
            viewHolder.lbl_title.setText(list.get(position).name + "\n\n" + string);
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
