package com.tianfei.takehometest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyMatabaseHelper myMatabaseHelper;
    private EditText editOrigin;
    private EditText editDestination;
    private ListView dataListView;
    private Intent intent;
    private int spotMode = 0;
    private List<Airports> airposts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editOrigin = findViewById(R.id.origin_IATA);
        editDestination = findViewById(R.id.destination_IATA);
        dataListView = findViewById(R.id.listView_Results);
        myMatabaseHelper = new MyMatabaseHelper(this);
        intent =new Intent(this, MapsActivity.class);
    }

    /*
     * push data from routers.csv into DB.table: routers
     * !!include METHOD:    addData_routers()
     *                      addData_airports()
     * !!Huge data -> new thread
     * */
    public void readDataToDB () throws IOException {
        new Thread() {
            public void run() {
                InputStream inputStream_routers = getResources().openRawResource(R.raw.routes);
                BufferedReader reader_routers = new BufferedReader(
                        new InputStreamReader(inputStream_routers, Charset.forName("UTF-8"))
                );
                InputStream inputStream_airports = getResources().openRawResource(R.raw.airports);
                BufferedReader reader_airports = new BufferedReader(
                        new InputStreamReader(inputStream_airports, Charset.forName("UTF-8"))
                );
                String line = "";
                try{
                    // Step over headers
                    reader_routers.readLine();
                    reader_airports.readLine();
                    // If buffer is not empty
                    while ((line = reader_routers.readLine()) != null) {
                        Log.d("MyActivity", "Get Line routers: " + line);
                        // use comma as separator columns of CSV
                        String[] tokens = line.split(",");
                        //add the data into db.table
                        addData_routers(tokens[0], tokens[1], tokens[2]);
                    }
                    line = "";
                    while ((line = reader_airports.readLine()) != null) {
                        Log.d("MyActivity", "Get Line airports: " + line);
                        // use comma as separator columns of CSV
                        String[] tokens = line.split(",");
                        double lat = Double.parseDouble(tokens[4]);
                        double lon = Double.parseDouble(tokens[5]);
                        //add the data into db.table
                        addData_airports(tokens[0], tokens[1], tokens[2], tokens[3], lat, lon);
                    }
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "Loading data successfully!", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } catch (IOException e)
                {
                    // Logs error with priority level
                    Log.wtf("MyActivity", "Error reading data file on line" + line, e);
                    // Prints throwable details
                    e.printStackTrace();
                 }
            }
         }.start();
    }
    /*
    * insert data to DB.table: routers
    * parameters: airlineID, origin, destination
    * */
    public void addData_routers(String id,String origin,String destination){
        boolean insertData = myMatabaseHelper.addData_routers(id,origin,destination);
        if(insertData)
            Log.d("INSERT:"," Successfully!");
        else
            Log.d("INSERT", "wrong!");
    }
    /*
     * insert data to DB.table: airports
     * parameters: name, city, country, IATA, latitude,longitude
     * */
    public void addData_airports(String name,String city,String country,String iata, double latitude, double longitude){
        boolean insertData = myMatabaseHelper.addData_airports(name,city,country,iata,latitude,longitude);
        if(insertData)
            Log.d("INSERT:"," Successfully!");
        else
            Log.d("INSERT", "wrong!");
    }
    /*
    * BUTTON "ADD DATA" event listener
    * */
    public void buttonAddData(View view) throws IOException {
        Toast.makeText(this, "Please waiting for the loading", Toast.LENGTH_LONG).show();
        readDataToDB();
    }

    /*
    * Check user input
    * */
    public boolean checkName(){
        String ori = editOrigin.getText().toString();
        String des = editDestination.getText().toString();

        // is empty
        Log.d("CHECK_NAME", ori + " ; " + des);
        if(ori.isEmpty() || des.isEmpty()) {
            Toast.makeText(this, "Please input your origin and destination!", Toast.LENGTH_LONG).show();
            return false;
        }
        //is 3-digits
        if(ori.length() != 3 || des.length() != 3 || !ori.matches("^[A-Z]*") || !des.matches("^[A-Z]*")){
            Toast.makeText(this, "Please input correct IATA!", Toast.LENGTH_LONG).show();
            return false;
        }

        Cursor data_ori = myMatabaseHelper.getAirport(ori);
        Cursor data_des = myMatabaseHelper.getAirport(des);
        ArrayList<String> results_o = new ArrayList<>();
        ArrayList<String> results_d = new ArrayList<>();
        while(data_ori.moveToNext() && data_des.moveToNext()){
            results_o.add(data_ori.getString(3));
            results_d.add(data_des.getString(3));
        }
        // is existed
        if(results_o.isEmpty() || results_d.isEmpty()){
            Toast.makeText(this, "Sorry, There is no IATA you input!", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
    }

    /**
     * BUTTON "RESEARCH" event listener
     * include method: showResult
     */
    public void buttonShowResults(View view){
        if(checkName()) {
            String ori = editOrigin.getText().toString();
            String des = editDestination.getText().toString();
            showResult(ori, des);
        }
    }

    /*
    * show the results on the listview
    * parameters: String ori, String des
    * */
    public void showResult(String ori, String des){
        Cursor data = myMatabaseHelper.getData_trans_0(ori,des);
        if(!data.moveToNext()) {
            data = myMatabaseHelper.getData_trans_1(ori, des);
            spotMode = 1;
            if(!data.moveToNext()) {
                data = myMatabaseHelper.getData_trans_2(ori, des);
                spotMode = 2;
            }
            else
                data.moveToPrevious();
        }
        else
            data.moveToPrevious();

        //init results
        //for show on the list
        ArrayList<String> results = new ArrayList<>();
        //for intent adding extra
        List<String> spots = new ArrayList<>();
        while(data.moveToNext()){
            //set the form of list item
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(!data.isNull(i)) {
                if((i+1)%3 == 1) {
                    sb.append("AirLine: ").append(data.getString(i));
                }
                if((i+1)%3 == 2) {
                    sb.append(" Origin: ").append(data.getString(i));
                    spots.add(data.getString(i));
                }
                if((i+1)%3 == 0 ) {
                    sb.append(" ->  Destination: ").append(data.getString(i)).append("\n");
                    spots.add(data.getString(i));
                }
                i++;
            }
            results.add(sb.toString());
            spots = new ArrayList<String>(new HashSet<String>(spots));
            Log.d("SPOTS",spots.toString());
        }
        //is empty
        if(results.isEmpty()) {
            Toast.makeText(this, "Sorry, there is no any lines between two cities!", Toast.LENGTH_LONG).show();
            return;
        }
        //product a Airports object according to the value of IATA
        airposts = new ArrayList<Airports>();
        for(int i=0;i< spots.size();i++){
            airposts.add(getAirportsInfo(spots.get(i)));
        }
        //set listView
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,results);
        dataListView.setAdapter(listAdapter);
        dataListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        adapterView.getItemAtPosition(i);
                        switch (spotMode){
                            case 0:
                                intent.putExtra("mode",0);
                                intent.putExtra("origin",airposts.get(0));
                                intent.putExtra("destination",airposts.get(1));
                                break;
                            case 1:
                                intent.putExtra("mode",1);
                                intent.putExtra("origin",airposts.get(0));
                                intent.putExtra("transfer_1",airposts.get(1));
                                intent.putExtra("destination",airposts.get(2));
                                break;
                            case 2:
                                intent.putExtra("mode",2);
                                intent.putExtra("origin",airposts.get(0));
                                intent.putExtra("transfer_1",airposts.get(1));
                                intent.putExtra("transfer_2",airposts.get(2));
                                intent.putExtra("destination",airposts.get(3));
                                break;
                        }

                        startActivity(intent);
                    }
                }
        );
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
    };
}

