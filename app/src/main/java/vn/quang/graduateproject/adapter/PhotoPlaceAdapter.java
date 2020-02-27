package vn.quang.graduateproject.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.activity.WebviewActivity;
import vn.quang.graduateproject.model.Analysis;

/**
 * Created by keban on 3/26/2018.
 */

public class PhotoPlaceAdapter extends RecyclerView.Adapter<PhotoPlaceAdapter.RecyclerViewHolder> {

    Intent intent;
    private List<Analysis> list = new ArrayList<>();
    private Activity context;

    public PhotoPlaceAdapter(List<Analysis> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    public void updateList(List<Analysis> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addItem(Analysis data) {
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
        View itemView = inflater.inflate(R.layout.row_item_text, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.lbl_percent.setText(list.get(position).percent);
        viewHolder.lbl_place.setText(list.get(position).place);
        viewHolder.lbl_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, WebviewActivity.class);
                intent.putExtra("q", list.get(position).place);
                context.startActivity(intent);
            }
        });
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView lbl_percent;
        private TextView lbl_place;
        private TextView lbl_change;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            lbl_percent = itemView.findViewById(R.id.lbl_percent);
            lbl_place = itemView.findViewById(R.id.lbl_place);
            lbl_change = itemView.findViewById(R.id.lbl_search);
        }
    }
}
