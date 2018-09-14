package com.tianfei.takehometest;

import android.content.Intent;
import android.database.Cursor;
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

public class MainActivity extends AppCompatActivity {

    MyMatabaseHelper myMatabaseHelper;
    private EditText editOrigin;
    private EditText editDestination;
    private ListView dataListView;
    private Intent intent;

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
                    Toast.makeText(MainActivity.this, "Loading data successfully!", Toast.LENGTH_LONG).show();
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

        Cursor data_ori = myMatabaseHelper.getData(ori);
        Cursor data_des = myMatabaseHelper.getData(des);
        ArrayList<String> results_o = new ArrayList<>();
        ArrayList<String> results_d = new ArrayList<>();
        while(data_ori.moveToNext() && data_des.moveToNext()){
            results_o.add(data_ori.getString(0));
            results_d.add(data_des.getString(0));
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
            if(!data.moveToNext())
                data = myMatabaseHelper.getData_trans_2(ori,des);
            else
                data.moveToPrevious();
        }
        else
            data.moveToPrevious();

        //init results
        ArrayList<String> results = new ArrayList<>();
        while(data.moveToNext()){
            //set the form of list item
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(!data.isNull(i)) {
                if((i+1)%3 == 1) {
                    sb.append("AirLine: ").append(data.getString(i));
                }
                if((i+1)%3 == 2) {
                    sb.append("\tOrigin: ").append(data.getString(i));
                }
                if((i+1)%3 == 0 )
                    sb.append("\t->\tDestination: ").append(data.getString(i)).append("\n");
                i++;
            }
            results.add(sb.toString());
        }
        //is empty
        if(results.isEmpty())
            results.add("Sorry, there is no any line between two cities!");

        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,results);
        dataListView.setAdapter(listAdapter);
        dataListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    // 解析string， DB查找生成对象，传递对象
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        adapterView.getItemAtPosition(i);
                        intent.putExtra("","");
                        startActivity(intent);
                    }
                }
        );
    }
}

