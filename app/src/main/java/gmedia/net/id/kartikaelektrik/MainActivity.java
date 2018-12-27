package gmedia.net.id.kartikaelektrik;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.kartikaelektrik.CustomView.WrapContentViewPager;
import gmedia.net.id.kartikaelektrik.activityPiutang.DetailPiutangJatuhTempo;
import gmedia.net.id.kartikaelektrik.adapter.DashboardAdapter;
import gmedia.net.id.kartikaelektrik.adapter.HeaderSliderAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.PhotoModel;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;
import gmedia.net.id.kartikaelektrik.util.LocationUpdater;
import gmedia.net.id.kartikaelektrik.util.MasterDataHandler;
import gmedia.net.id.kartikaelektrik.util.RuntimePermissionsActivity;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends RuntimePermissionsActivity {

    private final String TAG = "MainAct";
    private List<CustomListItem> sliderList = new ArrayList<>();
    private Animation menuAnimation;
    private ImageButton ibtTambahPelanggan, ibtTambahSO, ibtDaftarSO, ibtTagihanPiutang, ibtInfoStok, ibtKomisi, ibtDenda, ibtBonus, ibtUpdateMaster, ibtMenuAdmin, ibtCustomerLimit, ibtHapusDenda;
    private LinearLayout llLogo, llTambahPelanggan, llPermintaanHarga, llTambahSO, llDaftarSO, llTagihanPiutang, llInfoStok, llKomisi, llDenda, llBonus, llUpdateMaster, llMenuAdmin, llCustomerLimit, llHapusDenda;
    private Intent intent;
    private boolean doubleBackToExitPressedOnce = false;
    private String urlGetSO = "", urlGetLatestVersion = "";
    private SessionManager sessionManager;
    private ItemValidation iv = new ItemValidation();
    private Button btnJumlahSOPermintaanHarga;
    private HashMap<String, String> user;
    private Integer levelUser;
    private static final int REQUEST_PERMISSIONS = 20;
    private LinearLayout llTambahCanvas, llEntryCanvas, llRetur, llOmsetPenjualan;
    private ImageButton ibtTambahCanvas, ibtEntryCanvas, ibtRetur, ibtOmsetPenjualan;
    private LinearLayout llEntryPaket;
    private ImageButton ibtEntryPaket;
    private LinearLayout llLine1, llLine2, llLine3, llLine4, llLine5, llLine6;
    private String version = "";
    private String latestVersion = "";
    private String link = "";
    private boolean updateRequired = false;
    private LinearLayout llCustomOrder, llSetoran, llBarangTidakLaku;
    private ImageButton ibtCustomOrder, ibtSetoran, ibtBarangTidakLaku;
    private String updateStatus = "";

    // untuk dialog status
    private AlertDialog.Builder dialog;
    private AlertDialog dialogViews;
    private View dialogView;
    private Switch swStatus;
    private EditText edUsername, edPassword;
    private boolean dialogActive = false;
    private LinearLayout llDataPiutang, llPiutangTelat, llDataRetur, llDendaPelanggan;
    private TextView tvDataPiutang, tvPiutangTelat, tvDataRetur, tvDendaPelanggan;
    private AlertDialog dialogVersion;
    private WrapContentViewPager vpHeaderSlider;
    private Context context;

    //header slider
    private boolean firstLoad = true;
    private int changeHeaderTimes = 5;
    private Timer timer;
    private HeaderSliderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        // Check Login
        sessionManager = new SessionManager(MainActivity.this);
        user = sessionManager.getUserDetails();

        if(!sessionManager.isLoggedIn()) {
            sessionManager.logoutUser();
            finish();
        }

        // for android > M
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {

            MainActivity.super.requestAppPermissions(new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WAKE_LOCK,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, gmedia.net.id.kartikaelektrik.R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        /*MasterDataHandler mdh = new MasterDataHandler(MainActivity.this);
        mdh.checkWeeklyUpdate();*/
        context = this;
        dialogActive = false;
        firstLoad = true;

        initUI();
    }

    private void initUI() {

        urlGetSO = getResources().getString(gmedia.net.id.kartikaelektrik.R.string.url_get_so);
        llLogo = (LinearLayout) findViewById(R.id.v_logo);
        llTambahPelanggan = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_tambah_pelanggan);
        llPermintaanHarga = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_permintaan_harga_order);
        llTambahSO = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_tambah_so);
        llEntryPaket = (LinearLayout) findViewById(R.id.v_menu_entry_order_paket);
        llDaftarSO = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_daftar_so);
        llTagihanPiutang = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_tagihan_piutang);
        llInfoStok = (LinearLayout) findViewById(R.id.v_menu_info_stok);
        llEntryCanvas = (LinearLayout) findViewById(R.id.v_menu_entry_canvas);
        llTambahCanvas = (LinearLayout) findViewById(R.id.v_menu_tambah_canvas);
        llKomisi = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_komisi);
        llDenda = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_denda);
        llBonus = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_bonus);
        llRetur = (LinearLayout) findViewById(R.id.v_menu_retur);
        llCustomOrder = (LinearLayout) findViewById(R.id.v_menu_custom_order);
        llBarangTidakLaku = (LinearLayout) findViewById(R.id.v_menu_barang_tidak_laku);
        llSetoran = (LinearLayout) findViewById(R.id.v_menu_setoran);
        llOmsetPenjualan = (LinearLayout) findViewById(R.id.v_menu_omset_penjualan);
        llUpdateMaster = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_update_master);
        llMenuAdmin = (LinearLayout) findViewById(R.id.v_menu_admin);
        llCustomerLimit = (LinearLayout) findViewById(R.id.v_menu_customer_limit);
        llHapusDenda = (LinearLayout) findViewById(R.id.v_menu_hapus_denda);

        levelUser = iv.parseNullInteger(user.get(sessionManager.TAG_LEVEL));

        ibtTambahPelanggan = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tambah_pelanggan);
        ibtTambahSO = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tambah_so);
        ibtEntryPaket = (ImageButton) findViewById(R.id.ibt_menu_entry_order_paket);
        ibtDaftarSO = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_daftar_so);
        ibtTagihanPiutang = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tagihan_piutang);
        ibtInfoStok = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_info_stok);
        ibtTambahCanvas = (ImageButton) findViewById(R.id.ibt_menu_tambah_canvas);
        ibtEntryCanvas = (ImageButton) findViewById(R.id.ibt_menu_entry_canvas);
        ibtKomisi = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_komisi);
        ibtDenda = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_denda);
        ibtBonus = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_bonus);
        ibtRetur = (ImageButton) findViewById(R.id.ibt_menu_retur);
        ibtCustomOrder = (ImageButton) findViewById(R.id.ibt_menu_custom_order);
        ibtBarangTidakLaku = (ImageButton) findViewById(R.id.ibt_menu_barang_tidak_laku);
        ibtSetoran = (ImageButton) findViewById(R.id.ibt_menu_setoran);
        ibtOmsetPenjualan = (ImageButton) findViewById(R.id.ibt_menu_omset_penjualan);
        ibtUpdateMaster = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_update_master);
        ibtMenuAdmin = (ImageButton) findViewById(R.id.ibt_menu_admin);
        ibtCustomerLimit = (ImageButton) findViewById(R.id.ibt_menu_customer_limit);
        ibtHapusDenda = (ImageButton) findViewById(R.id.ibt_menu_hapus_denda);
        btnJumlahSOPermintaanHarga = (Button) findViewById(gmedia.net.id.kartikaelektrik.R.id.btn_status_permohonan);

        llLine1 = (LinearLayout) findViewById(R.id.ll_line_1);
        llLine2 = (LinearLayout) findViewById(R.id.ll_line_2);
        llLine3 = (LinearLayout) findViewById(R.id.ll_line_3);
        llLine4 = (LinearLayout) findViewById(R.id.ll_line_4);
        llLine5 = (LinearLayout) findViewById(R.id.ll_line_5);
        llLine6 = (LinearLayout) findViewById(R.id.ll_line_6);

        llDataPiutang = (LinearLayout) findViewById(R.id.ll_data_piutang);
        llPiutangTelat = (LinearLayout) findViewById(R.id.ll_piutang_telat);
        llDataRetur = (LinearLayout) findViewById(R.id.ll_data_retur);
        llDendaPelanggan = (LinearLayout) findViewById(R.id.ll_denda_pelanggan);

        tvDataPiutang = (TextView) findViewById(R.id.tv_data_piutang);
        tvPiutangTelat = (TextView) findViewById(R.id.tv_piutang_telat);
        tvDataRetur = (TextView) findViewById(R.id.tv_data_retur);
        tvDendaPelanggan = (TextView) findViewById(R.id.tv_denda_pelanggan);

        vpHeaderSlider = (WrapContentViewPager) findViewById(R.id.pager_introduction);
        vpHeaderSlider.setScrollDurationFactor(4);

        getListHeaderSlider();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            // this is why the minimal sdk must be JB
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size);
            }else {
                display.getSize(size);
            }
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        int menuWidth = 0;
        menuWidth = size.x;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth - iv.dpToPx(MainActivity.this, 8) , menuWidth/3);
        lp.setMargins(iv.dpToPx(MainActivity.this, 0),iv.dpToPx(MainActivity.this, 0),iv.dpToPx(MainActivity.this, 0),iv.dpToPx(MainActivity.this, 4));
        llLine1.setLayoutParams(lp);
        llLine2.setLayoutParams(lp);
        llLine3.setLayoutParams(lp);
        llLine4.setLayoutParams(lp);
        llLine5.setLayoutParams(lp);
        llLine6.setLayoutParams(lp);

        setOnClickMenu(llTambahPelanggan,ibtTambahPelanggan);
        setOnClickMenu(llPermintaanHarga,null);
        setOnClickMenu(llTambahSO, ibtTambahSO);
        setOnClickMenu(llEntryPaket, ibtEntryPaket);
        setOnClickMenu(llDaftarSO, ibtDaftarSO);
        setOnClickMenu(llTagihanPiutang, ibtTagihanPiutang);
        setOnClickMenu(llInfoStok, ibtInfoStok);
        setOnClickMenu(llTambahCanvas, ibtTambahCanvas);
        setOnClickMenu(llEntryCanvas, ibtEntryCanvas);
        setOnClickMenu(llKomisi, ibtKomisi);
        setOnClickMenu(llDenda, ibtDenda);
        setOnClickMenu(llBonus, ibtBonus);
        setOnClickMenu(llRetur, ibtRetur);
        setOnClickMenu(llCustomOrder, ibtCustomOrder);
        setOnClickMenu(llOmsetPenjualan, ibtOmsetPenjualan);
        setOnClickMenu(llUpdateMaster,ibtUpdateMaster);
        setOnClickMenu(llBarangTidakLaku, ibtBarangTidakLaku);
        setOnClickMenu(llSetoran, ibtSetoran);
        setOnClickMenu(llMenuAdmin, ibtMenuAdmin);
        setOnClickMenu(llCustomerLimit, ibtCustomerLimit);
        setOnClickMenu(llHapusDenda, ibtHapusDenda);

        CheckUserLevel();

        menuAnimation = AnimationUtils.loadAnimation(this, gmedia.net.id.kartikaelektrik.R.anim.menu_item_open);

        initEvent();
    }

    //region Slider Header
    private void getListHeaderSlider() {

        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", ServerURL.getHeaderImages, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(status.equals("200")){

                                JSONArray ja = responseAPI.getJSONArray("response");
                                for(int i = 0; i < ja.length();i++ ){

                                    JSONObject jo = ja.getJSONObject(i);
                                    sliderList.add(new CustomListItem(jo.getString("id"), jo.getString("gambar"), "",""));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, "Terjadi kesalahan saat memuat data, harap ulangi proses", Toast.LENGTH_LONG).show();
                        }

                        if(firstLoad){
                            setViewPagerTimer(changeHeaderTimes);
                            firstLoad = false;
                        }

                        setHeaderSlider();
                    }

                    @Override
                    public void onError(String result) {

                        Toast.makeText(context, "Terjadi kesalahan saat memuat data, harap ulangi proses", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void setHeaderSlider(){

        vpHeaderSlider.setAdapter(null);
        mAdapter = null;
        mAdapter = new HeaderSliderAdapter(context, sliderList);
        vpHeaderSlider.setAdapter(mAdapter);
        vpHeaderSlider.setCurrentItem(0);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.ci_indicator);
        indicator.setViewPager(vpHeaderSlider);
        mAdapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

    private void setViewPagerTimer(int seconds){
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {

                    if(vpHeaderSlider.getCurrentItem() == mAdapter.getCount() - 1){
                        vpHeaderSlider.setCurrentItem(0);

                    }else{
                        vpHeaderSlider.setCurrentItem(vpHeaderSlider.getCurrentItem() + 1);
                    }
                }
            });

        }
    }

    private void initEvent() {

        llDataPiutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DashboardContainer.class);
                intent.putExtra("kodemenu","tagihanpiutang");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        llPiutangTelat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DetailPiutangJatuhTempo.class);
                intent.putExtra("kode", "JT");
                startActivity(intent);
            }
        });

        llDataRetur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DashboardContainer.class);
                intent.putExtra("kodemenu","retur");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        llDendaPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DashboardContainer.class);
                intent.putExtra("kodemenu","denda");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        getDashboard();
    }

    private void getDashboard() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nik", sessionManager.getNik());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(MainActivity.this, jsonBody, "POST", ServerURL.getDashboard, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length(); i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    String nomor = jo.getString("nomor");

                                    if(nomor.equals("1")){

                                        tvDataPiutang.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("sisa"))));
                                    }else if(nomor.equals("2")){
                                        tvPiutangTelat.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("sisa"))));
                                    }else if(nomor.equals("3")){
                                        tvDataRetur.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("sisa"))));
                                    }else if(nomor.equals("4")){
                                        tvDendaPelanggan.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(jo.getString("sisa"))));
                                    }
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                    }
                });
    }

    private void CheckUserLevel(){

        if(levelUser == 0 || levelUser == 1){ // 0 Owner , 1 Accounting

            //getJumlahSOPermintaanHarga();

            /*llPermintaanHarga.setVisibility(View.VISIBLE);
            llLogo.setVisibility(View.GONE);*/
            //llPermintaanHarga.setVisibility(View.VISIBLE);
        }else{
            /*llPermintaanHarga.setVisibility(View.GONE);
            llLogo.setVisibility(View.VISIBLE);*/
            //llPermintaanHarga.setVisibility(View.INVISIBLE);
        }

        if(sessionManager.getLaba().equals("1")){

            llMenuAdmin.setVisibility(View.VISIBLE);
            //llLine6.setVisibility(View.VISIBLE);
        }else{

            llMenuAdmin.setVisibility(View.INVISIBLE);
            //llLine6.setVisibility(View.GONE);
        }
    }

    private void checkVersion(){

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
        latestVersion = "";
        link = "";
        getSupportActionBar().setSubtitle("V"+ version + " a/n " + sessionManager.getFullName());

        urlGetLatestVersion = getResources().getString(R.string.url_get_latest_version);

        ApiVolley request = new ApiVolley(MainActivity.this, new JSONObject(), "GET", urlGetLatestVersion, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                if(dialogVersion != null){
                    if(dialogVersion.isShowing()) dialogVersion.dismiss();
                }

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){
                        latestVersion = responseAPI.getJSONObject("response").getString("versi");
                        link = responseAPI.getJSONObject("response").getString("link");
                        updateRequired = (iv.parseNullInteger(responseAPI.getJSONObject("response").getString("wajib")) == 1) ? true : false;

                        if(!version.trim().equals(latestVersion.trim()) && link.length() > 0){

                            if(updateRequired){

                                dialogVersion = new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(R.mipmap.kartika_logo)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialogVersion.dismiss();
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }else{

                                dialogVersion = new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(R.mipmap.kartika_logo)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialogVersion.dismiss();
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialogVersion.dismiss();
                                                //dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                if(dialogVersion != null){
                    if(dialogVersion.isShowing()) dialogVersion.dismiss();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkVersion();
        CheckUserLevel();

        if(!sessionManager.getLaba().equals("1")) statusCheck();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        }else{

            try {
                new CountDownTimer(4000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        if(!iv.isServiceRunning(MainActivity.this, LocationUpdater.class)){
                            startService(new Intent(getApplicationContext(), LocationUpdater.class));
                        }
                    }

                }.start();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void buildAlertMessageNoGps() {
        if(!dialogActive){
            dialogActive = true;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mohon Hidupkan Akses Lokasi (GPS) Anda.")
                    .setCancelable(false)
                    .setPositiveButton("Hidupkan", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogActive = false;
                }
            });
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    public void getJumlahSOPermintaanHarga() {

        String urlGetSOPermintaanHarga = getResources().getString(gmedia.net.id.kartikaelektrik.R.string.url_get_so_permintaan_harga);

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(MainActivity.this, jsonBody, "GET", urlGetSOPermintaanHarga , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                String jml = "99+";
                                if(arrayJSON.length() < 100){
                                    jml = String.valueOf(arrayJSON.length());
                                }
                                btnJumlahSOPermintaanHarga.setText(jml);
                            }


                        }catch (Exception e){
                            e.printStackTrace();
                            btnJumlahSOPermintaanHarga.setText("0");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        btnJumlahSOPermintaanHarga.setText("0");
                    }
                });
    }

    private void setOnClickMenu(final LinearLayout ll, final ImageButton ib){

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(MainActivity.this,DashboardContainer.class);

                if (ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_tambah_pelanggan){
                    intent.putExtra("kodemenu","tambahpelanggan");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_permintaan_harga_order){
                    intent.putExtra("kodemenu","permintaanhargaorder");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_tambah_so){
                    intent.putExtra("kodemenu","tambahso");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == R.id.v_menu_entry_order_paket){
                    intent.putExtra("kodemenu","entrypaket");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_daftar_so){
                    intent.putExtra("kodemenu","daftarso");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_tagihan_piutang){
                    intent.putExtra("kodemenu","tagihanpiutang");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_info_stok){
                    intent.putExtra("kodemenu","infostok");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == R.id.v_menu_tambah_canvas){
                    intent.putExtra("kodemenu","tambahcanvas");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == R.id.v_menu_entry_canvas){
                    intent.putExtra("kodemenu","entrycanvas");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_komisi){
                    intent.putExtra("kodemenu","komisi");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_denda){
                    intent.putExtra("kodemenu","denda");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_bonus){
                    intent.putExtra("kodemenu","bonus");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == R.id.v_menu_retur){
                    intent.putExtra("kodemenu","retur");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == R.id.v_menu_omset_sales){
                    intent.putExtra("kodemenu","omsetsales");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == R.id.v_menu_omset_penjualan){
                    intent.putExtra("kodemenu","omsetpenjualan");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if (ll.getId() == R.id.v_menu_custom_order){
                    intent.putExtra("kodemenu","ordercustom");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_update_master){
                    /*MasterDataHandler mdh = new MasterDataHandler(MainActivity.this);
                    mdh.updateMasterData();*/
                }else if(ll.getId() == R.id.v_menu_barang_tidak_laku){
                    intent.putExtra("kodemenu","barangtaklaku");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == R.id.v_menu_setoran){
                    intent.putExtra("kodemenu","setoran");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == R.id.v_menu_admin){
                    intent.putExtra("kodemenu","menuadmin");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == R.id.v_menu_customer_limit){
                    intent.putExtra("kodemenu","menucustomerlimit");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(ll.getId() == R.id.v_menu_hapus_denda){
                    intent.putExtra("kodemenu","menuhapusdenda");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            }
        });

        if(ib != null){
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intent = new Intent(MainActivity.this,DashboardContainer.class);

                    if (ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tambah_pelanggan){
                        intent.putExtra("kodemenu","tambahpelanggan");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tambah_so){
                        intent.putExtra("kodemenu","tambahso");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_entry_order_paket){
                        intent.putExtra("kodemenu","entrypaket");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_daftar_so){
                        intent.putExtra("kodemenu","daftarso");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tagihan_piutang){
                        intent.putExtra("kodemenu","tagihanpiutang");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_info_stok){
                        intent.putExtra("kodemenu","infostok");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_tambah_canvas){
                        intent.putExtra("kodemenu","tambahcanvas");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_entry_canvas){
                        intent.putExtra("kodemenu","entrycanvas");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_komisi){
                        intent.putExtra("kodemenu","komisi");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_denda){
                        intent.putExtra("kodemenu","denda");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_bonus){
                        intent.putExtra("kodemenu","bonus");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_retur){
                        intent.putExtra("kodemenu","retur");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_omset_sales){
                        intent.putExtra("kodemenu","omsetsales");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_omset_penjualan){
                        intent.putExtra("kodemenu","omsetpenjualan");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_custom_order){
                        intent.putExtra("kodemenu","ordercustom");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_update_master){
                        /*MasterDataHandler mdh = new MasterDataHandler(MainActivity.this);
                        mdh.updateMasterData();*/
                    }else if(ib.getId() == R.id.ibt_menu_barang_tidak_laku){
                        intent.putExtra("kodemenu","barangtaklaku");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_setoran){
                        intent.putExtra("kodemenu","setoran");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_admin){
                        intent.putExtra("kodemenu","menuadmin");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_customer_limit){
                        intent.putExtra("kodemenu","menucustomerlimit");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else if(ib.getId() == R.id.ibt_menu_hapus_denda){
                        intent.putExtra("kodemenu","menuhapusdenda");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            /*stopService(new Intent(MainActivity.this, BackgroundLocationService.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            System.exit(0);*/
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        // take 2 second before the doubleBackToExitPressedOnce become false again
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        SessionManager sessionManager1 = new SessionManager(MainActivity.this);
        MenuItem item = menu.findItem(R.id.option_kill_access);
        if(sessionManager1.getLevel().equals("0")) item.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == gmedia.net.id.kartikaelektrik.R.id.option_ubah_password) {
            Intent intent = new Intent(MainActivity.this, ChangePassword.class);
            startActivity(intent);
            return true;
        }else if(id == gmedia.net.id.kartikaelektrik.R.id.option_logout){

            if(iv.isServiceRunning(MainActivity.this, LocationUpdater.class)){
                stopService(new Intent(getApplicationContext(), LocationUpdater.class));
            }
            if(!sessionManager.getLaba().equals("1")){

                new LocationUpdateHandler(MainActivity.this,"Logout");

            }else{

                sessionManager.logoutUser();
                finish();
            }


            return true;
        }else if(id == R.id.option_kill_access){

            getMobileStatus();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getMobileStatus() {

        String urlAPI = getResources().getString(R.string.url_get_status);

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(MainActivity.this, jsonBody, "GET", urlAPI , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){
                                String mobileStatus = responseAPI.getJSONObject("response").getString("status");
                                DialogForm(mobileStatus);
                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }

    private void DialogForm(String status) {

        dialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.layout_status_mobile, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.kartika_logo);
        dialog.setTitle("Pengaturan Status");

        swStatus = (Switch) dialogView.findViewById(R.id.sw_status);

        if(status.equals("1")) {
            swStatus.setChecked(true);
        }else{
            swStatus.setChecked(false);
        }

        edUsername = (EditText) dialogView.findViewById(R.id.edt_username);
        edPassword = (EditText) dialogView.findViewById(R.id.edt_password);

        SessionManager user = new SessionManager(MainActivity.this);
        edUsername.setText(user.getUser());

        dialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        dialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });



        dialogViews = dialog.create();
        dialogViews.show();

        dialogViews.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String statusUpdate = swStatus.isChecked()? "1" : "0";
                String userUpdate = edUsername.getText().toString();
                String passUpdate = edPassword.getText().toString();
                update_status(statusUpdate, userUpdate, passUpdate);
            }
        });
    }

    public void update_status(String status, String username, String password) {

        String urlAPI = getResources().getString(R.string.url_update_status);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", status);
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(MainActivity.this, jsonBody, "PUT", urlAPI , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");

                            if(iv.parseNullInteger(status) == 200) dialogViews.dismiss();
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }
}
