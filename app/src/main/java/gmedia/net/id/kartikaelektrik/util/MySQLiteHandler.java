package gmedia.net.id.kartikaelektrik.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gmedia.net.id.kartikaelektrik.model.LocationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shin on 1/12/2017.
 */

public class MySQLiteHandler extends SQLiteOpenHelper {

    private static final int dbVersion = 1;
    private static final String dbName = "kartikastore";

    // location Table
    private final String tLocationName = "location";
    private final String keyLocID = "id";
    private final String keyLatitude = "latitude";
    private final String keyLongitude = "longitude";
    private final String keyDate = "date";
    private final String keyKeterangan = "keterangan";
    private final String keyFlag = "flag";
    private SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String[] cAllFieldLocation = {keyLocID, keyLatitude, keyLongitude, keyDate, keyKeterangan, keyFlag};


    public MySQLiteHandler(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Table location
        String CREATE_DB = "create table "+tLocationName+" (" + keyLocID + " integer primary key autoincrement, " + keyLatitude + " char(50) " +
                ", " + keyLongitude + " char(50), " + keyDate + " date, " + keyKeterangan + " char(100), " + keyFlag + " char(5))";

        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        // Table location
        String DROP_DB = "drop table if exist " + tLocationName;
        db.execSQL(DROP_DB);
    }

    //region Location Table
    public void addLocation(LocationModel locationModel, String formatDate){

        SimpleDateFormat customFormat = new SimpleDateFormat(formatDate);

        Date date = null;

        try {
            date = customFormat.parse(locationModel.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put(keyLatitude, locationModel.getLatitude());
        value.put(keyLongitude, locationModel.getLongitude());
        value.put(keyDate, defaultFormat.format(date));
        value.put(keyKeterangan, locationModel.getKeterangan());
        value.put(keyFlag, locationModel.getFlag());

        db.insert(tLocationName,null,value);
        db.close();
    }

    public LocationModel getLocationById(int id, String formatDate){

        SimpleDateFormat customFormat = new SimpleDateFormat(formatDate);

        Date date = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tLocationName, // tablename
                cAllFieldLocation, // select field
                keyLocID + " = ?", // where field
                new String[]{String.valueOf(id)}, // where value
                null, // group by
                null, // having
                null, // order by
                null); // limit

        if (cursor != null) cursor.moveToFirst();
        LocationModel locationModel = new LocationModel();
        locationModel.setId(Integer.parseInt(cursor.getString(0).toString()));
        locationModel.setLatitude(cursor.getString(1).toString());
        locationModel.setLongitude(cursor.getString(2).toString());
        locationModel.setKeterangan(cursor.getString(4).toString());
        locationModel.setFlag(cursor.getString(5).toString());
        try {
            date = defaultFormat.parse(cursor.getString(3).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        locationModel.setDate(customFormat.format(date));

        return locationModel;
    }

    public List<LocationModel> getAllLocation(String formateDate){

        SimpleDateFormat customFormat = new SimpleDateFormat(formateDate);
        Date date;

        List<LocationModel> locationModelList = new ArrayList<>();

        String qwery = "SELECT * FROM "+ tLocationName;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(qwery,null);

        LocationModel locationModel;

        if(cursor.moveToFirst()){

            do{
                date = new Date();
                locationModel = new LocationModel();
                locationModel.setId(Integer.parseInt(cursor.getString(0).toString()));
                locationModel.setLatitude(cursor.getString(1).toString());
                locationModel.setLongitude(cursor.getString(2).toString());
                locationModel.setKeterangan(cursor.getString(4).toString());
                locationModel.setFlag(cursor.getString(5).toString());
                try {
                    date = defaultFormat.parse(cursor.getString(3).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                locationModel.setDate(customFormat.format(date));
                locationModelList.add(locationModel);
            }while (cursor.moveToNext());
        }

        return locationModelList;
    }

    public List<LocationModel> getLocationByRangeTime(String formateDate, String date1, String date2){

        SimpleDateFormat customFormat = new SimpleDateFormat(formateDate);
        Date date;

        List<LocationModel> locationModelList = new ArrayList<>();

        String qwery = "SELECT * FROM "+ tLocationName + " WHERE " + keyDate + " BETWEEN '" + date1 + "' AND '" + date2 + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(qwery,null);

        LocationModel locationModel;

        if(cursor.moveToFirst()){

            do{
                date = new Date();
                locationModel = new LocationModel();
                locationModel.setId(Integer.parseInt(cursor.getString(0).toString()));
                locationModel.setLatitude(cursor.getString(1).toString());
                locationModel.setLongitude(cursor.getString(2).toString());
                locationModel.setKeterangan(cursor.getString(4).toString());
                locationModel.setFlag(cursor.getString(5).toString());
                try {
                    date = defaultFormat.parse(cursor.getString(3).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                locationModel.setDate(customFormat.format(date));
                locationModelList.add(locationModel);
            }while (cursor.moveToNext());
        }

        return locationModelList;
    }
    //endregion
}
