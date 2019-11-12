package com.sonu.vocabprogress.utilities.helpers;


import com.sonu.vocabprogress.models.Quiz;

import java.util.ArrayList;
import java.util.List;

public interface OnGetDataListener {
    void onStart();
    void onSuccess(List<?> list);
    void onFailure(String errorMessage);
}
