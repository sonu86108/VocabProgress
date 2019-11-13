package com.sonu.vocabprogress.utilities;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class AppUtils {
    public static void toast(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void snackBar(View layoutView,String msg){
        Snackbar.make(layoutView,msg,Snackbar.LENGTH_SHORT).show();
    }

}
