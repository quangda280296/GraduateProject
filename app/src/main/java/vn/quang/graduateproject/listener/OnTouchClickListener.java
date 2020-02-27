package vn.quang.graduateproject.listener;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import vn.quang.graduateproject.R;

/**
 * Created by Manh Dang on 03/05/2018.
 */

public class OnTouchClickListener implements View.OnTouchListener {
    /**
     * min movement to detect as a click action.
     */
    private int minMove = 20;
    private float startX;
    private float startY;
    private OnClickListener mListener;
    private Context context;

    public OnTouchClickListener(OnClickListener mListener, int minMove, Context context) {
        this.mListener = mListener;
        this.minMove = minMove;
        this.context = context;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > minMove || differenceY > minMove) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Animation zoom_in = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        Animation zoom_out = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                v.startAnimation(zoom_in);
                return true;

            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (isAClick(startX, endX, startY, endY))
                    mListener.onClick(v);
                v.startAnimation(zoom_out);
                break;
        }
        return false;
    }

    public interface OnClickListener {
        void onClick(View v);
    }
}
