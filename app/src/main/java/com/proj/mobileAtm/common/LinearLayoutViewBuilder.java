package com.proj.mobileAtm.common;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.proj.mobileAtm.transaction.model.entity.KeyValueEntity;

import java.util.List;

public class LinearLayoutViewBuilder extends LinearLayout {

    public LinearLayoutViewBuilder(Context context) {
        super(context);
    }

    public LinearLayoutViewBuilder(Context context, String key, String value,String fontStyle, String fontSize,String fontColour,float headerWeight,float valueWeight){
        super(context);
        initLinearLayoutView(context,key,value,fontStyle,fontSize,fontColour,headerWeight,valueWeight);
    }

    private void initLinearLayoutView(Context context, String key, String value,String fontStyle, String fontSize,String fontColour,float headerWeight,float valueWeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);

        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, headerWeight);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, valueWeight);
        TextView tvHeader = new TextViewBuilder(context, key, Constants.NORMAL_FONT_TYPE, Constants.MEDIUM_FONT, "#979797", headerParams);
        TextView tvValue = new TextViewBuilder(context, value, fontStyle, fontSize, fontColour, valueParams);
        this.addView(tvHeader);
        this.addView(tvValue);
    }

    @BindingAdapter("addCurrencyList")
    public static void addCurrencyList(LinearLayout linearLayout, List<KeyValueEntity> keyValueEntities){
        linearLayout.removeAllViews();
        if (keyValueEntities != null && !keyValueEntities.isEmpty()) {
            for (KeyValueEntity keyValuePairEntity : keyValueEntities) {
                int amount = keyValuePairEntity.getKey();
                int value = keyValuePairEntity.getValue();
                String currency = value+ " Notes";
                String fontStyle = Constants.NORMAL_FONT_TYPE;
                String fontSize = Constants.MEDIUM_FONT;
                String fontColour = "#000000";
                LinearLayout llPrimaryDataList = new LinearLayoutViewBuilder(linearLayout.getContext(),currency,String.valueOf(amount),fontStyle,fontSize,fontColour,0.5f,0.5f);
                linearLayout.addView(llPrimaryDataList);
            }
        }
    }

}