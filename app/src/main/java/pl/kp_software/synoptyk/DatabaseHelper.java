package pl.kp_software.synoptyk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by majster on 28.09.17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Synoptyk.db";
    public static final String TABLE_MEASUREMENTS = "measurements";
    public static final String TABLE_FORECASTS = "forecasts";
    public static final String TABLE_STATIONS = "stations";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MEASUREMENTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HOUR INTEGER, TEMPERATURE REAL, WIND_SPEED REAL, WIND_DIRECT REAL, HUMIDITY REAL, " +
                "PREASURE REAL, RAINFALL REAL, DATE TEXT, STATION TEXT) " );
        db.execSQL("create table " + TABLE_FORECASTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HOUR TEXT, DATE TEXT, NEXT TEXT, TIMES_FROM TEXT, TIMES_TO TEXT, " +
                "TEMPERATURES TEXT, WIND_SPEEDS TEXT, WIND_DIRECTS TEXT, PREASURES TEXT, SITUATIONS TEXT," +
                "PRECIPITATIONS TEXT, STATION TEXT) " );
        db.execSQL("create table " + TABLE_STATIONS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, NUMBER INTEGER, LATITUDE REAL, LONGITUDE REAL, STATION_ID INTEGER)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECASTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        onCreate(db);
    }

    public boolean insertDataMeasurements(String hour, String temperature, String wind_speed, String wind_direct, String humidity,
                                          String preasure, String rainfall, String date, String station){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HOUR", hour);
        contentValues.put("TEMPERATURE", temperature);
        contentValues.put("WIND_SPEED", wind_speed);
        contentValues.put("WIND_DIRECT", wind_direct);
        contentValues.put("HUMIDITY", humidity);
        contentValues.put("PREASURE", preasure);
        contentValues.put("RAINFALL", rainfall);
        contentValues.put("DATE", date);
        contentValues.put("STATION", station);
        long result = db.insert(TABLE_MEASUREMENTS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataForecasts(String hour, String date, String next, String times_from, String times_to, String temperatures,
                                       String wind_speeds, String wind_directs, String preasures, String situations, String precipitations,
                                       String station){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HOUR", hour);
        contentValues.put("DATE", date);
        contentValues.put("NEXT", next);
        contentValues.put("TIMES_FROM", times_from);
        contentValues.put("TIMES_TO", times_to);
        contentValues.put("TEMPERATURES", temperatures);
        contentValues.put("WIND_SPEEDS", wind_speeds);
        contentValues.put("WIND_DIRECTS", wind_directs);
        contentValues.put("PREASURES", preasures);
        contentValues.put("SITUATIONS", situations);
        contentValues.put("PRECIPITATIONS", precipitations);
        contentValues.put("STATION", station);
        long result = db.insert(TABLE_FORECASTS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataStations(String name, String number, String latitude, String longitude, String station_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("NUMBER", number);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        contentValues.put("STATION_ID", station_id);
        long result = db.insert(TABLE_STATIONS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllDataMeasurements(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select HOUR, TEMPERATURE, WIND_SPEED, WIND_DIRECT, HUMIDITY, PREASURE," +
                "RAINFALL, DATE, STATION, ID from "+ TABLE_MEASUREMENTS, null);
        return res;
    }

    public Cursor getAllDataForecasts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select HOUR, DATE, NEXT, TIMES_FROM, TIMES_TO, TEMPERATURES, WIND_SPEEDS, WIND_DIRECTS, " +
                "PREASURES, SITUATIONS, PRECIPITATIONS, STATION, ID from "+ TABLE_FORECASTS, null);
        return res;
    }

    public Cursor getAllDataStations(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select NAME, NUMBER, LATITUDE, LONGITUDE, STATION_ID from "+ TABLE_STATIONS, null);
        return res;
    }

    public Cursor getDataMeasurement(String measurement_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select HOUR, TEMPERATURE, WIND_SPEED, WIND_DIRECT, HUMIDITY, PREASURE," +
                "RAINFALL, DATE, STATION from "+ TABLE_MEASUREMENTS + " where ID=" + measurement_id, null);
        return res;
    }

    public Cursor getDataForecast(String forecast_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select HOUR, DATE, NEXT, TIMES_FROM, TIMES_TO, TEMPERATURES, WIND_SPEEDS, WIND_DIRECTS, " +
                "PREASURES, SITUATIONS, PRECIPITATIONS, STATION from "+ TABLE_FORECASTS + " where ID=" + forecast_id, null);
        return res;
    }

    public Integer deleteDataMeasurementsAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MEASUREMENTS, null, null);
    }

    public Integer deleteDataMForecastsAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FORECASTS, null, null);
    }

    public Integer deleteDataStationsAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STATIONS, null, null);
    }
}
