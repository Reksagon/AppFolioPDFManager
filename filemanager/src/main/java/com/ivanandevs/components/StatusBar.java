package com.ivanandevs.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.ivanandevs.R;
import com.ivanandevs.Utilities;

public class StatusBar extends LinearLayout {

    private CardView cardView;

    public StatusBar(Context context) {
        super(context);
        init();
    }

    public StatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_status_bar, null);
        cardView = view.findViewById(R.id.cardView);
        initColor();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, Utilities.getStatusBarHeight(getContext()));
        cardView.setLayoutParams(params);
        addView(view);
    }

    public void initColor() {
        if (Build.VERSION.SDK_INT < 23 && !Utilities.isNightTheme()) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorGrayLight));
        } else {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public void setStatusColor(int color) {
        setBackgroundColor(color);
        cardView.setBackgroundColor(color);
    }
}
