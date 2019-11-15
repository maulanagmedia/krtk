package gmedia.net.id.kartikaelektrik;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.adapter.DashboardAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;
import gmedia.net.id.kartikaelektrik.util.LocationUpdater;
import gmedia.net.id.kartikaelektrik.util.MasterDataHandler;
import gmedia.net.id.kartikaelektrik.util.RuntimePermissionsActivity;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Dashboard extends RuntimePermissionsActivity {

    private final String TAG = "DashboardLog";
    private Animation menuAnimation;
    private ImageButton ibtTambahPelanggan, ibtTambahSO, ibtDaftarSO, ibtTagihanPiutang, ibtInfoStok, ibtKomisi, ibtDenda, ibtBonus;
    private LinearLayout llTambahPelanggan, llPermintaanHarga, llTambahSO, llDaftarSO, llTagihanPiutang, llInfoStok, llKomisi, llDenda, llBonus;
    private Intent intent;
    private boolean doubleBackToExitPressedOnce = false;
    private ListView lvDashboard;
    private String urlGetSO;
    private LinearLayout llUpdateMaster;
    private ImageButton ibtUpdateMaster;
    private SessionManager sessionManager;
    private ItemValidation iv = new ItemValidation();
    private Button btnJumlahSOPermintaanHarga;
    private HashMap<String, String> user;
    private Integer levelUser;
    private static final int REQUEST_PERMISSIONS = 20;
    private LinearLayout llEntryCanvas, llRetur, llOmsetSales, llOmsetPenjualan;
    private ImageButton ibtEntryCanvas, ibtRetur, ibtOmsetSales, ibtOmsetPenjualan;
    private LinearLayout llEntryPaket;
    private ImageButton ibtEntryPaket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gmedia.net.id.kartikaelektrik.R.layout.activity_dashboard);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        Toolbar toolbar = (Toolbar) findViewById(gmedia.net.id.kartikaelektrik.R.id.toolbar);
        setSupportActionBar(toolbar);

        // for android > M
        if (ContextCompat.checkSelfPermission(
                Dashboard.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                Dashboard.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {

            Dashboard.super.requestAppPermissions(new
                            String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WAKE_LOCK, android.Manifest.permission.READ_EXTERNAL_STORAGE}, gmedia.net.id.kartikaelektrik.R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        /*MasterDataHandler mdh = new MasterDataHandler(Dashboard.this);
        mdh.checkWeeklyUpdate();*/

        initUI();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void initUI() {

        urlGetSO = ServerURL.getSO;
        llTambahPelanggan = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_tambah_pelanggan);
        llPermintaanHarga = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_permintaan_harga_order);
        llTambahSO = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_tambah_so);
        llEntryPaket = (LinearLayout) findViewById(R.id.v_menu_entry_order_paket);
        llDaftarSO = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_daftar_so);
        llTagihanPiutang = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_tagihan_piutang);
        llInfoStok = (LinearLayout) findViewById(R.id.v_menu_info_stok);
        llEntryCanvas = (LinearLayout) findViewById(R.id.v_menu_entry_canvas);
        llKomisi = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_komisi);
        llDenda = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_denda);
        llBonus = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_bonus);
        llRetur = (LinearLayout) findViewById(R.id.v_menu_retur);
        llOmsetSales = (LinearLayout) findViewById(R.id.v_menu_omset_sales);
        llOmsetPenjualan = (LinearLayout) findViewById(R.id.v_menu_omset_penjualan);
        llUpdateMaster = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.v_menu_update_master);
        sessionManager = new SessionManager(Dashboard.this);
        user = sessionManager.getUserDetails();
        levelUser = iv.parseNullInteger(user.get(sessionManager.TAG_LEVEL));

        ibtTambahPelanggan = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tambah_pelanggan);
        ibtTambahSO = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tambah_so);
        ibtEntryPaket = (ImageButton) findViewById(R.id.ibt_menu_entry_order_paket);
        ibtDaftarSO = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_daftar_so);
        ibtTagihanPiutang = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_tagihan_piutang);
        ibtInfoStok = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_info_stok);
        ibtEntryCanvas = (ImageButton) findViewById(R.id.ibt_menu_entry_canvas);
        ibtKomisi = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_komisi);
        ibtDenda = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_denda);
        ibtBonus = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_bonus);
        ibtRetur = (ImageButton) findViewById(R.id.ibt_menu_retur);
        ibtOmsetSales = (ImageButton) findViewById(R.id.ibt_menu_omset_sales);
        ibtOmsetPenjualan = (ImageButton) findViewById(R.id.ibt_menu_omset_penjualan);
        ibtUpdateMaster = (ImageButton) findViewById(gmedia.net.id.kartikaelektrik.R.id.ibt_menu_update_master);
        lvDashboard = (ListView) findViewById(gmedia.net.id.kartikaelektrik.R.id.lv_dashboard);
        btnJumlahSOPermintaanHarga = (Button) findViewById(gmedia.net.id.kartikaelektrik.R.id.btn_status_permohonan);

        setOnClickMenu(llTambahPelanggan,ibtTambahPelanggan);
        setOnClickMenu(llPermintaanHarga,null);
        setOnClickMenu(llTambahSO, ibtTambahSO);
        setOnClickMenu(llEntryPaket, ibtEntryCanvas);
        setOnClickMenu(llDaftarSO, ibtDaftarSO);
        setOnClickMenu(llTagihanPiutang, ibtTagihanPiutang);
        setOnClickMenu(llInfoStok, ibtInfoStok);
        setOnClickMenu(llEntryCanvas, ibtEntryCanvas);
        setOnClickMenu(llKomisi, ibtKomisi);
        setOnClickMenu(llDenda, ibtDenda);
        setOnClickMenu(llBonus, ibtBonus);
        setOnClickMenu(llRetur, ibtRetur);
        setOnClickMenu(llOmsetSales, ibtOmsetSales);
        setOnClickMenu(llOmsetPenjualan, ibtOmsetPenjualan);
        setOnClickMenu(llUpdateMaster,ibtUpdateMaster);

        getDashboardJSON();

        CheckUserLevel();

        menuAnimation = AnimationUtils.loadAnimation(this, gmedia.net.id.kartikaelektrik.R.anim.menu_item_open);
    }

    private void CheckUserLevel(){

        if(levelUser == 0 || levelUser == 1){ // 0 Owner , 1 Accounting
            getJumlahSOPermintaanHarga();
            llPermintaanHarga.setVisibility(View.VISIBLE);
        }else{
            llPermintaanHarga.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDashboardJSON();
        CheckUserLevel();
    }

    public void getJumlahSOPermintaanHarga() {

        String urlGetSOPermintaanHarga = getResources().getString(gmedia.net.id.kartikaelektrik.R.string.url_get_so_permintaan_harga);

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(Dashboard.this, jsonBody, "GET", urlGetSOPermintaanHarga , "", "", 0,
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

                intent = new Intent(Dashboard.this,DashboardContainer.class);

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
                }else if(ll.getId() == gmedia.net.id.kartikaelektrik.R.id.v_menu_update_master){

                }

            }
        });

        if(ib != null){
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intent = new Intent(Dashboard.this,DashboardContainer.class);

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
                    }else if(ib.getId() == gmedia.net.id.kartikaelektrik.R.id.ibt_menu_update_master){
                        /*MasterDataHandler mdh = new MasterDataHandler(Dashboard.this);
                        mdh.updateMasterData();*/
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            stopService(new Intent(Dashboard.this, LocationUpdater.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            System.exit(0);
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

    public void getDashboardJSON(){

        lvDashboard.setAdapter(null);
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(Dashboard.this, jsonBody, "GET", urlGetSO, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){
                                ShowDashBoardItem(result);
                            }else{
                                ShowDashBoardItem(null);
                            }

                        }catch (Exception e){
                            ShowDashBoardItem(null);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        ShowDashBoardItem(null);
                    }
                });
    }

    private void ShowDashBoardItem(String json){

        lvDashboard.setAdapter(null);

        if(json != null){

            OrderJSONHandler pj = new OrderJSONHandler(json,"all");
            pj.ParseOrderJSON();
            DashboardAdapter cl = new DashboardAdapter(Dashboard.this, pj.nobukti, pj.tgltempo, pj.total, pj.status);
            lvDashboard.setAdapter(cl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mainMenu = new MenuInflater(this);
        mainMenu.inflate(gmedia.net.id.kartikaelektrik.R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == gmedia.net.id.kartikaelektrik.R.id.option_ubah_password) {
            Intent intent = new Intent(Dashboard.this, ChangePassword.class);
            startActivity(intent);
            return true;
        }else if(id == gmedia.net.id.kartikaelektrik.R.id.option_logout){

            if(!sessionManager.getLaba().equals("1")) new LocationUpdateHandler(Dashboard.this,"Logout");

            if(sessionManager.isLoggedIn()) {

                sessionManager.logoutUser(Dashboard.this);
            }
            return true;
        }else if( id == R.id.option_profile){


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}