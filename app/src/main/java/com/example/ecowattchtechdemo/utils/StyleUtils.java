package com.example.ecowattchtechdemo.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import androidx.core.content.ContextCompat;
import com.example.ecowattchtechdemo.R;

public class StyleUtils {
    
    public static SpannableString createStyledUsernameText(Context context, String username) {
        String fullText = username + " -- 1ST PLACE";
        SpannableString spannableString = new SpannableString(fullText);
        
        // Make the username part red
        spannableString.setSpan(
            new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_red)),
            0, username.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        // Make "1ST PLACE" part red and bold
        int placeStart = fullText.indexOf("1ST PLACE");
        spannableString.setSpan(
            new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_red)),
            placeStart, fullText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableString.setSpan(
            new StyleSpan(Typeface.BOLD),
            placeStart, fullText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        return spannableString;
    }

    public static SpannableString createStyledUsageText(Context context, String usage) {
        String fullText = usage + "kW";
        SpannableString spannableString = new SpannableString(fullText);
        
        // Make the number white and large
        spannableString.setSpan(
            new ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)),
            0, usage.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        // Make "kW" red and smaller
        spannableString.setSpan(
            new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_red)),
            usage.length(), fullText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableString.setSpan(
            new RelativeSizeSpan(0.6f),
            usage.length(), fullText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        return spannableString;
    }
}
