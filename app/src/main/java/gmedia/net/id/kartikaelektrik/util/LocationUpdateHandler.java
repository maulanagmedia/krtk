package gmedia.net.id.kartikaelektrik.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.LocationModel;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shin on 1/27/2017.
 */

public class LocationUpdateHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final Handler taskHandler;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private String keterangan;
    private Location mLastLocation;
    private double latitude, longitude;
    private MySQLiteHandler mySQLiteHandler;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private ItemValidation iv = new ItemValidation();
    private int firstTaskTimer = 3 * 1000;
    private boolean locationUpdated = false;
    private SharedPreferenceHandler sph = new SharedPreferenceHandler();

    public LocationUpdateHandler(Context context,String keterangan){

        this.context = context;
        this.keterangan = keterangan;

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        mySQLiteHandler = new MySQLiteHandler(context);

        taskHandler = new Handler();
        taskRunable.run();

        updateLocation();
    }

    Runnable taskRunable = new Runnable() {
        @Override
        public void run() {

            try {
                if(!locationUpdated){
                    updateLocation();
                }
            } finally {

                if(!locationUpdated){
                    taskHandler.postDelayed(taskRunable, firstTaskTimer);
                }else{
                    taskHandler.removeCallbacks(taskRunable);
                    mGoogleApiClient.disconnect();
                }
            }
        }
    };

    private void updateLocation(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null){
            locationUpdated = true;
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            String subKeterangan = "";
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String cityName = iv.parseNullString(addresses.get(0).getAddressLine(0));
                String stateName = iv.parseNullString(addresses.get(0).getAddressLine(1));
                String countryName = iv.parseNullString(addresses.get(0).getAddressLine(2));
                subKeterangan = cityName+" "+stateName+" "+countryName;
            } catch (IOException e) {
                e.printStackTrace();
            }

            LocationModel locationModel = new LocationModel(String.valueOf(mLastLocation.getLatitude()),String.valueOf(mLastLocation.getLongitude()),iv.getCurrentDate(dateFormat),keterangan + ": " + subKeterangan,"U");
            mySQLiteHandler.addLocation(locationModel,dateFormat);

            sph.setLastUpdatedLocationDate(context, iv.getCurrentDate(dateFormat));
            UpdateLocationToServer(locationModel);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                /*GooglePlayServicesUtil.getErrorDialog(resultCode,(Activity) getBaseContext(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
                Toast.makeText(context,
                        "Please Login to your google play service.", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(context,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
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

    private void UpdateLocationToServer(LocationModel locationModel){

        String urlPostLocation = context.getResources().getString(R.string.url_insert_location);
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

        ApiVolley hargaBarangDetail = new ApiVolley(context, jsonBody, "POST", urlPostLocation, "", "", 0, new ApiVolley.VolleyCallback() {
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
}
