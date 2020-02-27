package vn.quang.graduateproject.Interface;

import java.util.List;

import vn.quang.graduateproject.model.Point;

/**
 * Created by keban on 4/3/2018.
 */

public interface IGetNearbyPlace {
    void onNearbySearched(List<Point> list);
}
