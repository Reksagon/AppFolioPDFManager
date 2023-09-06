package com.ivanandevs.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.ivanandevs.Utilities.FontType;

import androidx.appcompat.widget.AppCompatEditText;

import com.ivanandevs.R;
import com.ivanandevs.Utilities;

public class ThemeEditText extends AppCompatEditText {

    private FontType fontType;

    public ThemeEditText(Context context) {
        super(context);
        init();
    }

    public ThemeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ThemeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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

