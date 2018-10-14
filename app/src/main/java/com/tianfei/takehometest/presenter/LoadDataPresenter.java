package com.tianfei.takehometest.presenter;

import android.util.Log;

import com.tianfei.takehometest.MainActivity;
import com.tianfei.takehometest.model.LoadDataModel;
import com.tianfei.takehometest.view.MainView;

import java.io.IOException;
import java.io.InputStream;

public class LoadDataPresenter {
    LoadDataModel loadDataModel;
    private MainView mainView;

    public void bind(MainActivity mainActivity){
        mainView = mainActivity;
    }

    public LoadDataPresenter(InputStream routers, InputStream airports) {
        loadDataModel = new LoadDataModel(mainView.getContext(), routers, airports, new LoadDataModel.LoadData() {
            @Override
            public void loadSusses() {
                mainView.loadData();
            }
        });
    }

    public void load(){
        loadDataModel.run();
    }
}
