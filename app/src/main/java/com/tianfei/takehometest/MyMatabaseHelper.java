package com.tianfei.takehometest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MyMatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "takeHome.db";
    private static final String TABLE_NAME = "routes";
    private static final String TABLE_NAME_AIRPORT = "airports";
    private Context mContext;

    private static final String ID = "airlineID";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String AIRPORTS = "airportsIATA";

    public MyMatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String newTable = "create table " + TABLE_NAME +
                " (airlineID text, " +
                "origin text, " +
                "destination text)";
        String newTable_airports = "create table " + TABLE_NAME_AIRPORT +
                " (airportsIATA text)";
        db.execSQL(newTable);
        db.execSQL(newTable_airports);
        //Toast.makeText(mContext,"create table: routes!",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("");
    }

    public boolean addData(String id, String origin, String destination){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(ORIGIN,origin);
        contentValues.put(DESTINATION, destination);

        long resut = db.insert(TABLE_NAME,null,contentValues);
        if(resut == -1)
            return false;
        else
            return true;
    }

    public boolean addAirport(String iata){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(iata != "\\N")
            contentValues.put(AIRPORTS, iata);

        long resut = db.insert(TABLE_NAME_AIRPORT,null,contentValues);
        if(resut == -1)
            return false;
        else
            return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+ TABLE_NAME;
        return db.rawQuery(query,null);
    }
    /*
    * select a.*,b.* from routers a,routers b where a.`Destination`=b.`Origin` AND a.`Origin` = '起始' and b.`Destination` = '终点'
     * */
    public Cursor getData(String origin,String destination){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select distinct a.* ,b.* from "+ TABLE_NAME +" a," + TABLE_NAME +
                " b where a." + DESTINATION + "=b." + ORIGIN +
                " and a." + ORIGIN + "=\'" + origin + "\' and b." + DESTINATION + "=\'" + destination +"\'";
        return db.rawQuery(query,null);

    }

    public Cursor getData_air(String iata){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+ TABLE_NAME_AIRPORT + " where " + DESTINATION + "=\'" + iata +"\'";
        Log.d("CHECKING_DB", query);
        return db.rawQuery(query,null);
    }

}
