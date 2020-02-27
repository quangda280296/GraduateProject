package vn.quang.graduateproject.adapter;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.model.Route;

/**
 * Created by keban on 3/26/2018.
 */

public class GuideNavigationAdapter extends RecyclerView.Adapter<GuideNavigationAdapter.RecyclerViewHolder> {

    private Context context;
    private Route route;
    private GoogleMap mMap;
    private List<String> list = new ArrayList<>();
    private BottomSheetBehavior mBottomSheetBehavior;
    private List<Marker> marker;
    private NestedScrollView scrollView;

    public GuideNavigationAdapter(Context context, Route route, GoogleMap mMap, List<String> list, BottomSheetBehavior mBottomSheetBehavior, List<Marker> marker, NestedScrollView scrollView) {
        this.context = context;
        this.route = route;
        this.mMap = mMap;
        this.list = list;
        this.mBottomSheetBehavior = mBottomSheetBehavior;
        this.marker = marker;
        this.scrollView = scrollView;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.row_listview, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.lbl_guide.setText(list.get(position) + "(" + route.listDistances.get(position).text + " ~ " + route.listDurations.get(position).text + ")");
        viewHolder.guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //thiết lập Info cho Map
                for (int i = 0; i < route.listStepsLocations.size(); i++) {
                    if (i == position) {
                        mMap.setInfoWindowAdapter(new GuideInfoWindowAdapter(
                                context,
                                route.listHtmlInstructions.get(i).toString(),
                                route.listDistances.get(i).text,
                                route.listDurations.get(i).text));

                        //Tiến hành hiển thị lên Custom marker option lên Map:
                        marker.get(position).showInfoWindow();
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        scrollView.smoothScrollTo(0, 0);
                    }
                }
            }
        });
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView lbl_guide;
        private LinearLayout guide;
        private TextView lbl_title;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            lbl_guide = itemView.findViewById(R.id.lbl_guide);
            guide = itemView.findViewById(R.id.guide);
            lbl_title = itemView.findViewById(R.id.lbl_title);
        }
    }
}
