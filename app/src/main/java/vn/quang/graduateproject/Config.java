package vn.quang.graduateproject;

import java.util.ArrayList;
import java.util.List;

import vn.quang.graduateproject.model.Point;
import vn.quang.graduateproject.model.Schedule;

/**
 * Created by keban on 3/12/2018.
 */

public class Config {
    public static final String CLOUD_VISION_API_KEY = "AIzaSyAq8eYw5efNLgY9isX52QpjtvJZo-UV6ec";
    public static final String FILE_NAME = "temp.jpg";
    public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    public static final int MAX_LABEL_RESULTS = 10;
    public static final int MAX_DIMENSION = 1200;
    public static final int GALLERY_PERMISSIONS_REQUEST = 0;
    public static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static Schedule schedule;
    public static int[] img =
            {R.mipmap.img_store_1,
                    R.mipmap.img_store_2,
                    R.mipmap.img_store_3,
                    R.mipmap.img_store_4,
                    R.mipmap.img_store_5,
                    R.mipmap.img_store_6,
                    R.mipmap.img_store_7,
                    R.mipmap.img_store_8,
                    R.mipmap.img_store_9,
                    R.mipmap.img_store_10,
                    R.mipmap.img_store_11,
                    R.mipmap.img_store_12,
                    R.mipmap.img_store_13,
                    R.mipmap.img_store_14,
                    R.mipmap.img_store_15,
                    R.mipmap.img_store_16,
                    R.mipmap.img_store_17,
                    R.mipmap.img_store_18,
                    R.mipmap.img_store_19,
                    R.mipmap.img_store_20,
            };

    public static int[] img_dulich =
            {
                    R.mipmap.img_poster_1,
                    R.mipmap.img_poster_2,
                    R.mipmap.img_poster_3,
                    R.mipmap.img_dulich_1,
                    R.mipmap.img_dulich_2,
                    R.mipmap.img_dulich_3,
                    R.mipmap.img_dulich_4,
                    R.mipmap.img_dulich_5,
            };


    public static List<String> openningTime = new ArrayList<>();
    public static List<String> closingTime = new ArrayList<>();
    public static List<Point> listPoint = new ArrayList<>();

    public static String language = "vi";
}
