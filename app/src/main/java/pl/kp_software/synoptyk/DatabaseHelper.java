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
    public static final String TABLE_METAR_STATIONS = "metar_stations";
    public static final String TABLE_METAR_RAPORTS = "metar_raports";
    public static final String TABLE_GIOS_STATIONS = "gios_stations";
    public static final String TABLE_GIOS_MEASURMENTS = "gios_measurments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MEASUREMENTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HOUR INTEGER, TEMPERATURE REAL, WIND_SPEED REAL, WIND_DIRECT REAL, HUMIDITY REAL, " +
                "PREASURE REAL, RAINFALL REAL, DATE TEXT, STATION TEXT, LATITUDE REAL, LONGITUDE REAL) " );
        db.execSQL("create table " + TABLE_FORECASTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HOUR TEXT, DATE TEXT, NEXT TEXT, TIMES_FROM TEXT, TIMES_TO TEXT, " +
                "TEMPERATURES TEXT, WIND_SPEEDS TEXT, WIND_DIRECTS TEXT, PREASURES TEXT, SITUATIONS TEXT," +
                "PRECIPITATIONS TEXT, STATION TEXT, LATITUDE REAL, LONGITUDE REAL) " );
        db.execSQL("create table " + TABLE_STATIONS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, NUMBER INTEGER, LATITUDE REAL, LONGITUDE REAL, STATION_ID INTEGER)" );
        db.execSQL("create table " + TABLE_METAR_STATIONS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, NUMBER INTEGER, LATITUDE REAL, LONGITUDE REAL, STATION_ID INTEGER, ELEVATION INTEGER)" );
        db.execSQL("create table " + TABLE_METAR_RAPORTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "STATION INTEGER, DAY INTEGER, HOUR INTEGER, METAR TEXT, MESSAGE TEXT, CREATED_AT TEXT, SITUATION TEXT, " +
                "VISIBILITY TEXT, CLOUD_COVER TEXT, WIND_DIRECT TEXT, WIND_SPEED TEXT, TEMPERATURE TEXT, PRESSURE TEXT, " +
                "LATITUDE REAL, LONGITUDE REAL)" );
        db.execSQL("create table " + TABLE_GIOS_STATIONS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, NUMBER INTEGER, LATITUDE REAL, LONGITUDE REAL, STATION_ID INTEGER, CITY TEXT)" );
        db.execSQL("create table " + TABLE_GIOS_MEASURMENTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "STATION TEXT, CALC_DATE TEXT, ST_INDEX INTEGER, CO_INDEX INTEGER, PM10_INDEX INTEGER, C6H6_INDEX INTEGER, " +
                "NO2_INDEX INTEGER, PM25_INDEX INTEGER, O3_INDEX INTEGER, SO2_INDEX INTEGER, CO_VALUE REAL, PM10_VALUE REAL, " +
                "C6H6_VALUE REAL, NO2_VALUE REAL, PM25_VALUE REAL, O3_VALUE REAL, SO2_VALUE REAL, CO_DATE TEXT, PM10_DATE TEXT, " +
                "C6H6_DATE TEXT, NO2_DATE TEXT, PM25_DATE TEXT, O3_DATE TEXT, SO2_DATE TEXT, LATITUDE REAL, LONGITUDE REAL)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECASTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METAR_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METAR_RAPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIOS_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIOS_MEASURMENTS);
        onCreate(db);
    }

    public boolean insertDataMeasurements(String hour, String temperature, String wind_speed, String wind_direct, String humidity,
                                          String preasure, String rainfall, String date, String station, String station_latitude, String station_longitude){
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
        contentValues.put("LATITUDE", station_latitude);
        contentValues.put("LONGITUDE", station_longitude);
        long result = db.insert(TABLE_MEASUREMENTS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataForecasts(String hour, String date, String next, String times_from, String times_to, String temperatures,
                                       String wind_speeds, String wind_directs, String preasures, String situations, String precipitations,
                                       String station, String latitude, String longitude){
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
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
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

    public boolean insertDataMetarStations(String name, String number, String latitude, String longitude, String station_id, String elevation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("NUMBER", number);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        contentValues.put("STATION_ID", station_id);
        contentValues.put("ELEVATION", elevation);
        long result = db.insert(TABLE_METAR_STATIONS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataMetarRaports(String station, String day, String hour, String metar, String message, String created_at,
                                          String visibility, String cloud_cover, String wind_direct, String wind_speed, String temperature,
                                          String pressure, String situation, String latitude, String longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATION", station);
        contentValues.put("DAY", day);
        contentValues.put("HOUR", hour);
        contentValues.put("METAR", metar);
        contentValues.put("MESSAGE", message);
        contentValues.put("CREATED_AT", created_at);
        contentValues.put("VISIBILITY", visibility);
        contentValues.put("CLOUD_COVER", cloud_cover);
        contentValues.put("WIND_DIRECT", wind_direct);
        contentValues.put("WIND_SPEED", wind_speed);
        contentValues.put("TEMPERATURE", temperature);
        contentValues.put("PRESSURE", pressure);
        contentValues.put("SITUATION", situation);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        long result = db.insert(TABLE_METAR_RAPORTS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataGiosStations(String name, String number, String latitude, String longitude, String station_id, String city){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("NUMBER", number);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        contentValues.put("STATION_ID", station_id);
        contentValues.put("CITY", city);
        long result = db.insert(TABLE_GIOS_STATIONS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataGiosMeasurments(String station, String calc_date, String st_index, String co_index, String pm10_index,
                                             String c6h6_index, String no2_index, String pm25_index, String o3_index, String so2_index,
                                             String co_value, String pm10_value, String c6h6_value, String no2_value, String pm25_value,
                                             String o3_value, String so2_value, String co_date, String pm10_date, String c6h6_date,
                                             String no2_date, String pm25_date, String o3_date, String so2_date, String latitude, String longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATION", station);
        contentValues.put("CALC_DATE", calc_date);
        contentValues.put("ST_INDEX", st_index);
        contentValues.put("CO_INDEX", co_index);
        contentValues.put("PM10_INDEX", pm10_index);
        contentValues.put("C6H6_INDEX", c6h6_index);
        contentValues.put("NO2_INDEX", no2_index);
        contentValues.put("PM25_INDEX", pm25_index);
        contentValues.put("O3_INDEX", o3_index);
        contentValues.put("SO2_INDEX", so2_index);
        contentValues.put("CO_VALUE", co_value);
        contentValues.put("PM10_VALUE", pm10_value);
        contentValues.put("C6H6_VALUE", c6h6_value);
        contentValues.put("NO2_VALUE", no2_value);
        contentValues.put("PM25_VALUE", pm25_value);
        contentValues.put("O3_VALUE", o3_value);
        contentValues.put("SO2_VALUE", so2_value);
        contentValues.put("CO_DATE", co_date);
        contentValues.put("PM10_DATE", pm10_date);
        contentValues.put("C6H6_DATE", c6h6_date);
        contentValues.put("NO2_DATE", no2_date);
        contentValues.put("PM25_DATE", pm25_date);
        contentValues.put("O3_DATE", o3_date);
        contentValues.put("SO2_DATE", so2_date);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        long result = db.insert(TABLE_GIOS_MEASURMENTS, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllDataMeasurements(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select HOUR, TEMPERATURE, WIND_SPEED, WIND_DIRECT, HUMIDITY, PREASURE," +
                "RAINFALL, DATE, STATION, ID, LATITUDE, LONGITUDE from "+ TABLE_MEASUREMENTS, null);
        return res;
    }

    public Cursor getAllDataForecasts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select HOUR, DATE, NEXT, TIMES_FROM, TIMES_TO, TEMPERATURES, WIND_SPEEDS, WIND_DIRECTS, " +
                "PREASURES, SITUATIONS, PRECIPITATIONS, STATION, ID, LATITUDE, LONGITUDE from "+ TABLE_FORECASTS, null);
        return res;
    }

    public Cursor getAllDataStations(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select NAME, NUMBER, LATITUDE, LONGITUDE, STATION_ID from "+ TABLE_STATIONS, null);
        return res;
    }

    public Cursor getAllDataMetarStations(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select NAME, NUMBER, LATITUDE, LONGITUDE, STATION_ID, ELEVATION from "+ TABLE_METAR_STATIONS, null);
        return res;
    }

    public Cursor getAllDataMetarRaports(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select STATION, DAY, HOUR, METAR, MESSAGE, CREATED_AT, SITUATION, VISIBILITY, CLOUD_COVER, WIND_DIRECT, " +
                "WIND_SPEED, TEMPERATURE, PRESSURE, ID, LATITUDE, LONGITUDE from "+ TABLE_METAR_RAPORTS + " ORDER BY STATION", null);
        return res;
    }

    public Cursor getAllDataGiosMeasurments(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select STATION, CALC_DATE, ST_INDEX, ID, CO_INDEX, PM10_INDEX, C6H6_INDEX, NO2_INDEX, PM25_INDEX, O3_INDEX, " +
                "SO2_INDEX, CO_VALUE, PM10_VALUE, C6H6_VALUE, NO2_VALUE, PM25_VALUE, O3_VALUE, SO2_VALUE, CO_DATE, PM10_DATE, C6H6_DATE, " +
                "NO2_DATE, PM25_DATE, O3_DATE, SO2_DATE, LATITUDE, LONGITUDE from "+ TABLE_GIOS_MEASURMENTS + " ORDER BY STATION", null);
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

    public Cursor getDataMetarRaport(String metar_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select STATION, DAY, HOUR, METAR, MESSAGE, CREATED_AT, SITUATION, VISIBILITY, CLOUD_COVER, WIND_DIRECT, " +
                "WIND_SPEED, TEMPERATURE, PRESSURE from "+ TABLE_METAR_RAPORTS + " where ID=" + metar_id, null);
        return res;
    }

    public Cursor getDataGiosMeasurment(String measur_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select STATION, CALC_DATE, ST_INDEX, CO_INDEX, PM10_INDEX, C6H6_INDEX, NO2_INDEX, PM25_INDEX, O3_INDEX, " +
                "SO2_INDEX, CO_VALUE, PM10_VALUE, C6H6_VALUE, NO2_VALUE, PM25_VALUE, O3_VALUE, SO2_VALUE, CO_DATE, PM10_DATE, C6H6_DATE, " +
                "NO2_DATE, PM25_DATE, O3_DATE, SO2_DATE from "+ TABLE_GIOS_MEASURMENTS + " where ID=" + measur_id, null);
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

    public Integer deleteDataMetarsAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_METAR_RAPORTS, null, null);
    }

    public Integer deleteDataGiossAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GIOS_MEASURMENTS, null, null);
    }
}
