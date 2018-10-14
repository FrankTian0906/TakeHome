package com.tianfei.takehometest.view;

import android.content.Context;

import java.util.List;
import java.util.Map;

public interface MainView {
    void loadData();

    String getOrigin();

    String getDestination();

    Context getContext();

    void showResults();

    void showNoResults();

    void showData(List<Map<String,String>> data);

    void getSpotMode(int mode);

    void showData_intent(List<List<String>> airports);
}
