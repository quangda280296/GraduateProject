package vn.quang.graduateproject;

import android.app.Application;

import vn.quang.graduateproject.utils.Utils;

/**
 * Created by Manh Dang on 02/01/2018.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.getTime(getApplicationContext());
    }
}
