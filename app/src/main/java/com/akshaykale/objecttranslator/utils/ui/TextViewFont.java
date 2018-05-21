package com.akshaykale.objecttranslator.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.akshaykale.objecttranslator.R;

public class TextViewFont extends AppCompatTextView {

    public TextViewFont(Context context) {
        super(context);
        setUp(null);
    }

    public TextViewFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs);
    }

    public TextViewFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(attrs);
    }

    private void setUp(AttributeSet set) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Nunito-Regular.ttf");
        if (set == null) {
            setTypeface(tf);
        }else {
            TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.TextViewFont, 0, 0);
            try {
                if (ta.hasValue(R.styleable.TextViewFont_fontStyle)) {
                    int value = ta.getInt(R.styleable.TextViewFont_fontStyle, 0);
                    if (value == 1){
                        setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                                "fonts/Nunito-Light.ttf"));
                    }else if(value == 2){
                        setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                                "fonts/Nunito-Bold.ttf"));
                    } else {
                        setTypeface(tf);
                    }
                }
            } finally {
                ta.recycle();
            }
        }

    }
}
