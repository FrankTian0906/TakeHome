package com.tianfei.takehometest.model;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tianfei.takehometest.MainActivity;
import com.tianfei.takehometest.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class LoadDataModel implements Runnable{

    MyMatabaseHelper myMatabaseHelper;
    InputStream inputStream_routers;
    InputStream inputStream_airports;
    LoadData loadData;

    public LoadDataModel(Context context, InputStream inputStream_routers, InputStream inputStream_airports,LoadData loadData) {
        myMatabaseHelper = new MyMatabaseHelper(context);
        this.inputStream_routers = inputStream_routers;
        this.inputStream_airports = inputStream_airports;
        this.loadData = loadData;
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

    @Override
    public void run() {
        //InputStream inputStream_routers = context.getResources().openRawResource(R.raw.routes);
        BufferedReader reader_routers = new BufferedReader(
                new InputStreamReader(inputStream_routers, Charset.forName("UTF-8"))
        );
        //InputStream inputStream_airports = context.getResources().openRawResource(R.raw.airports);
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
                Log.d("LoadDataModel", "Get Line routers: " + line);
                // use comma as separator columns of CSV
                String[] tokens = line.split(",");
                //add the data into db.table
                addData_routers(tokens[0], tokens[1], tokens[2]);
            }
            line = "";
            while ((line = reader_airports.readLine()) != null) {
                Log.d("LoadDataModel", "Get Line airports: " + line);
                // use comma as separator columns of CSV
                String[] tokens = line.split(",");
                double lat = Double.parseDouble(tokens[4]);
                double lon = Double.parseDouble(tokens[5]);
                //add the data into db.table
                addData_airports(tokens[0], tokens[1], tokens[2], tokens[3], lat, lon);
            }
            loadData.loadSusses();
        }catch (IOException e)
        {
            // Logs error with priority level
            Log.wtf("LoadDataModel", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public interface LoadData{
        public void loadSusses();
    }
}
