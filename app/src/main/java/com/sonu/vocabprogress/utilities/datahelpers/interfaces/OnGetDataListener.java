package com.sonu.vocabprogress.utilities.datahelpers.interfaces;


import java.util.List;

public interface OnGetDataListener {
    void onStart();
    void onSuccess(List<?> list);
    void onFailure(String errorMessage);
}
