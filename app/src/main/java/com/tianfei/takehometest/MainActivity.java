package com.tianfei.takehometest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.tianfei.takehometest.model.Airports;
import com.tianfei.takehometest.model.MyMatabaseHelper;
import com.tianfei.takehometest.presenter.CheckAirportsPresenter;
import com.tianfei.takehometest.presenter.ListPresenter;
import com.tianfei.takehometest.presenter.LoadDataPresenter;
import com.tianfei.takehometest.view.MainView;
import com.tianfei.takehometest.view.MyAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainView {

    MyMatabaseHelper myMatabaseHelper;
    private EditText editOrigin;
    private EditText editDestination;
    private ListView dataListView;
    private Intent intent;
    //mode 0-> 0 transfer station; 1-> 1 transfer station; 2-> 2 transfer stations
    //should be final static int, ignore it.
    private int spotMode = 0;
    private List<Airports> airports;
    private List<List<String>> spots_str;

    private LoadDataPresenter loadDataPresenter;
    private CheckAirportsPresenter checkAirportsPresenter;
    private ListPresenter listPresenter;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editOrigin = findViewById(R.id.origin_IATA);
        editDestination = findViewById(R.id.destination_IATA);
        dataListView = findViewById(R.id.listView_Results);
        myMatabaseHelper = new MyMatabaseHelper(this);
        intent =new Intent(this, MapsActivity.class);

        loadDataPresenter = new LoadDataPresenter(getResources().openRawResource(R.raw.routes),getResources().openRawResource(R.raw.airports));
        loadDataPresenter.bind(this);
        checkAirportsPresenter = new CheckAirportsPresenter();
        checkAirportsPresenter.bind(this);
        listPresenter = new ListPresenter();
        listPresenter.bind(this);
    }

    /*
    * BUTTON "ADD DATA" event listener
    * */
    public void buttonAddData(View view) throws IOException {
        Toast.makeText(this, "Please waiting for the loading", Toast.LENGTH_LONG).show();
        loadDataPresenter.load();
    }

    /**
     * BUTTON "RESEARCH" event listener
     * include method: showResult
     */
    public void buttonShowResults(View view){
        checkAirportsPresenter.check();
    }

    /**
     * product a Airports object according to the value of IATA
     */
    public Airports getAirportsInfo(String iata){
        Cursor data = myMatabaseHelper.getAirport(iata);
        Airports airports =new Airports();
        if(data.moveToNext()){
            airports.setName(data.getString(0));
            airports.setCity(data.getString(1));
            airports.setCountry(data.getString(2));
            airports.setIATA(data.getString(3));
            airports.setLatitude(data.getDouble(4));
            airports.setLongitude(data.getDouble(5));
        }
        return airports;
    }

    //TODO
    @Override
    public void loadData() {
        Toast.makeText(this, "Loading data successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public String getOrigin() {
        return editOrigin.getText().toString();
    }

    @Override
    public String getDestination() {
        return editDestination.getText().toString();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showResults() {
        listPresenter.showData();
    }

    @Override
    public void showNoResults() {
        Toast.makeText(this, "Sorry, There is no IATA you input!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showData(List<Map<String,String>> data) {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.item_slide);
        myAdapter = new MyAdapter(data);
        dataListView.setAdapter(myAdapter);
        dataListView.setLayoutAnimation(layoutAnimationController);
        dataListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        adapterView.getItemAtPosition(i);
                        //product a Airports object according to the value of IATA
                        List<String> pots = spots_str.get(i);
                        airports = new ArrayList<>();
                        for(int j=0;j< pots.size();j++){
                            airports.add(getAirportsInfo(pots.get(j)));
                        }

                        switch (spotMode){
                            case 0:
                                intent.putExtra("mode",0);
                                intent.putExtra("origin",airports.get(0));
                                intent.putExtra("destination",airports.get(1));
                                Log.d("0 INTENT->",airports.get(0).getIATA()+ airports.get(1).getIATA());
                                break;
                            case 1:
                                intent.putExtra("mode",1);
                                intent.putExtra("origin",airports.get(0));
                                intent.putExtra("transfer_1",airports.get(1));
                                intent.putExtra("destination",airports.get(2));
                                Log.d("1 INTENT->ORI DES",airports.get(0).getIATA()+ airports.get(1).getIATA()+ airports.get(2).getIATA());
                                break;
                            case 2:
                                intent.putExtra("mode",2);
                                intent.putExtra("origin",airports.get(0));
                                intent.putExtra("transfer_1",airports.get(1));
                                intent.putExtra("transfer_2",airports.get(2));
                                intent.putExtra("destination",airports.get(3));
                                Log.d("2 INTENT->ORI DES",airports.get(0).getIATA()+airports.get(1).getIATA()+airports.get(2).getIATA()+airports.get(3).getIATA());
                                break;
                        }

                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void getSpotMode(int mode) {
        spotMode = mode;
    }

    @Override
    public void showData_intent(List<List<String>> airports) {
        spots_str = airports;
    }
}

