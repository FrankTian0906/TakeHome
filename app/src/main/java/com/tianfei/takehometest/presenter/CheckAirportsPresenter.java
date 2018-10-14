package com.tianfei.takehometest.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tianfei.takehometest.MainActivity;
import com.tianfei.takehometest.view.MainView;
import com.tianfei.takehometest.model.CheckAirportsModel;

public class CheckAirportsPresenter {
    private CheckAirportsModel checkAirportsModel;

    public CheckAirportsPresenter(){
        checkAirportsModel = new CheckAirportsModel();
    }

    private MainView mainView;
    public void bind(MainActivity mainActivity){
        mainView = mainActivity;
    }

    public void check(){
        String origin = mainView.getOrigin();
        String destination = mainView.getDestination();
        Context context = mainView.getContext();
        Log.e("Presenter->", "Origin:" + origin + ",Destination" + destination);
        if(checkNameCorrect(origin,destination, context)){
            checkAirportsModel.checkExisted(origin, destination, context,
                    new CheckAirportsModel.CheckResults() {
                        @Override
                        public void airportsExist() {
                            mainView.showResults();
                        }

                        @Override
                        public void airportsNotExist() {
                            mainView.showNoResults();
                        }
                    });
        }

    }

    private boolean checkNameCorrect(String origin, String destination, Context context){
        // is empty
        if(origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(context, "Please input your origin and destination!", Toast.LENGTH_LONG).show();
            return false;
        }
        //is 3-digits
        else if(origin.length() != 3 || destination.length() != 3 || !origin.matches("^[A-Z]*") || !destination.matches("^[A-Z]*") || origin.equals(destination)){
            Toast.makeText(context, "Please input correct IATA!", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
    }
}
