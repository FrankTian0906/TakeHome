package com.tianfei.takehometest;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyMatabaseHelper myMatabaseHelper;
    private EditText editOrigin;
    private EditText editDestination;
    private Button btnAddData;
    private Button btnResults;
    private ListView dataListView;
    private List<Routes> routes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editOrigin = (EditText)findViewById(R.id.origin_IATA);
        editDestination = (EditText)findViewById(R.id.destination_IATA);
        btnAddData = (Button)findViewById(R.id.btn_addData);
        btnResults = (Button)findViewById(R.id.btn_Result);
        dataListView = (ListView)findViewById(R.id.listView_Results);
        myMatabaseHelper = new MyMatabaseHelper(this);
    }

    /*
    * push data from routers.csv into DB.table: routers
    * !!include METHOD: addData()
    * */
    public void readRoutesToDB() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.routes);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );

        // Initialization
        String line = "";
        try {
            // Step over headers
                reader.readLine();
            // If buffer is not empty
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity","Get Line: " + line);
                // use comma as separator columns of CSV
                String[] tokens = line.split(",");
                //add the data into db.table
                addData(tokens[0],tokens[1],tokens[2]);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            // Prints throwable details
            e.printStackTrace();
        }finally {
            inputStream.close();
        }

    }
    /*
     * push data from airports.csv into DB.table: airports
     * !!include METHOD: addData_air()
     * */
    public void readRoutesToDB_air() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.airports);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );

        // Initialization
        String line = "";
        try {
            // Step over headers
            reader.readLine();
            // If buffer is not empty
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity","Get Line: " + line);
                // use comma as separator columns of CSV
                String[] tokens = line.split(",");
                //add the data into db.table
                addData_air(tokens[3]);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            // Prints throwable details
            e.printStackTrace();
        }finally {
            inputStream.close();
        }

    }

    /*
    * insert data to DB.table: routers
    * parameters: airlineID, origin, destination
    * */
    public void addData(String id,String origin,String destination){
        boolean insertData = myMatabaseHelper.addData(id,origin,destination);
        if(insertData)
            Log.d("INSERT:"," Successfully!");
        else
            Log.d("INSERT", "wrong!");
    }
    /*
     * insert data to DB.table: airports
     * parameters: airport
     * */
    public void addData_air(String iata){
        boolean insertData = myMatabaseHelper.addAirport(iata);
        if(insertData)
            Log.d("INSERT:"," Successfully!");
        else
            Log.d("INSERT", "wrong!");
    }

    /*
    * BUTTON "ADD DATA" event listener
    * */
    public void buttonAddData(View view) throws IOException {
        readRoutesToDB();
        readRoutesToDB_air();
    }

    /*
    * Check user input
    * */
    public boolean checkName(){
        String ori = editOrigin.getText().toString();
        String des = editDestination.getText().toString();
        Cursor data_ori = myMatabaseHelper.getData_air(ori);
        Cursor data_des = myMatabaseHelper.getData_air(des);
        ArrayList<String> results_o = new ArrayList<>();
        ArrayList<String> results_d = new ArrayList<>();
        while(data_ori.moveToNext() && data_des.moveToNext()){
            results_o.add(data_ori.getString(0));
            results_d.add(data_des.getString(0));
        }
        // is empty
        if(editOrigin.getText().length() == 0 || editDestination.getText().length() == 0) {
            Log.d("CHECK_NULL", ori + " ; " + des);
            Toast.makeText(this, "Please input your origin and destination!", Toast.LENGTH_LONG).show();
            return false;
        }
        //is 3 digits
        else if(ori.length() != 3 || des.length() != 3){
            Toast.makeText(this, "Please input correct IATA!", Toast.LENGTH_LONG).show();
            return false;
        }
        // is existed
        else if(results_o.isEmpty() || results_d.isEmpty()){
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

    public void showResult(String ori, String des){
        Cursor data = myMatabaseHelper.getData(ori,des);
        ArrayList<String> results = new ArrayList<>();
        while(data.moveToNext()){
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(!data.isNull(i)) {
                if((i+1)%3 == 1) {
                    sb.append("AirLine: ").append(data.getString(i));
                }
                if((i+1)%3 == 2) {
                    sb.append(" Origin: ").append(data.getString(i));
                }
                if((i+1)%3 == 0 )
                    sb.append(" -> Destination: ").append(data.getString(i)).append("\n");
                i++;
            }
            results.add(sb.toString());
        }
        if(results.isEmpty())
            results.add("Sorry, there is no any line between two cities!");

        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,results);
        dataListView.setAdapter(listAdapter);
    }

}

