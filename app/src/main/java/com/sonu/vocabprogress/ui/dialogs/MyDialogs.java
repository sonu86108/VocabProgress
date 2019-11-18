package com.sonu.vocabprogress.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.models.QuizWord;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.activities.words.WordListActivity;
import com.sonu.vocabprogress.utilities.SelectionMode;
import com.sonu.vocabprogress.utilities.datahelpers.CloudDatabaseHelper;
import com.sonu.vocabprogress.utilities.datahelpers.QuizHelper;
import com.sonu.vocabprogress.utilities.datahelpers.QuizWordHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyDialogs implements View.OnClickListener {
    Dialog dialog;
    WordListActivity context;
    SelectionMode selectionMode;
    EditText edtQuizName;
    ProgressBar pbMakeQuiz;
    Button btnSave;
    List<Word> wordList;
    List<Integer> selectedWords;
    QuizHelper quizHelper;
    QuizWordHelper quizWordHelper;
    String date, quizName;
    String quizId;
    CloudDatabaseHelper cloudDatabaseHelper;

    public MyDialogs(Context context, SelectionMode selectionMode, List<Word> wordList, List<Integer> selectedWords) {
        this.context = (WordListActivity) context;
        this.selectionMode = selectionMode;
        this.wordList = wordList;
        dialog = new Dialog(context);
        this.selectedWords = selectedWords;
        cloudDatabaseHelper=CloudDatabaseHelper.getInstance();
        dialog.setContentView(R.layout.dialog_make_quiz);
        dialog.setCancelable(true);
        edtQuizName = dialog.findViewById(R.id.id_quizname);
        btnSave = dialog.findViewById(R.id.id_btn_save);
        pbMakeQuiz=dialog.findViewById(R.id.id_pb_makeQuiz);
        quizHelper = QuizHelper.getInstance(this.context);
        quizWordHelper = QuizWordHelper.getInstance(this.context);
        btnSave.setOnClickListener(this);
        if(Build.VERSION.SDK_INT >=24){
            date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        }


    }

    public void showDialog() {
        dialog.show();
    }

    @Override
    public void onClick(View p1) {
        quizName = edtQuizName.getText().toString().trim();
        switch (p1.getId()){
            case R.id.id_btn_save:
                if(CloudDatabaseHelper.isSignedIn()){
                    uploadDataToFirebase(new Quiz(quizName,date),getSeleectedWords());
                }else{
                    if (quizHelper.insertData(new Quiz(quizName, date))) {
                        quizId = String.valueOf(quizHelper.retrieveQuizId(quizName));
                        saveSelectedWords();
                    }else {
                        context.showInToast("Error creating quiz");
                    }
                }
                break;
        }
    }

    private List<Word> getSeleectedWords(){
        List<Word>  words=new ArrayList<>();
        for(int a: this.selectedWords){
            words.add(wordList.get(a));
        }
        return words;
    }

    private void saveSelectedWords() {
        if (selectedWords.isEmpty()) {
            this.context.showInSnackBar("Selected words not found");
            dialog.dismiss();
        } else {
            for (int n : selectedWords) {
                quizWordHelper.insertData(new QuizWord(quizId, wordList.get(n)));
            }
            dialog.dismiss();
            selectionMode.exitSelectionMode();
            context.showInSnackBar("Quiz created");
        }
    }

    private void uploadDataToFirebase(final Quiz quiz, final List<Word> words){
        pbMakeQuiz.setVisibility(View.VISIBLE);
        btnSave.setFocusable(false);
         cloudDatabaseHelper.getdBQuizRef().addListenerForSingleValueEvent(new ValueEventListener() {
             boolean result=false;
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot data: dataSnapshot.getChildren()){
                     if(data.getValue(Quiz.class).getQuizName().equals(quiz.getQuizName())){
                         result=true;
                     }
                 }
                 if(!result){
                    uploadQuizToFirebase(quiz,words);
                 }else {
                     edtQuizName.setError("Quiz Already exits");
                     pbMakeQuiz.setVisibility(View.GONE);
                     btnSave.setFocusable(true);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void uploadQuizToFirebase(Quiz quiz, final List<Word> words){
        final String quizKey=cloudDatabaseHelper.getdBQuizRef().push().getKey();
        cloudDatabaseHelper.getdBQuizRef().child(quizKey).
                setValue(quiz).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    for(Word word: words){
                        uploadQuizWordsToFirebase(quizKey,word);
                    }
                    pbMakeQuiz.setVisibility(View.GONE);
                    dialog.dismiss();
                    selectionMode.exitSelectionMode();
                    context.showInSnackBar("Quiz created successfully");
                }
            }
        });
    }

    private void uploadQuizWordsToFirebase( String quizKey,Word word) {
        DatabaseReference quizDbRef=cloudDatabaseHelper.getdBQuizWordRef().child(quizKey);
        quizDbRef.child(quizDbRef.push().getKey()).
                setValue(word);
    }


}
