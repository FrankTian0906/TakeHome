package com.tianfei.takehometest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MyMatabaseHelper myMatabaseHelper;
    private EditText editOrigin;
    private EditText editDestination;
    private ListView dataListView;
    private Intent intent;
    //mode 0-> 0 transfer station; 1-> 1 transfer station; 2-> 2 transfer stations
    //should be final static int, ignore it.
    private int spotMode = 0;
    private List<Airports> airports;

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
     * push data from routers.csv into DB.table: routers, airports
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
        if(ori.length() != 3 || des.length() != 3 || !ori.matches("^[A-Z]*") || !des.matches("^[A-Z]*") || ori.equals(des)){
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
        data_ori.close();
        data_des.close();
        // is existed
        if(results_o.isEmpty() || results_d.isEmpty()){
            Toast.makeText(this, "Sorry, There is no IATA you input!", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
    }

    /**
     * remove duplicated element on the string list
     * @param list list
     * @return List list
     */
    public List<String> removeDuplication(List<String> list){
        for(int i = 0; i< list.size()-1;i++){
            for(int j = i+1; j< list.size();j++){
                if(list.get(i).equals(list.get(j)))
                    list.remove(j);
            }
        }
        return list;
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
    * include METHOD: removeDuplication()
    * */
    private List<Map<String,String>> listData = new ArrayList<>();
    public void showResult(String ori, String des){
        Cursor data = myMatabaseHelper.getData_trans_0(ori,des);
        if(!data.moveToNext()) {
            data = myMatabaseHelper.getData_trans_1(ori, des);
            spotMode = 1;
            if(!data.moveToNext()) {
                data = myMatabaseHelper.getData_trans_2(ori, des);
                spotMode = 2;
            }
            else {
                spotMode = 1;
                data.moveToPrevious();
            }
        }
        else {
            spotMode = 0;
            data.moveToPrevious();
        }
        //init results
        //for show on the list
        ArrayList<String> results = new ArrayList<>();
        //for intent adding extra instance
        final List<List<String>> spots_str = new ArrayList<>();
        spots_str.clear();
        while(data.moveToNext()){
            //get IATAs of every list item and put it into List<List<String>> spots_str
            List<String> spots = new ArrayList<>();
            //set the form of list item
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(!data.isNull(i)) {
                if((i+1)%3 == 1) {
                    sb.append("\n").append("AirLine: ").append(data.getString(i));
                }
                if((i+1)%3 == 2) {
                    sb.append(" Origin: ").append(data.getString(i));
                    spots.add(data.getString(i));
                }
                if((i+1)%3 == 0 ) {
                    sb.append(" ->  Destination: ").append(data.getString(i));
                    spots.add(data.getString(i));
                }
                i++;
            }
            String res = sb.toString();
            if(res.startsWith("\n"))
                res = res.replaceFirst("\n", "");
            results.add(res);

            spots = removeDuplication(spots);
            Log.d("SPOTS-after",spots.toString());
            spots_str.add(spots);
        }
        //is empty
        if(results.isEmpty()) {
            Toast.makeText(this, "Sorry, there is no any lines between two cities!", Toast.LENGTH_LONG).show();
            return;
        }
        //set listView
        listData.clear();
        for(String data_result: results){
            Map<String , String> map = new HashMap<String, String>();
            map.put("message",data_result);
            listData.add(map);
        }
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.item_slide);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listData,R.layout.item_list,new String[]{"message"},new  int[]{R.id.message});
        dataListView.setAdapter(simpleAdapter);
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
}

