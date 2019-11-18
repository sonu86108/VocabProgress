package com.sonu.vocabprogress.utilities.datahelpers.interfaces;

import java.util.List;

public interface DataFetcher {
    void onStart();
    void onSuccess(List<?> list);
    void onFailure();
}
