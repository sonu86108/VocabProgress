package com.sonu.vocabprogress.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.utilities.datahelpers.interfaces.RecyclerViewTouchEventListener;

import java.util.List;


public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordListViewHolder> {
    List<Word> wordList;
    RecyclerViewTouchEventListener eventListner;

    public WordListAdapter(List<Word> wordlist, RecyclerViewTouchEventListener eventListner) {
        this.wordList = wordlist;
        this.eventListner = eventListner;

    }

    @Override
    public WordListViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        View view = LayoutInflater.from(p1.getContext()).
                inflate(R.layout.row_layout_words, p1, false);
        return new WordListViewHolder(view, eventListner);
    }


    @Override
    public void onBindViewHolder(WordListAdapter.WordListViewHolder p1, int p2) {
        // TODO: Implement this method
        Word word = this.wordList.get(p2);
        p1.name.setText(word.getWordName());
        p1.meaning.setText(word.getWordMeaning());
        p1.desc.setText(word.getWordDesc());
        //p1.cardView.setOnLongClickListener(this.mainActivity);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    //ViewModel class extending RecyclerView.ViewHolder
    public class WordListViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {
        public TextView name, meaning, desc;
        RecyclerViewTouchEventListener eventListener;
        CardView cardView;
        ImageView moreOptionsWord;

        public WordListViewHolder(View view, RecyclerViewTouchEventListener eventListener) {
            super(view);
            this.eventListener = eventListener;
            name = view.findViewById(R.id.id_textview_word);
            meaning = view.findViewById(R.id.id_textview_meaning);
            desc = view.findViewById(R.id.id_textview_desc);
            moreOptionsWord=view.findViewById(R.id.id_more_menu_word);
            cardView = view.findViewById(R.id.id_cardView);
            cardView.setOnLongClickListener(this);
            cardView.setOnClickListener(this);
            moreOptionsWord.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            eventListener.onRecyclerViewItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            eventListener.onRecyclerViewItemLongClick(v,moreOptionsWord, getAdapterPosition());
            return true;
        }


    }


}
