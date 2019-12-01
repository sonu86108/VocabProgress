package com.sonu.vocabprogress.ui.activities.updateword;

public interface UpdateWordContract {
    interface View{

    }
    interface Presenter{
        void onStart();
        void defaults();
        void onSaveWord();
    }
}
