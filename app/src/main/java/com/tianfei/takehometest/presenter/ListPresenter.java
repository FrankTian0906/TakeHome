package com.tianfei.takehometest.presenter;

import com.tianfei.takehometest.MainActivity;
import com.tianfei.takehometest.model.ShowListModel;
import com.tianfei.takehometest.view.MainView;

import java.util.List;
import java.util.Map;

public class ListPresenter {
    ShowListModel showListModel;

    public ListPresenter() {
        showListModel = new ShowListModel();
    }

    private MainView mainView;
    public void bind(MainActivity mainActivity){
        mainView = mainActivity;
    }

    public void showData(){
        showListModel.showResults(mainView.getOrigin(), mainView.getDestination(), mainView.getContext(), new ShowListModel.ShowListListener() {
            @Override
            public void getData_list(List<Map<String, String>> data) {
                mainView.showData(data);
            }

            @Override
            public void getData_intent(List<List<String>> airports) {
                mainView.showData_intent(airports);
            }

            @Override
            public void getData_model(int mode) {
                mainView.getSpotMode(mode);
            }
        });
    }
}
