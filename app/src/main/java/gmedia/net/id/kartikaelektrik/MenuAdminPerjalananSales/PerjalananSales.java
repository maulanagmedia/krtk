package gmedia.net.id.kartikaelektrik.MenuAdminPerjalananSales;

import android.app.DatePickerDialog;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class PerjalananSales extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context;
    private TextView tvNama;
    private String nik = "", nama = "";
    private String formatDate = "", formatDateDisplay = "", formatTimestamp = "", formatTime = "";
    private String curdate = "";
    private ItemValidation iv = new ItemValidation();
    private EditText edtTanggal;
    private double latitude = 0, longitude = 0;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perjalanan_sales);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;
        initUI();
        initEvent();
    }

    private void initEvent() {

        edtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat(formatDateDisplay);
                Date dateValue = null;

                try {
                    dateValue = sdf.parse(curdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                final Calendar customDate;
                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(formatDateDisplay, Locale.US);
                        curdate = sdFormat.format(customDate.getTime());
                        edtTanggal.setText(curdate);
                        getLocationSales();
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);
        formatTimestamp = context.getResources().getString(R.string.format_date1);
        formatTime = context.getResources().getString(R.string.format_time);

        curdate = iv.getCurrentDate(formatDateDisplay);

        ivBack = (ImageView) findViewById(R.id.iv_back);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            nik = bundle.getString("nik", "");
            nama = bundle.getString("nama", "");
        }

        tvNama.setText(nama);
        edtTanggal.setText(curdate);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationSales();
    }

    private void getLocationSales() {

        if(mMap == null){

            Toast.makeText(context, "Peta belum siap, silahkan tunggu kemudian muat data kembali", Toast.LENGTH_LONG).show();
            return;
        }

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nik", nik);
            jsonBody.put("tgl", iv.ChangeFormatDateString(curdate, formatDateDisplay, formatDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(this, jsonBody, "POST", ServerURL.getSalesLocation, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        mMap.clear();

                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(status.equals("200")){

                                JSONArray ja = responseAPI.getJSONArray("response");
                                LatLng latLng = new LatLng(0,0);
                                for(int i = 0; i < ja.length();i++ ){

                                    JSONObject jo = ja.getJSONObject(i);
                                    latLng = new LatLng(iv.parseNullDouble(jo.getString("latitude")), iv.parseNullDouble(jo.getString("longitude")));
                                    mMap.addMarker(new MarkerOptions()
                                            .anchor(0.0f, 1.0f)
                                            .title(iv.ChangeFormatDateString(jo.getString("tgl"), formatTimestamp, formatTime))
                                            .position(new LatLng(iv.parseNullDouble(jo.getString("latitude")), iv.parseNullDouble(jo.getString("longitude")))))
                                            .showInfoWindow();
                                }

                                if(ja.length() > 0) {

                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, "Terjadi kesalahan saat memuat data, harap ulangi proses", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(String result) {

                        Toast.makeText(context, "Terjadi kesalahan saat memuat data, harap ulangi proses", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
