package gmedia.net.id.kartikaelektrik.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.LoginScreen;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.LocationModel;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.MySQLiteHandler;
import gmedia.net.id.kartikaelektrik.util.SessionManager;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shin on 1/11/2017.
 */

public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int firstTaskTimer = 4 * 1000; // 4 sec
    private int secondTaskTimer = 5 * 60 * 1000; // 5 minutes
    private int serviceTimer = 30 * 1000; // 30 minutes
    private int updateLocationToServerTimer = 60 * 60 * 1000; // 1 hour
    private Boolean firstTaskDone = false;
    private Handler taskHandler, serviceHandler, locationUpdateServerHandler;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String TAG = "TestService";
    private double latitude, longitude;
    private MySQLiteHandler mySQLiteHandler;
    private ItemValidation iv = new ItemValidation();
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SessionManager session;
    private Date expiredDate;
    private HashMap<String, String > user;
    private String lastUpdatedLocationdate;
    private SharedPreferenceHandler sph = new SharedPreferenceHandler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mySQLiteHandler = new MySQLiteHandler(getApplicationContext());

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        String userDate = user.get(session.TAG_EXP);

        try {
            expiredDate = defaultFormat.parse(userDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        taskHandler = new Handler();
        startFirstRepeatingTask();

        serviceHandler = new Handler();
        serviceRunable.run();

        locationUpdateServerHandler = new Handler();
        updateLocationServerRunable.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        killService();
        super.onDestroy();
    }

    private void killService(){
        serviceHandler.removeCallbacks(serviceRunable);
        locationUpdateServerHandler.removeCallbacks(updateLocationServerRunable);
        stopFirstRepeatingTask();
        stopSecondRepeatingTask();
        mGoogleApiClient.disconnect();
    }

    //region First Repeating Task
    void startFirstRepeatingTask() {
        firstTaskRunable.run();
    }

    void stopFirstRepeatingTask() {
        taskHandler.removeCallbacks(firstTaskRunable);
        taskHandler = new Handler();

        startSecondRepeatingTask();
    }

    Runnable firstTaskRunable = new Runnable() {
        @Override
        public void run() {

            try {
                if(firstTaskDone){
                    stopFirstRepeatingTask();
                }else{
                    updateLocation();
                }
            } finally {
                if(!firstTaskDone){
                    taskHandler.postDelayed(firstTaskRunable, firstTaskTimer);
                }else{
                    stopFirstRepeatingTask();
                }
            }
        }
    };

    Runnable secondTaskRunamble = new Runnable() {
        @Override
        public void run() {

            try {
                updateLocation();
            } finally {
                taskHandler.postDelayed(secondTaskRunamble, secondTaskTimer);
            }
        }
    };

    Runnable serviceRunable = new Runnable() {

        @Override
        public void run() {
            try {
                Date currentDateTime = null;
                Date date = new Date();
                String currentDateTimeString = defaultFormat.format(date);

                try {
                    currentDateTime = defaultFormat.parse(currentDateTimeString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(currentDateTime.after(expiredDate)){

                    BackToLogin();
                }

            } finally {
                serviceHandler.postDelayed(serviceRunable, serviceTimer);
            }
        }
    };

    //region Update Location Runable
    Runnable updateLocationServerRunable = new Runnable() {

        @Override
        public void run() {
            try {

                lastUpdatedLocationdate = sph.getLastUpdatedLocationDate(getApplicationContext());
                if(lastUpdatedLocationdate == null
                        || lastUpdatedLocationdate.equals("")
                        || lastUpdatedLocationdate.equals(null)
                        || lastUpdatedLocationdate.equals("null")){
                    lastUpdatedLocationdate = iv.getCurrentDate(dateFormat);
                }

                String currentDateTime = iv.getCurrentDate(dateFormat);

                Date currentDate = null;
                Date lastDate = null;

                try {
                    currentDate = defaultFormat.parse(currentDateTime);
                    lastDate = defaultFormat.parse(lastUpdatedLocationdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastDate);
                calendar.add(Calendar.HOUR, 1);
                Date newDate = calendar.getTime();

                if(newDate.equals(currentDate) || newDate.after(currentDate)){
                    List<LocationModel> locationList = mySQLiteHandler.getLocationByRangeTime(dateFormat, defaultFormat.format(lastDate), defaultFormat.format(currentDate));
                    sph.setLastUpdatedLocationDate(getApplicationContext(),currentDateTime);
                    for(LocationModel location : locationList){
                        UpdateLocationToServer(location);
                    }
                }

            } finally {
                locationUpdateServerHandler.postDelayed(updateLocationServerRunable, updateLocationToServerTimer);
            }
        }
    };
    //endregion

    //endregion

    //region Second Task
    void startSecondRepeatingTask() {
        secondTaskRunamble.run();
    }

    void stopSecondRepeatingTask() {
        taskHandler.removeCallbacks(secondTaskRunamble);
    }
    //endregion

    private void updateLocation(){

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null){

            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            if(!firstTaskDone) { // Login
                firstTaskDone = true;
//                LocationModel locationModel = mySQLiteHandler.getLocationById(1,getResources().getString(R.string.format_date));

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<android.location.Address> addresses = null;
                String keterangan = "";

                try {
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    String cityName = iv.parseNullString(addresses.get(0).getAddressLine(0));
                    String stateName = iv.parseNullString(addresses.get(0).getAddressLine(1));
                    String countryName = iv.parseNullString(addresses.get(0).getAddressLine(2));
                    keterangan = cityName+" "+stateName+" "+countryName;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LocationModel locationModel = new LocationModel(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), iv.getCurrentDate(dateFormat), "Login : "+ keterangan, "L");
                mySQLiteHandler.addLocation(locationModel, dateFormat);

                // update Location
                lastUpdatedLocationdate = iv.getCurrentDate(dateFormat);
                sph.setLastUpdatedLocationDate(getApplicationContext(), lastUpdatedLocationdate);

                UpdateLocationToServer(locationModel);
            }else { // Update

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<android.location.Address> addresses = null;
                String keterangan = "";
                try {
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    String cityName = iv.parseNullString(addresses.get(0).getAddressLine(0));
                    String stateName = iv.parseNullString(addresses.get(0).getAddressLine(1));
                    String countryName = iv.parseNullString(addresses.get(0).getAddressLine(2));
                    keterangan = cityName+" "+stateName+" "+countryName;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LocationModel locationModel = new LocationModel(String.valueOf(mLastLocation.getLatitude()),String.valueOf(mLastLocation.getLongitude()),iv.getCurrentDate(dateFormat),"Update Location :" + keterangan, "U");
                mySQLiteHandler.addLocation(locationModel,dateFormat);
            }

        }
    }

    private void UpdateLocationToServer(LocationModel locationModel){

        if(!session.isLoggedIn()){

            return;
        }
        String urlPostLocation = getApplicationContext().getResources().getString(R.string.url_insert_location);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("tanggal", locationModel.getDate());
            jsonBody.put("longitude", locationModel.getLongitude());
            jsonBody.put("latitude", locationModel.getLatitude());
            jsonBody.put("keterangan", locationModel.getKeterangan());
            jsonBody.put("flag", locationModel.getFlag());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley hargaBarangDetail = new ApiVolley(getApplicationContext(), jsonBody, "POST", urlPostLocation, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void BackToLogin(){

        Intent intent = new Intent(this, LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),
                "Ijin anda kadaluarsa, mohon Login kembali", Toast.LENGTH_LONG)
                .show();
        stopSelf();
    }

    //region Play Service
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                /*GooglePlayServicesUtil.getErrorDialog(resultCode,(Activity) getBaseContext(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
                Toast.makeText(getApplicationContext(),
                        "Please Login to your google play service.", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    //endregion

}
