package gmedia.net.id.kartikaelektrik.activityTambahOrderSales;

import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;
import gmedia.net.id.kartikaelektrik.activitySalesOrderDetail.DetailSalesOrder;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrderDetail extends AppCompatActivity {

    private String kdCus, namaPelanggan, tempo, idTempo, kodeBarang, namaBarang, noSalesOrder = "", hargaPcs;
    private String approveLevel = ""; // 1. Keuangan, 2. Bos
    private String tanggal, tanggalTempo, jumlah, diskon, lastDiskon, isiSatuan1, isiSatuan2, selectedIsiSatuan, jumlahpcs;
    private ItemValidation iv = new ItemValidation();
    private EditText edNoSO, edNamaPelanggan, edNamaBarang, edTanggal, edTanggalTempo, edJumlah, edHarga, edDiskon, edHargaWithDiskon, edHargaTotal;
    private RadioButton rbQuantity2, rbQuantity3;
    private final String TAG = "Order.Detail";
    private TextInputLayout tilNoSO, tilTanggalTempo, tilJumlah, tilDiskon, tilHargaTotal;
    private String urlGetSOByID;
    private String urlListBarangByID;
    private String urlSaveSO;
    private String urlGetHarga;
    private String urlGetGudang;
    private String flagHarga, satuan2, satuan3, selectedSatuan, gudangKecil, gudangBesar, selectedGudang;
    private int minStok, maxDiskon;
    private String harga, hargaNetto;
    private RadioGroup rbgUnitQuantity;
    private LinearLayout llNoSO;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    private String idOrderDetail;
    private boolean updateOrderFlag = false;
    private String hargaToSave = "0";
    private int statusSO;
    private ProgressBar pbLoading;
    private String crBayar = "";
    private int statusChangeHarga = 1;
    private boolean afterChangeFlag = false;
    private TextView tvKetSatuan;
    private EditText edtKetarangan;
    private RadioGroup rgPilihanPPN, rgJenisPPN;
    private RadioButton rbNonPPN, rbPPN, rbEFaktur, rbCast, rbOperan;
    private TextView tvGudangBesar, tvGudangKecil, tvTempo;
    private String currentString = "", currentString1 = "";
    private String keteranganBarang = "";
    private String formatDate = "", formatDateDisplay = "";
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();

    }

    private void initUI() {

        /*
        * Define all your activity here
        */

        // API
        urlGetSOByID = ServerURL.getBarangDetail;
        urlListBarangByID = ServerURL.getListBarangById;
        urlSaveSO = ServerURL.saveSO;
        urlGetHarga = ServerURL.getHargaSODetail;
        urlGetGudang = ServerURL.getGudangSODetailByID;

        Bundle extras = getIntent().getExtras();
        if (extras != null){

            idOrderDetail = extras.getString("idOrderDetail");
            noSalesOrder = extras.getString("noSalesOrder");
            kdCus = extras.getString("kdCus");
            namaPelanggan = extras.getString("namaPelanggan");
            tempo = extras.getString("tempo", "30");
            idTempo = extras.getString("idTempo", "");
            jumlah = extras.getString("jumlah");
            selectedSatuan = extras.getString("satuan");
            kodeBarang = extras.getString("kodeBarang");
            namaBarang = extras.getString("namaBarang");
            statusSO = iv.parseNullInteger(extras.getString("statusSO"));
            setTitle("Order Detail");

            llNoSO = (LinearLayout) findViewById(R.id.ll_no_so_container);
            tilNoSO = (TextInputLayout) findViewById(R.id.til_no_so);
            edNoSO = (EditText) findViewById(R.id.edt_no_so);
            edNamaPelanggan = (EditText) findViewById(R.id.edt_nama_pelanggan);
            edNamaBarang = (EditText) findViewById(R.id.edt_nama_barang);
            edTanggal = (EditText) findViewById(R.id.edt_tanggal);
            tilTanggalTempo = (TextInputLayout) findViewById(R.id.til_tanggal_tempo);
            edTanggalTempo = (EditText) findViewById(R.id.edt_tanggal_tempo);
            tilJumlah = (TextInputLayout) findViewById(R.id.til_quantity);
            edJumlah = (EditText) findViewById(R.id.edt_quantity);
            rbgUnitQuantity = (RadioGroup) findViewById(R.id.rbg_unit);
            rbQuantity2 = (RadioButton) findViewById(R.id.rb_quantity_2);
            rbQuantity3 = (RadioButton) findViewById(R.id.rb_quantity_3);
            tvKetSatuan = (TextView) findViewById(R.id.tv_ket_satuan);
            edHarga = (EditText) findViewById(R.id.edt_harga);
            tilDiskon = (TextInputLayout) findViewById(R.id.til_diskon);
            edDiskon = (EditText) findViewById(R.id.edt_diskon);
            pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
            edHargaWithDiskon = (EditText) findViewById(R.id.edt_harga_with_dicsount);
            tilHargaTotal = (TextInputLayout) findViewById(R.id.til_harga_total);
            edHargaTotal = (EditText) findViewById(R.id.edt_harga_total);
            llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
            tvSave = (TextView) findViewById(R.id.tv_save);
            tvGudangBesar = (TextView) findViewById(R.id.tv_gudang_besar);
            tvGudangKecil = (TextView) findViewById(R.id.tv_gudang_kecil);
            tvTempo = (TextView) findViewById(R.id.tv_tempo);

            edtKetarangan = (EditText) findViewById(R.id.edt_keterangan);
            rgPilihanPPN = (RadioGroup) findViewById(R.id.rg_pilihan_ppn);
            rgJenisPPN = (RadioGroup) findViewById(R.id.rg_jenis_ppn);
            rbNonPPN = (RadioButton) findViewById(R.id.rb_non_ppn);
            rbPPN = (RadioButton) findViewById(R.id.rb_ppn);
            rbEFaktur = (RadioButton) findViewById(R.id.rb_e_faktur);
            rbCast = (RadioButton) findViewById(R.id.rb_cash);
            rbOperan = (RadioButton) findViewById(R.id.rb_operan);

            edNamaPelanggan.setText(namaPelanggan);
            edNamaBarang.setText(namaBarang);

            tvTempo.setText(tempo + " Hari");

            if(noSalesOrder != null && !noSalesOrder.toLowerCase().equals("null") && noSalesOrder.length() > 0){

                if(idOrderDetail != null && !idOrderDetail.toLowerCase().equals("null")){

                    tvSave.setText("Ubah Order");
                    initEventAddOrder();
                    updateOrderFlag = true;
                }else{
                    tvSave.setText("Tambah Order");
                    initEventAddOrder();
                    rbQuantity3.setChecked(true);
                }

            }else{

                tvSave.setText("Buat Order Baru");
                llNoSO.setVisibility(View.GONE);
                initEventNewSO();
                rbQuantity3.setChecked(true);
            }

            // Event radion button
            rgPilihanPPN.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if(rbNonPPN.isChecked()){

                        rgJenisPPN.setVisibility(View.GONE);
                    }else{

                        rgJenisPPN.setVisibility(View.VISIBLE);
                    }
                }
            });

        }else{
            finish();
        }
    }

    private void initEventAddOrder(){

        // add new order from last SO
        llNoSO.setVisibility(View.VISIBLE);
        edNoSO.setText(noSalesOrder);

        edTanggalTempo.setKeyListener(null);
        getDetailSO();
        getDetailBarang();
        getClickCreateSOEvent();
    }

    private void initEventNewSO() {

        // add New Sales Order
        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        tanggal = iv.getCurrentDate(formatDate);
        edTanggal.setText(iv.ChangeFormatDateString(tanggal, formatDate, formatDateDisplay));

        // Get Tanggal tempo
        //getTanggalTempo();
        tanggalTempo = iv.sumDate(tanggal,iv.parseNullInteger(tempo),formatDate);
        tvTempo.setText(tempo + " Hari");
        edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));
        edTanggalTempo.setKeyListener(null);

        //iv.datePickerEventMax(OrderDetail.this,edTanggalTempo,"RIGHT",formatDateDisplay, iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay), iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));

        //endregion

        getDetailBarang();

        getClickCreateSOEvent();
    }

    private void getDetailSO(){

        /* Get Detail Sales Order
        * Des: get value of tanggal & tanggal tempo
        */
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetSOByID +noSalesOrder, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            JSONObject responseJSON = responseAPI.getJSONObject("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){
                                // Get Header
                                try{

                                    tanggal = responseJSON.getJSONObject("so_header").getString("tgl");
                                    tanggalTempo = responseJSON.getJSONObject("so_header").getString("tgltempo");
                                    String formateDate = getResources().getString(R.string.format_date);
                                    String formateDateDisplay = getResources().getString(R.string.format_date_display);

                                    edTanggal.setText(iv.ChangeFormatDateString(tanggal, formateDate, formateDateDisplay));
                                    edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo, formateDate, formateDateDisplay));
                                    //iv.datePickerEvent(OrderDetail.this,edTanggalTempo,"RIGHT",formateDateDisplay, iv.ChangeFormatDateString(tanggalTempo, formateDate, formateDateDisplay));

                                    edtKetarangan.setText(responseJSON.getJSONObject("so_header").getString("keterangan"));
                                    String ppn = responseJSON.getJSONObject("so_header").getString("nota_ppn");
                                    String jenisPPN = responseJSON.getJSONObject("so_header").getString("jenis_ppn");

                                    if(ppn.equals("Y")){

                                        rbPPN.setChecked(true);
                                        rgJenisPPN.setVisibility(View.VISIBLE);
                                        if(jenisPPN.equals("E")){
                                            rbEFaktur.setChecked(true);
                                        }else if(jenisPPN.equals("C")){
                                            rbCast.setChecked(true);
                                        }else if(jenisPPN.equals("O")){
                                            rbOperan.setChecked(true);
                                        }
                                    }else if(ppn.equals("N")){

                                        rbNonPPN.setChecked(true);
                                        rgJenisPPN.setVisibility(View.GONE);
                                    }

                                    getClickCreateSOEvent();
                                }catch (Exception e){
                                    e.printStackTrace();
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

    private void getDetailBarang() {

        /*
        * Access the detail barang API
        * Des: get value satuan, gudang, min stok & max diskon
        */

        // Get Detail Barang
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlListBarangByID +kodeBarang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            JSONArray arrayJSON = responseAPI.getJSONArray("response");
                            String s = responseAPI.getJSONObject("metadata").getString("message");
                            for(int i = 0; i < arrayJSON.length();i++){
                                JSONObject jo = arrayJSON.getJSONObject(i);
                                flagHarga = jo.getString("flag_harga");
                                satuan2 = jo.getString("sat2");
                                satuan3 = jo.getString("sat3");
                                gudangKecil = jo.getString("gudang_jual_k");
                                gudangBesar = jo.getString("gudang_jual_b");
                                isiSatuan1 = jo.getString("isi1");
                                isiSatuan2 = jo.getString("isi2");
                                //keteranganBarang = jo.getString("keterangan");
                                keteranganBarang = "";

                                try {
                                    minStok = Integer.parseInt(jo.getString("stokminim"));
                                }catch (Exception e){
                                    minStok = 0;
                                }

                                try {
                                    maxDiskon = Integer.parseInt(jo.getString("maxdiskon"));
                                }catch (Exception e){
                                    maxDiskon = 0;
                                }

                                preValidateElement();
                            }

                            getDetailStokBarang();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }

    private void getDetailStokBarang() {

        pbLoading.setVisibility(View.VISIBLE);
        ApiVolley restService = new ApiVolley(OrderDetail.this, new JSONObject(), "GET", ServerURL.getDetailStokBarang+kodeBarang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        pbLoading.setVisibility(View.GONE);
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONObject jo = responseAPI.getJSONObject("response");
                                tvGudangBesar.setText("Gudang Besar : \n"+jo.getString("gudangbesar"));
                                tvGudangKecil.setText("Gudang Kecil : \n"+jo.getString("gudangkecil"));
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(OrderDetail.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(OrderDetail.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void preValidateElement(){
        /*
        * 1. Pengaturan minimal stok
        * 2. Pengaturan Gudang Besar dan Gudang Kecil
        * 3. pengaturan kolom diskon
        * 4. Inisialisasi harga
        * 5. Harga per satuan
        * 6. Diskon
        */

        //1. Pengaturan minimal stok
        /*if(Integer.parseInt(isiSatuan2) >= Integer.parseInt(isiSatuan1)){
            iv.setLimitValue( edJumlah, tilJumlah, "MIN", minStok, "Minimal "+ minStok + " " + satuan3);
        }else{
            iv.setLimitValue( edJumlah, tilJumlah, "MIN", minStok, "Minimal "+ minStok + " " + satuan2);
        }*/

        //2. Pengaturan gudang besar dan gudang kecil
        if(updateOrderFlag){ // if update order

            edJumlah.setText(jumlah);
            if(selectedSatuan.toUpperCase().equals(satuan2.toUpperCase())){
                selectedIsiSatuan = isiSatuan2;
                rbQuantity2.setChecked(true);
            }else{ // if add order
                selectedIsiSatuan = isiSatuan1;
                rbQuantity3.setChecked(true);
            }

            Bundle extras = getIntent().getExtras();
            if(extras != null){

                hargaPcs = extras.getString("hargapcs");

                if(flagHarga.trim().equals("2")){ //diskon
                    edHarga.setText(iv.ChangeToCurrencyFormat(extras.getString("harga")));
                    edDiskon.setText(extras.getString("diskon"));

                    edDiskon.setFocusable(true);
                    edDiskon.setKeyListener(new EditText(OrderDetail.this).getKeyListener());
                    edDiskon.setFocusableInTouchMode(true);

                    edHargaWithDiskon.setFocusable(true);
                    edHargaWithDiskon.setKeyListener(new DigitsKeyListener(true,true));
                    edHargaWithDiskon.setFocusableInTouchMode(true);

                    if(iv.parseNullDouble(extras.getString("harganetto")) > 0){ // Custom Harga netto
                        statusChangeHarga = 2;
                        afterChangeFlag = true;
                        edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(extras.getString("harganetto")));
                    }else{
                        statusChangeHarga = 1;
                    }

                    CalculateHargaByDiskon();
                }else{
                    edHarga.setText(iv.ChangeToCurrencyFormat(extras.getString("harga")));
                    edDiskon.setText(extras.getString("diskon"));
                    edDiskon.setKeyListener(null);
                    edHargaWithDiskon.setKeyListener(null);
                    edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(extras.getString("harganetto")));
                    edHargaTotal.setText(iv.ChangeToCurrencyFormat(extras.getString("total")));
                }
            }

        }else{

            selectedSatuan = satuan3;
            selectedIsiSatuan = isiSatuan1;
            selectedGudang = gudangBesar;
        }

        if(satuan2.trim().equals(satuan3.trim())){
            tvKetSatuan.setText(isiSatuan1 + " " + satuan2 + " : " + isiSatuan2 + " " + satuan3 + "\n"+keteranganBarang);
            //tvKetSatuan.setVisibility(View.GONE);
            /*rbQuantity2.setText(satuan2);
            rbQuantity3.setVisibility(View.INVISIBLE);*/
            rbQuantity3.setText(satuan3);
            rbQuantity2.setVisibility(View.GONE);
//            listRadioButton = new ArrayList<RadioButton>();
        }else{

            tvKetSatuan.setText(isiSatuan1 + " " + satuan2 + " : " + isiSatuan2 + " " + satuan3+ "\n"+keteranganBarang);
            rbQuantity2.setText(satuan2);
            rbQuantity2.setVisibility(View.GONE);
            rbQuantity3.setText(satuan3);
            rbQuantity3.setVisibility(View.VISIBLE);
        }

        //3. Pengaturan kolom diskon
        if(Integer.parseInt(flagHarga) == 2){
            //discount Allowed
        }else{

        }

        //region 4. inisialisasi harga
        edJumlah.addTextChangedListener(new TextWatcher() {

            boolean isTyping = false;

            Timer timer = new Timer();
            long delay;
            int jml = edJumlah.length();
            int jmlText = edJumlah.length();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                jmlText = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                timer = new Timer();
                delay = 1000;

                if(jmlText < charSequence.length()){
                    jml++;
                }else if(jmlText > charSequence.length()){
                    jml--;
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {

                final int counter = jml;

                if(!isTyping) {

                    // start typing
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {

                            @Override
                            public void run() {

                                isTyping = false;

                                if(counter == editable.length()){

                                    // stopped typing
                                    if(edJumlah.getText().length() > 0 /*&& (Long.parseLong(edJumlah.getText().toString()) * Integer.parseInt(selectedIsiSatuan)) >= minStok*/ ){

                                        try {
                                            jumlah = edJumlah.getText().toString();
                                            getHargaBarang();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }else if(edJumlah.getText().length() <= 0 /*|| (Long.parseLong(edJumlah.getText().toString()) * Integer.parseInt(selectedIsiSatuan)) < minStok*/ ){
                                        ClearHargaField();
                                    }
                                }
                            }
                        },
                        delay
                );
            }
        });
        //endregion

        //region 5. Harga per satuan
        rbgUnitQuantity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rbSat2 = rbQuantity2.getId();
                int rbSat3 = rbQuantity3.getId();

                if(i == rbSat3){
                    selectedIsiSatuan = isiSatuan1;
                }else{
                    selectedIsiSatuan = isiSatuan2;
                }

                if(edJumlah.getText().length() > 0 && (Integer.parseInt(edJumlah.getText().toString())* Integer.parseInt(selectedIsiSatuan)) >= minStok ){

                    try {
                        jumlah = edJumlah.getText().toString();
                        getHargaBarang();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(edJumlah.getText().length() <= 0 || (Integer.parseInt(edJumlah.getText().toString())* Integer.parseInt(selectedIsiSatuan)) < minStok ){
                    ClearHargaField();
                }
            }
        });

        edHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edHarga.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edHarga.setText(formatted);
                    edHarga.setSelection(formatted.length());

                    if(flagHarga.equals("2")){

                        if(afterChangeFlag && statusChangeHarga == 2){
                            afterChangeFlag = false;
                            statusChangeHarga = 2;
                        }else{
                            statusChangeHarga = 1;
                        }
                        CalculateHargaByDiskon();
                    }else{

                        //double hargaDouble = iv.parseNullDouble(edHarga.getText().toString().replace(".", "").replace(",", ""));
                        double hargaDouble = iv.parseNullDouble(cleanString);
                        edHargaWithDiskon.setText(formatted);
                        edHargaTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(iv.parseNullDouble(selectedIsiSatuan) * hargaDouble * iv.parseNullDouble(jumlah))));
                    }

                    edHarga.addTextChangedListener(this);
                }
            }
        });

        edHargaWithDiskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString1)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edHargaWithDiskon.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString1 = formatted;
                    edHargaWithDiskon.setText(formatted);
                    edHargaWithDiskon.setSelection(formatted.length());

                    if(flagHarga.equals("2")){
                        statusChangeHarga = 2;
                        CalculateHargaByDiskon();
                    }else{
                        //double hargaDouble = iv.parseNullDouble(edHarga.getText().toString().replace(".", "").replace(",", ""));
                        double hargaDouble = iv.parseNullDouble(cleanString);
                        edHargaTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(iv.parseNullDouble(selectedIsiSatuan) * hargaDouble * iv.parseNullDouble(jumlah))));
                    }

                    edHargaWithDiskon.addTextChangedListener(this);
                }
            }
        });

        if(updateOrderFlag){ // if update order

            Bundle extras = getIntent().getExtras();
            if(extras != null){
                hargaPcs = extras.getString("hargapcs");
                edHarga.setText(iv.ChangeToCurrencyFormat(extras.getString("harga")));
            }
        }
        //endregion

        //region 6. Pengaturan Diskon
        edDiskon.setFilters(new InputFilter[] { filter });
        edDiskon.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                statusChangeHarga = 1;
                CalculateHargaByDiskon();
            }
        });
        //endregion
    }

    // Perhitungan Harga dengan diskon, diskon bertingkat
    private void CalculateHargaByDiskon(){

        if(flagHarga.trim().equals("2") && edJumlah.getText().length() > 0){ // diskon
            String changedString = edDiskon.getText().toString().replaceAll("[+]", ",");

            // Calculate harga netto * total Diskon
            List<String> diskonList = new ArrayList<String>(Arrays.asList(changedString.split(",")));
            List<Double> diskonListDouble = new ArrayList<Double>();
            for (String diskon: diskonList){

                try {
                    Double x = iv.parseNullDouble(diskon);
                    diskonListDouble.add(x);
                }catch (Exception e){
                    Double df = Double.valueOf(0);
                    diskonListDouble.add(df);
                    e.printStackTrace();
                }
            }

            if(diskonListDouble.size()>0 && edHarga.getText().length() > 0){

                //long hargaAwal = iv.parseNullLong(edHarga.getText().toString().replace(".", "").replace(",", "")) / iv.parseNullInteger(selectedIsiSatuan);
                String hargaClean = edHarga.getText().toString().replaceAll("[,.]", "");
                double hargaAwal = iv.parseNullDouble(hargaClean) / iv.parseNullDouble(selectedIsiSatuan);
                //long newHargaNetto = 0;
                double newHargaNetto = 0;
                Integer index = 1;

                if(diskonListDouble.size() > 0){
                    for(Double i: diskonListDouble){
                        if(index == 1){
                            //double minDiskon = i / 100 * hargaAwal;
                            newHargaNetto = hargaAwal - (i / 100 * hargaAwal);
                        }else{
                            newHargaNetto = newHargaNetto - ( i / 100 * newHargaNetto);
                        }
                        index++;
                    }
                }else{
                    newHargaNetto = hargaAwal;
                }


                if(statusChangeHarga != 2){

                    edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(selectedIsiSatuan))));
                    edHargaTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(selectedIsiSatuan) * iv.parseNullDouble(jumlah))));
                }else{

                    edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(selectedIsiSatuan))));
                    edHargaTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(selectedIsiSatuan) * iv.parseNullDouble(jumlah))));

                }

            }else{
                // clear field
                edHargaWithDiskon.setText("0");
                edHargaTotal.setText("0");
            }
        }
    }

    public void getClickCreateSOEvent() {

        /*
        * validation before save data
        */

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //region validate before saving
                // date
                formatDate = getResources().getString(R.string.format_date);
                formatDateDisplay = getResources().getString(R.string.format_date_display);
                tanggal = iv.ChangeFormatDateString(edTanggal.getText().toString(), formatDateDisplay, formatDate);
                tanggalTempo = iv.ChangeFormatDateString(edTanggalTempo.getText().toString(), formatDateDisplay, formatDate);

                if(tanggalTempo.length() <= 0){
                    Toast.makeText(OrderDetail.this, "Tanggal tempo harap diisi",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!iv.validateCustomDate(edTanggalTempo,formatDateDisplay)){
                    Toast.makeText(OrderDetail.this, "Format Tanggal Tempo tidak benar",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!iv.isMoreThanCurrentDate(edTanggalTempo,edTanggal,formatDateDisplay)){
                    Toast.makeText(OrderDetail.this, "Tanggal Tempo harus lebih dari tanggal order",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Jumlah
                if((edJumlah.getText().length() <= 0) /*|| ((Long.parseLong(edJumlah.getText().toString()) * Integer.parseInt(selectedIsiSatuan)) < minStok)*/){
                    edJumlah.setError("Jumlah harus lebih dari 0");
                    edJumlah.requestFocus();
                    return;
                }else{
                    jumlah = edJumlah.getText().toString();
                }

                // Status flag
                if(statusSO == 3 || statusSO == 7 || statusSO == 9){
                    String peringatan = "Tidak Dapat Mengubah Order";
                    switch (statusSO){
                        case 3:
                            peringatan = "Tidak dapat mengubah Order yang telah disetujui";
                            break;
                        case 7:
                            peringatan = "Tidak dapat mengubah Order yang telah di Posting";
                            break;
                        case 9:
                            peringatan = "Tidak dapat mengubah Order yang ditolak";
                            break;
                    }
                    Toast.makeText(OrderDetail.this, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                jumlah = edJumlah.getText().toString();
                int selectedUnitID = rbgUnitQuantity.getCheckedRadioButtonId();
                RadioButton selectedRadio = (RadioButton) findViewById(selectedUnitID);
                selectedSatuan = selectedRadio.getText().toString();

                AlertDialog builder = new AlertDialog.Builder(OrderDetail.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin memproses Order ini?")
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    getGudangBySatuan(selectedSatuan);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();

            }
        });
    }

    private InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i)) && (source.charAt(i) != '+') && (source.charAt(i) != '.')) {
                    return "";
                }
            }
            return null;
        }

    };

    private void getHargaBarang() throws JSONException {

        /*
        * get Harga & total
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.VISIBLE);
            }
        });

        String jumlahTotal = String.valueOf(Integer.parseInt(jumlah) * Integer.parseInt(selectedIsiSatuan)) ;
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("kode_brg", kodeBarang);
        jsonBody.put("qty", jumlahTotal);
        jsonBody.put("flag", "2"/*flagHarga*/);

        ApiVolley hargaBarangDetail = new ApiVolley(getApplicationContext(), jsonBody, "POST", urlGetHarga, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String message = responseAPI.getJSONObject("metadata").getString("message");
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(Integer.parseInt(status) == 200){

                        harga = responseAPI.getJSONObject("response").getString("harga");
                        hargaPcs = responseAPI.getJSONObject("response").getString("harga");
                        crBayar = responseAPI.getJSONObject("response").getString("crbayar");
                        String diskonString = "";
                        if(responseAPI.getJSONObject("response").getString("diskon").length() > 0){
                            diskonString = responseAPI.getJSONObject("response").getString("diskon");
                        }

                        diskon = (diskonString.equals("0") ? "" : diskonString);
                        lastDiskon = diskonString;
                        hargaNetto = responseAPI.getJSONObject("response").getString("harganetto");
                        approveLevel = responseAPI.getJSONObject("response").getString("approve");

                        // Enable or disable diskon
                        if(!approveLevel.trim().equals("0") && flagHarga.trim().equals("2")){
                            edDiskon.setFocusable(true);
                            edDiskon.setKeyListener(new EditText(OrderDetail.this).getKeyListener());
                            edDiskon.setFocusableInTouchMode(true);

                            edHargaWithDiskon.setFocusable(true);
                            edHargaWithDiskon.setKeyListener(new DigitsKeyListener(true, true));
                            edHargaWithDiskon.setFocusableInTouchMode(true);
                        }else{
                            edDiskon.setKeyListener(null);
                            edHargaWithDiskon.setKeyListener(null);
                        }

                        String hargaText = (iv.doubleToString(iv.parseNullDouble(harga) * iv.parseNullDouble(selectedIsiSatuan)));
                        edHarga.setText(iv.ChangeToCurrencyFormat(hargaText));
                        edDiskon.setText(diskon);

                        CalculateHargaByDiskon();
                    }else{
                        edHarga.setText("0");
                        edDiskon.setText("");
                        edHargaWithDiskon.setText("0");
                        edHargaTotal.setText("0");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbLoading.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbLoading.setVisibility(View.GONE);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String result) {
                edHarga.setText("0");
                edDiskon.setText("");
                edHargaWithDiskon.setText("0");
                edHargaTotal.setText("0");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void ClearHargaField(){

        /*
        * Clear field(s) Harga & another depedencies field
        */

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                edHarga.setText("0");
                edDiskon.setText("");
                edHargaWithDiskon.setText("0");
                edHargaTotal.setText("0");
            }
        });
    }

    private void getGudangBySatuan(String satuan) throws JSONException {

        /*
        * get Gudang by input of satuan
        */

        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("kode_brg", kodeBarang);
        jsonBody.put("satuan", satuan);

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jsonBody, "POST", urlGetGudang, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(Integer.parseInt(status) == 200){

                        selectedGudang = responseAPI.getJSONObject("response").getString("gudang");

                        // Save data
                        try {
                            doInsertSalesOrder();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
            }
        });
    }

    private void getTanggalTempo(){

        /*
         * get Gudang by input of satuan
         */

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("kodebrg", kodeBarang);
            jsonBody.put("kdcus", kdCus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jsonBody, "POST", ServerURL.getTanggalTempo, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    if(Integer.parseInt(status) == 200){

                        JSONObject jo = responseAPI.getJSONObject("response");
                        tempo = jo.getString("tempo");

                        tanggalTempo = iv.sumDate(tanggal,iv.parseNullInteger(tempo),formatDate);
                        tvTempo.setText(tempo + " Hari");
                        edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));
                        edTanggalTempo.setKeyListener(null);

                        //iv.datePickerEventMax(OrderDetail.this,edTanggalTempo,"RIGHT",formatDateDisplay, iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay), iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));

                    }else{
                        
                        Toast.makeText(OrderDetail.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
            }
        });
    }

    private void doInsertSalesOrder() throws JSONException {

        /*
        * Insert to API
        * depend of Sales Order, if exist: add new order, if not: insert new Sales Order
        */

        final JSONObject jsonBody = new JSONObject();

        String method = "POST";
        //String hargaInput = edHarga.getText().toString().replace(",", "").replace(".", "");
        String hargaInput = edHarga.getText().toString().replaceAll("[,.]", "");

        jumlahpcs = String.valueOf(Long.parseLong(edJumlah.getText().toString()) * Integer.parseInt(selectedIsiSatuan));
        hargaToSave = hargaInput;

        if(updateOrderFlag){ // update Order

            method = "PUT";
            urlSaveSO = ServerURL.updateDetailSO + idOrderDetail;
            jsonBody.put("jumlah", jumlah);
            jsonBody.put("satuan", selectedSatuan);
            jsonBody.put("gudang", selectedGudang);
            jsonBody.put("harga", hargaToSave);
            jsonBody.put("jumlahpcs", jumlahpcs); // jumlah pcs
            jsonBody.put("hargapcs", hargaPcs); // harga asli
            jsonBody.put("total", edHargaTotal.getText().toString().replaceAll("[,.]", "")); // harga * jumlahpcs
            jsonBody.put("diskon", edDiskon.getText().toString()); // default 0
            jsonBody.put("hargadiskon", edHargaWithDiskon.getText().toString().replaceAll("[,.]", "")); // default 0
            jsonBody.put("keterangan", edtKetarangan.getText().toString());
            String ppn = "N";
            String jenisPpn = "";
            if(rbPPN.isChecked()){
                ppn = "T";

                if(rbEFaktur.isChecked()){
                    jenisPpn = "E";
                }else if(rbCast.isChecked()){
                    jenisPpn = "C";
                }else if(rbOperan.isChecked()){
                    jenisPpn = "O";
                }
            }

            jsonBody.put("ppn", ppn);
            jsonBody.put("jenis_ppn", jenisPpn);

        }else{

            if(noSalesOrder != null && !noSalesOrder.trim().toLowerCase().equals("null") && noSalesOrder.length() > 0){ // add another Order
                jsonBody.put("nobukti", noSalesOrder);
            }else{ // add new Order
                jsonBody.put("nobukti", "");
                jsonBody.put("kdcus", kdCus);
                jsonBody.put("tgl", tanggal);
                jsonBody.put("tgltempo", tanggalTempo);
            }

            jsonBody.put("kdbrg", kodeBarang);
            jsonBody.put("jumlah", jumlah);
            jsonBody.put("gudang", selectedGudang);
            jsonBody.put("satuan", selectedSatuan);
            jsonBody.put("harga", hargaToSave);
            jsonBody.put("jumlahpcs", jumlahpcs); // jumlah pcs
            jsonBody.put("hargapcs", hargaPcs); // harga asli
            jsonBody.put("total", edHargaTotal.getText().toString().replaceAll("[,.]", "")); // harga * jumlahpcs
            jsonBody.put("diskon", edDiskon.getText().toString()); // default 0
            jsonBody.put("hargadiskon", edHargaWithDiskon.getText().toString().replaceAll("[,.]", "")); // default 0

            jsonBody.put("keterangan", edtKetarangan.getText().toString());
            String ppn = "N";
            String jenisPpn = "";
            if(rbPPN.isChecked()){
                ppn = "T";

                if(rbEFaktur.isChecked()){
                    jenisPpn = "E";
                }else if(rbCast.isChecked()){
                    jenisPpn = "C";
                }else if(rbOperan.isChecked()){
                    jenisPpn = "O";
                }
            }

            jsonBody.put("ppn", ppn);
            jsonBody.put("jenis_ppn", jenisPpn);
            jsonBody.put("tempo", tempo);
            jsonBody.put("id_tempo", idTempo);
        }

        ApiVolley insertData = new ApiVolley(getApplicationContext(), jsonBody, method, urlSaveSO, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String message = responseAPI.getJSONObject("metadata").getString("message");
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show(); // show message response

                    if(Integer.parseInt(status) == 201 || Integer.parseInt(status) == 200){ // Success insert SO

                        try {

                            JSONObject jsonHeader = responseAPI.getJSONObject("response").getJSONObject("so_header");

                            // Redirect to Detail SO

                            Intent intent = new Intent(getApplicationContext(), DetailSalesOrder.class);

                            if(noSalesOrder != null && !noSalesOrder.trim().toLowerCase().equals("null") && noSalesOrder.length() > 0){
                                intent.putExtra("nosalesorder",noSalesOrder);
                            }else{
                                String noSO = jsonHeader.getString("nobukti");
                                intent.putExtra("nosalesorder",noSO);

                                new LocationUpdateHandler(OrderDetail.this,"Tambah Order "+ noSO);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear all top activity
                            finish();
                            startActivity(intent);
                        } catch (JSONException e){

                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}