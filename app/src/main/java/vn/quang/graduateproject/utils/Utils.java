package vn.quang.graduateproject.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import vn.quang.graduateproject.Config;
import vn.quang.graduateproject.R;

/**
 * Created by keban on 3/12/2018.
 */

public class Utils {

    public static void shortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void shortSnackbar(Activity activity, String text) {
        Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }

    public static void longSnackbar(Activity activity, String text) {
        Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
    }

    public static int rand(int min, int max) {
        try {
            Random rn = new Random();
            int range = max - min + 1;
            int randomNum = min + rn.nextInt(range);
            return randomNum;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void setLocale(String lang, Resources resources) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = myLocale;
        resources.updateConfiguration(conf, dm);
    }

    public static void getTime(Context context) {
        String openning_time[] = context.getResources().getStringArray(R.array.openning_time);
        String closing_time[] = context.getResources().getStringArray(R.array.closing_time);

        for (String ot : openning_time)
            Config.openningTime.add(ot);

        for (String ct : closing_time)
            Config.closingTime.add(ct);
    }

    public static String convertDateToString(Date objDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).format(objDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date convertStringToDate(String strDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat, Locale.US).parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertDateStringToString(String strDate, String currentFormat, String parseFormat) {
        try {
            return convertDateToString(convertStringToDate(strDate, currentFormat), parseFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
