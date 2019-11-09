package com.sonu.vocabprogress.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Quiz;
import com.sonu.vocabprogress.utilities.helpers.RecyclerViewTouchEventListener;

import java.util.List;

public class QuizesAdapter extends RecyclerView.Adapter<QuizesAdapter.QuizesViewHolder> {

    RecyclerViewTouchEventListener eventListener;
    List<Quiz> quizList;
    boolean isItPlayMode;

    public QuizesAdapter(RecyclerViewTouchEventListener eventListener, List<Quiz> list, boolean mode) {
        this.eventListener = eventListener;
        this.quizList = list;
        this.isItPlayMode = mode;
    }

    @Override
    public QuizesViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        View view = LayoutInflater.from(p1.getContext()).inflate(R.layout.row_layout_quizes, p1,
                false);
        return new QuizesViewHolder(view, eventListener, isItPlayMode);
    }

    @Override
    public void onBindViewHolder(QuizesViewHolder p1, int p2) {
        p1.tvQuizName.setText(quizList.get(p2).getQuizName());
        p1.tvQuizDate.setText(quizList.get(p2).getDate());
    }

    @Override
    public int getItemCount() {

        return quizList.size();
    }

    public static class QuizesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView  tvQuizName, tvQuizDate;
        ImageView ivPlayQuiz;
        RecyclerViewTouchEventListener eventListener;
        boolean isItPlayMode;

        private QuizesViewHolder(View v, RecyclerViewTouchEventListener eventListener, boolean mode) {
            super(v);
            this.eventListener = eventListener;
            tvQuizName = v.findViewById(R.id.id_tv_quizName);
            tvQuizDate = v.findViewById(R.id.id_tv_quizDate);
            if (mode) {
                isItPlayMode = true;
                ivPlayQuiz = v.findViewById(R.id.id_playQuiz);
                ivPlayQuiz.setVisibility(View.VISIBLE);
                ivPlayQuiz.setOnClickListener(this);
            } else {
                v.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            eventListener.onRecyclerViewItemClick(v, getAdapterPosition());

        }
    }

}
