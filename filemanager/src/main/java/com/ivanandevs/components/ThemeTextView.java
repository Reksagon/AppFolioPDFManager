package com.ivanandevs.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.ivanandevs.R;
import com.ivanandevs.Utilities;
import com.ivanandevs.Utilities.FontType;

public class ThemeTextView extends AppCompatTextView {

    private FontType fontType;

    public ThemeTextView(Context context) {
        super(context);
        init();
    }

    public ThemeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ThemeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init() {
        setTypeface(Utilities.getTypeface(getContext(), FontType.Light));
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ThemeTextView);
        this.fontType = FontType.values()[styledAttrs.getInt(R.styleable.ThemeTextView_fontType, 1)];
        setTypeface(Utilities.getTypeface(getContext(), fontType));
    }

    public void setFontType(FontType fontType) {
        setTypeface(Utilities.getTypeface(getContext(), fontType));
    }

}

