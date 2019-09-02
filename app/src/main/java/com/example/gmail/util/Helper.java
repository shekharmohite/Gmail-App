package com.example.gmail.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;

import com.example.gmail.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helper {

    @BindingAdapter("set_date")
    public static void setDate(TextView textView, Long timeInMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        textView.setText(format.format(calendar.getTime()));
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            } else {
                Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            return true;
        }
    }
}
