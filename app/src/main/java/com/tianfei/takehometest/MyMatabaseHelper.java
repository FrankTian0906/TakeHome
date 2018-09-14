package com.tianfei.takehometest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * creat database SQLite
 * DB: takehome.db
 * table: router(airlinrID text, origin text, destination text)
 */
public class MyMatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "takeHome.db";
    //table: routers
    private static final String TABLE_ROUTERS = "routes";
    private static final String ID = "airlineID";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private Context mContext;

    private static final String TABLE_AIRPORTS = "airports";
    private static final String AIRPORTS_NAME = "name";
    private static final String AIRPORTS_CITY = "city";
    private static final String AIRPORTS_COUNTRY = "country";
    private static final String AIRPORTS_IATA = "IATA";
    private static final String AIRPORTS_LATITUDE = "latitude";
    private static final String AIRPORTS_LONGITUDE = "longitude";

    public MyMatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String newTable_routers = "create table " + TABLE_ROUTERS +
                " (airlineID text, " +
                "origin text, " +
                "destination text)";
        db.execSQL(newTable_routers);

        String newTable_airports = "create table " + TABLE_AIRPORTS +
                " (name text," +
                " city text,"+
                " country text,"+
                " IATA text,"+
                " latitude double,"+
                " longitude double)";
        db.execSQL(newTable_airports);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("");
    }

    /*
    * add data to table routers
    * */
    public boolean addData_routers(String id, String origin, String destination){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(ORIGIN,origin);
        contentValues.put(DESTINATION, destination);
        long resut = db.insert(TABLE_ROUTERS,null,contentValues);
        if(resut == -1)
            return false;
        else
            return true;
    }
    /*
     * add data to table airports
     * */
    public boolean addData_airports(String name, String city, String country, String iata, double latitude, double longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AIRPORTS_NAME, name);
        contentValues.put(AIRPORTS_CITY,city);
        contentValues.put(AIRPORTS_COUNTRY, country);
        contentValues.put(AIRPORTS_IATA, iata);
        contentValues.put(AIRPORTS_LATITUDE,latitude);
        contentValues.put(AIRPORTS_LONGITUDE,longitude);
        long resut = db.insert(TABLE_AIRPORTS,null,contentValues);
        if(resut == -1)
            return false;
        else
            return true;
    }

    /*
     * select * from routers where Origin = '@spot'
     * */
    public Cursor getData(String spot){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+ TABLE_ROUTERS + " where " + ORIGIN + "=\'" + spot +"\'";
        return db.rawQuery(query,null);
    }
    /*
    *fixed
    * get data from table routers
    *   situation:  0 transfer station
    *               1 transfer station
    *               2 transfer station
    * select a.*,b.* from routers a,routers b where a.`Destination`=b.`Origin` AND a.`Origin` = '@origin' and b.`Destination` = '@destination'
    * */
    public Cursor getData_trans_0(String origin,String destination){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select distinct * from "+ TABLE_ROUTERS +
                " where " + ORIGIN + "=\'" + origin + "\' and " + DESTINATION + "=\'" + destination +"\'";
        return db.rawQuery(query,null);
    }
    public Cursor getData_trans_1(String origin,String destination){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select distinct a.* ,b.* from "+ TABLE_ROUTERS +" a," + TABLE_ROUTERS +
                " b where a." + DESTINATION + "=b." + ORIGIN +
                " and a." + ORIGIN + "=\'" + origin + "\' and b." + DESTINATION + "=\'" + destination +"\'";
        return db.rawQuery(query,null);
    }
    public Cursor getData_trans_2(String origin,String destination){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select distinct a.* ,b.*,c.* from "+ TABLE_ROUTERS +" a," + TABLE_ROUTERS + "b," + TABLE_ROUTERS +
                " c where a." + DESTINATION + "=b." + ORIGIN + " and b." + DESTINATION + "=c." + ORIGIN +
                " and a." + ORIGIN + "=\'" + origin + "\' and c." + DESTINATION + "=\'" + destination +"\'";
        return db.rawQuery(query,null);
    }

    /*
     * select * from airports where IATA = '@iata'
     * */
    public Cursor getAirport(String iata){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+ TABLE_AIRPORTS + " where " + AIRPORTS_IATA + "=\'" + iata +"\'";
        return db.rawQuery(query,null);
    }
}
