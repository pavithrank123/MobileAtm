package com.proj.mobileAtm.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class TextViewBuilder extends AppCompatTextView {
    public TextViewBuilder(Context context) {
        super(context);
    }

    public TextViewBuilder(Context context, String header,String fontStyle,String fontSizeValue,String fontColor, ViewGroup.LayoutParams layoutParams) {
        super(context);
        initTextView(header,fontStyle,fontSizeValue,fontColor, layoutParams);
    }

    private void initTextView(String headerName,String fontStyle,String fontSizeValue,String fontColor, ViewGroup.LayoutParams layoutParams) {

        int fontSize = getFontSize(fontSizeValue);

        int typeface = getTypeFace(fontStyle);
        int textAlignmentValue = Gravity.CENTER;

        if (layoutParams == null) {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        this.setText(headerName);
        this.setTypeface(Typeface.DEFAULT, typeface);
        this.setGravity(textAlignmentValue);
        this.setTextSize(fontSize);

        try {
            if (fontColor != null && !fontColor.isEmpty()) {
                this.setTextColor(Color.parseColor(fontColor));
            }
        } catch (Exception e) {

        }

        this.setLayoutParams(layoutParams);
    }

    private int getFontSize(String fontSizeValue) {

        if (fontSizeValue != null && !fontSizeValue.isEmpty()) {

            switch (fontSizeValue) {

                case Constants.SMALL_FONT:
                    return Constants.SMALL_FONT_SIZE;

                case Constants.MEDIUM_FONT:
                    return Constants.MEDIUM_FONT_SIZE;

                case Constants.LARGE_FONT:
                    return Constants.LARGE_FONT_SIZE;

                default:
                    return Constants.MEDIUM_FONT_SIZE;
            }
        }


        return Constants.MEDIUM_FONT_SIZE;
    }

    private int getTypeFace(String fontType) {

        if (fontType != null && !fontType.isEmpty()) {

            switch (fontType) {

                case Constants.BOLD_FONT_TYPE:
                    return Typeface.BOLD;

                case Constants.ITALIC_FONT_TYPE:
                    return Typeface.ITALIC;

                case Constants.NORMAL_FONT_TYPE:
                    return Typeface.NORMAL;

                default:
                    return Typeface.NORMAL;
            }

        }

        return Typeface.NORMAL;
    }

}
