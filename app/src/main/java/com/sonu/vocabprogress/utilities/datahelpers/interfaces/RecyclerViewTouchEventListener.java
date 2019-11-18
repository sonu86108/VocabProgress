package com.sonu.vocabprogress.utilities.datahelpers.interfaces;

import android.view.View;
import android.widget.ImageView;

public interface RecyclerViewTouchEventListener {

    void onRecyclerViewItemClick(View v, int p);

    void onRecyclerViewItemLongClick(View v, ImageView menu, int p);
}
