package vn.quang.graduateproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vn.quang.graduateproject.utils.Check;

/**
 * Created by keban on 9/1/2017.
 */

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Check.checkInternetConnection(context)) {

        }
    }

}
