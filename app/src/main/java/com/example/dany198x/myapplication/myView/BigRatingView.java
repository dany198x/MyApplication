package com.example.dany198x.myapplication.myView;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dany198x.myapplication.Util;

public class BigRatingView extends SimpleRatingView {
    @Override
    public ImageView getRatingView(Context context, int numStars, int posi) {
        ImageView imageView = new ImageView(context);
        int width = Util.dp2px(context, 26);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
        params.setMargins(Util.dp2px(context, 6),0,0,0);
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    public int getCurrentState(float rating, int numStars, int position) {
        position++;
        float dis = position - rating;
        if (dis <= 0) {
            return STATE_FULL;
        }
        if (dis == 0.5) {
            return STATE_FULL;
        }
        if (dis > 0.5) {
            return STATE_NONE;
        }
        return 0;
    }
}
