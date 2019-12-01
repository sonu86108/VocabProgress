package com.sonu.vocabprogress.utilities.datahelpers;

import android.content.Context;

import com.sonu.vocabprogress.utilities.App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetHelper {
    private static AssetHelper assetHelper=null;
    private Context context;

   private AssetHelper(){
     context=App.appContext;
    }
    public static AssetHelper getInstance(){
        if (assetHelper == null) {
            assetHelper=new AssetHelper();
        }
        return assetHelper;
    }
    public String getAssestString(String fileName){
       InputStream isr=null;
       StringBuffer content=new StringBuffer();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open(fileName.trim()));
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            while (bufferedReader.readLine()!=null){
                content.append(bufferedReader.readLine()+"\n");
            }
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
