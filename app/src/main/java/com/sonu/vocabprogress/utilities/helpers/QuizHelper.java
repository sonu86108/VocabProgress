package com.sonu.vocabprogress.utilities.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;

import com.sonu.vocabprogress.models.Quiz;

public class QuizHelper {
    private static QuizHelper instance;
    SQLiteHelper db;

    private QuizHelper(Context context) {
        db = SQLiteHelper.getSQLiteHelper(context);
    }

    public static QuizHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizHelper(context);
        }
        return instance;
    }

    public boolean insertData(Quiz quiz) throws SQLiteConstraintException {
        long result = -1;
        ContentValues values = new ContentValues();
        values.put(db.C2_QUIZ_NAME, quiz.getQuizName());
        values.put(db.C3_QUIZ_DATE, quiz.getDate());
        try {
            result = db.getWritableDatabase().insertOrThrow(db.TN_QUIZ_LIST, null, values);
        } catch (SQLiteConstraintException e) {
            throw e;
        } catch (SQLException e) {

        } finally {
            db.close();
        }
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor retrieveData() {
        Cursor curso = db.getReadableDatabase().rawQuery("SELECT * FROM " +
                db.TN_QUIZ_LIST, null);
        return curso;
    }

    public int retrieveQuizId(String quizName) {
        Cursor cursur =
                db.getReadableDatabase().query(db.TN_QUIZ_LIST, new String[]{db.C1_QUIZ_ID}, db.C2_QUIZ_NAME + "=?", new String[]{quizName}, null, null, null);
        cursur.moveToFirst();
        return cursur.getInt(0);
    }
}
