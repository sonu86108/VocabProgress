package com.sonu.vocabprogress.ui.activities.words;

import android.content.Context;
import android.database.Cursor;

import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.DataFetcher;

import java.util.ArrayList;
import java.util.List;

public class ViewModel {
    private SQLiteHelper sqLiteHelper;

    public ViewModel(Context context) {
        this.sqLiteHelper =SQLiteHelper.getSQLiteHelper(context);
    }

    public void fetchWordData(DataFetcher dataFetcher){
        dataFetcher.onStart();
        if(sqLiteHelper.retrieveData()!=null){
            Cursor cursor=sqLiteHelper.retrieveData();
            List<Word> words=new ArrayList<>();
            if(cursor.moveToFirst()){
                do{
                    words.add(new Word(cursor.getString(1),cursor.getString(2),
                            cursor.getString(3)));
                }while (cursor.moveToNext());
            }
            dataFetcher.onSuccess(words);
        }else {
            dataFetcher.onFailure();
        }
    }

}
