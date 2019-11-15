package gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class DetailOrderEntryCanvas extends AppCompatActivity {

    private String kdCus, namaPelanggan, stok, kodeBarang, namaBarang, noBukti = "", hargaPcs;
    private String approveLevel = ""; // 1. Keuangan, 2. Bos
    private String tanggal, jumlah, diskon, lastDiskon, isiSatuan1, isiSatuan2, selectedIsiSatuan, jumlahpcs;
    private ItemValidation iv = new ItemValidation();
    private EditText edNoSO, edNamaPelanggan, edNamaBarang, edTanggal, edJumlah, edHarga, edDiskon, edHargaWithDiskon, edHargaTotal;
    private RadioButton rbQuantity2, rbQuantity3;
    private final String TAG = "Order.Detail";
    private TextInputLayout tilNoSO, tilJumlah, tilDiskon, tilHargaTotal;
    private String urlListBarangByID;
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
    private String formatDate = "", formatDateDisplay;
    private ProgressBar pbLoading;
    private int statusChangeHarga = 1;
    private boolean afterChangeFlag = false;
    private boolean isLoadingHarga = false;
    private String noKonsinyasi = "";
    private List<CustomListItem> selectedBarangList;
    private String urlGenerateNobukti = "";
    private TextInputLayout tilTanggalTempo;
    private EditText edTanggalTempo;
    private String tanggalTempo;
    private String satuanAsli = "";
    private EditText edtLimitOrder;
    private String currentString = "", currentString1 = "";
    private String limitOrder = "";
    private TextView tvTempo;
    private String tempo = "", idTempo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_entry_canvas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Detail Penjualan Canvas");
        initUI();
    }

    private void initUI() {

        /*
        * Define all your activity here
        */

        // API
        urlListBarangByID = ServerURL.getListBarangById;
        urlGetHarga = ServerURL.getHargaSODetail;
        urlGetGudang = ServerURL.getGudangSODetailByID;
        urlGenerateNobukti = getResources().getString(R.string.url_generate_no_gc_v2);
        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);

        Bundle extras = getIntent().getExtras();
        if (extras != null){

            idOrderDetail = extras.getString("idorder");
            noBukti = extras.getString("nobukti");
            kdCus = extras.getString("kdcus");
            namaPelanggan = extras.getString("nama");
            jumlah = extras.getString("jumlah");
            stok = extras.getString("stok");
            tempo = extras.getString("tempo", "30");
            idTempo = extras.getString("idTempo", "");

            selectedSatuan = extras.getString("satuan");
            satuanAsli = extras.getString("satuan");
            kodeBarang = extras.getString("kodebarang");
            namaBarang = extras.getString("namabarang");
            noKonsinyasi = extras.getString("nokonsinyasi");

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
            edJumlah.setError("Sisa stok "+ stok);
            rbgUnitQuantity = (RadioGroup) findViewById(R.id.rbg_unit);
            rbQuantity2 = (RadioButton) findViewById(R.id.rb_quantity_2);
            rbQuantity3 = (RadioButton) findViewById(R.id.rb_quantity_3);
            edHarga = (EditText) findViewById(R.id.edt_harga);
            pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
            tilDiskon = (TextInputLayout) findViewById(R.id.til_diskon);
            edDiskon = (EditText) findViewById(R.id.edt_diskon);
            edHargaWithDiskon = (EditText) findViewById(R.id.edt_harga_with_dicsount);
            tilHargaTotal = (TextInputLayout) findViewById(R.id.til_harga_total);
            edHargaTotal = (EditText) findViewById(R.id.edt_harga_total);
            llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
            tvSave = (TextView) findViewById(R.id.tv_save);
            edtLimitOrder = (EditText) findViewById(R.id.edt_limit_order);
            tvTempo = (TextView) findViewById(R.id.tv_tempo);

            edNamaPelanggan.setText(namaPelanggan);
            edNamaBarang.setText(namaBarang);

            /*edHargaWithDiskon.addTextChangedListener(iv.textChangeListenerCurrency(edHargaWithDiskon));
            edHargaTotal.addTextChangedListener(iv.textChangeListenerCurrency(edHargaTotal));*/
            edTanggal.setText(iv.getToday(formatDateDisplay));
            tanggalTempo = iv.sumDate(iv.getToday(formatDate),iv.parseNullInteger(tempo),formatDate);
            edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo, formatDate, formatDateDisplay));
            edTanggalTempo.setKeyListener(null);
            //iv.datePickerEventMax(DetailOrderEntryCanvas.this,edTanggalTempo,"RIGHT",formatDateDisplay, iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay), iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));

            tvTempo.setText(tempo + " Hari");

            if(noBukti != null && !noBukti.toLowerCase().equals("null") && noBukti.length() > 0){

                // Ambil Barang yang telah dipilih
                String selectedBarangString  = extras.getString("selectedbarang");
                selectedBarangList = new ArrayList<>();
                Type tipeBarangList = new TypeToken<List<CustomListItem>>(){}.getType();
                Gson gson = new Gson();
                selectedBarangList = gson.fromJson(selectedBarangString, tipeBarangList);

                if(selectedBarangList != null && selectedBarangList.size() > 0){

                    long currentSisa = 0;
                    for(CustomListItem item : selectedBarangList){

                        if(kodeBarang.equals(item.getListItem1())) currentSisa += iv.parseNullLong(item.getListItem3());
                    }

                    /*stok = String.valueOf(iv.parseNullLong(stok) - currentSisa);
                    edJumlah.setError("Sisa stok "+ stok);*/
                }

                if(idOrderDetail != null && !idOrderDetail.toLowerCase().equals("null") && idOrderDetail.length() > 0){

                    tvSave.setText("Ubah Penjualan Canvas");
                    initEventAddOrder();
                    updateOrderFlag = true;
                }else{
                    tvSave.setText("Tambah Barang Canvas");
                    initEventAddOrder();
                }
                rbQuantity2.setChecked(true);
            }else{

                tvSave.setText("Buat Penjualan Canvas");
                llNoSO.setVisibility(View.GONE);
                initEventNewSO();
                rbQuantity2.setChecked(true);
            }

            getLimitOrder();
        }else{
            finish();
        }
    }

    private void getLimitOrder() {

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", ServerURL.getLimitOrder + kdCus, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            limitOrder = "0";
                            long total = 0;
                            String sisaLimit = "0";

                            if(status.equals("200")){

                                sisaLimit = responseAPI.getJSONObject("response").getString("sisa_limit");
                                if(selectedBarangList != null && selectedBarangList.size() > 0){

                                    for(CustomListItem item : selectedBarangList){

                                        total += iv.parseNullLong(item.getListItem12());
                                    }
                                }
                            }

                            limitOrder = String.valueOf(iv.parseNullLong(sisaLimit) - total);
                            edtLimitOrder.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(limitOrder)));

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }

    private void initEventAddOrder(){

        // add new order from last SO
        llNoSO.setVisibility(View.VISIBLE);
        edNoSO.setText(noBukti);

        getDetailBarang();
        getClickCreateSOEvent();
    }

    private void initEventNewSO() {

        // add New Sales Order
        edTanggal.setText(iv.getToday(formatDateDisplay));
        getDetailBarang();
        getClickCreateSOEvent();
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

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

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
        rbQuantity3.setVisibility(View.GONE);
        if(updateOrderFlag){ // if update order

            edJumlah.setText(jumlah);
            if(selectedSatuan.toUpperCase().equals(satuan3.toUpperCase())){ //Satuan kecil
                selectedIsiSatuan = isiSatuan1;
                rbQuantity2.setChecked(true);

            }else{ // if add order
                selectedIsiSatuan = isiSatuan2;
                rbQuantity2.setChecked(true);
            }

            Bundle extras = getIntent().getExtras();
            if(extras != null){

                hargaPcs = extras.getString("hargapcs");

                if(flagHarga.trim().equals("2")){ //diskon
                    edHarga.setText(iv.ChangeToCurrencyFormat(extras.getString("harga")));
                    edDiskon.setText(extras.getString("diskon"));

                    edDiskon.setFocusable(true);
                    edDiskon.setKeyListener(new EditText(DetailOrderEntryCanvas.this).getKeyListener());
                    edDiskon.setFocusableInTouchMode(true);

                    edHargaWithDiskon.setFocusable(true);
                    edHargaWithDiskon.setKeyListener(new DigitsKeyListener(true,true));
                    edHargaWithDiskon.setFocusableInTouchMode(true);

                    if(iv.parseNullDouble(extras.getString("harganetto")) > 0){ // Custom Harga netto
                        statusChangeHarga = 2;
                        afterChangeFlag = true;
                        edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(extras.getString("harganetto")));
                        //Log.d(TAG, "prevalidateelement harganetto > 0: "+edHargaWithDiskon.getText().toString());
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

            if(selectedSatuan.equals("")) selectedSatuan = satuan3;
            selectedIsiSatuan = isiSatuan1;
            selectedGudang = gudangKecil;

            rbQuantity2.setChecked(true);
        }

        if(selectedSatuan.toUpperCase().equals(satuan2.toUpperCase())){
            selectedIsiSatuan = isiSatuan2;
            //rbQuantity2.setText(satuan2);
            rbQuantity2.setText(satuanAsli);
        }else{
            selectedIsiSatuan = isiSatuan1;
            //rbQuantity2.setText(satuan3);
            rbQuantity2.setText(satuanAsli);
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
                        double hargaDouble = iv.parseNullDouble(cleanString);
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

    private void CalculateHargaByDiskon(){

        //dummy bos

        if(flagHarga.trim().equals("2") && edJumlah.getText().length() > 0){ // diskon
            String changedString = edDiskon.getText().toString().replaceAll("[+]", ",");

            // Calculate harga netto * total Diskon
            List<String> diskonList = new ArrayList<String>(Arrays.asList(changedString.split(",")));
            List<Double> diskonListDouble = new ArrayList<Double>();
            for (String diskon1: diskonList){

                try {
                    Double x = iv.parseNullDouble(diskon1);
                    diskonListDouble.add(x);
                }catch (Exception e){
                    Double df = Double.valueOf(0);
                    diskonListDouble.add(df);
                    e.printStackTrace();
                }
            }

            if(diskonListDouble.size()>0 && edHarga.getText().length() > 0){

                double hargaAwal = iv.parseNullDouble(edHarga.getText().toString().replaceAll("[,.]", "")) / iv.parseNullDouble(selectedIsiSatuan);
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

                    /*if(Double.parseDouble(edHargaWithDiskon.getText().toString().replaceAll("[,.]", "")) > 0){
                        newHargaNetto = Double.parseDouble(edHargaWithDiskon.getText().toString().replaceAll("[,.]", ""));
                    }*/

                    edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(selectedIsiSatuan))));
                    edHargaTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(selectedIsiSatuan) * iv.parseNullDouble(jumlah))));

                    //edHargaTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(newHargaNetto * iv.parseNullDouble(jumlah))));
                }
                //Log.d(TAG, "CalculateHargaByDiskon: "+ String.valueOf(newHargaNetto * iv.parseNullInteger(selectedIsiSatuan) * iv.parseNullInteger(jumlah)));
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

                tanggal = iv.ChangeFormatDateString(edTanggal.getText().toString(), formatDateDisplay, formatDate);
                tanggalTempo = iv.ChangeFormatDateString(edTanggalTempo.getText().toString(), formatDateDisplay, formatDate);

                if(tanggalTempo.length() <= 0){
                    Toast.makeText(DetailOrderEntryCanvas.this, "Tanggal tempo harap diisi",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!iv.validateCustomDate(edTanggalTempo,formatDateDisplay)){
                    Toast.makeText(DetailOrderEntryCanvas.this, "Format Tanggal Tempo tidak benar",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!iv.isMoreThanCurrentDate(edTanggalTempo,edTanggal,formatDateDisplay)){
                    //tilTanggalTempo.setErrorEnabled(true);
                    //tilTanggalTempo.setError("Tanggal Tempo Tidak Dapat Sebelum Tanggal Order");
                    Toast.makeText(DetailOrderEntryCanvas.this, "Tanggal Tempo harus lebih dari tanggal order",Toast.LENGTH_SHORT).show();
                    //edTanggalTempo.requestFocus();
                    return;
                }

                // Jumlah
                if((edJumlah.getText().length() <= 0) /*|| ((Long.parseLong(edJumlah.getText().toString()) * Integer.parseInt(selectedIsiSatuan)) < minStok)*/){
                    edJumlah.setError("Jumlah tidak boleh kosong");
                    edJumlah.requestFocus();
                    return;
                }else{
                    jumlah = edJumlah.getText().toString();
                }

                if(iv.parseNullDouble(edHargaTotal.getText().toString().replaceAll("[,.]", "")) > iv.parseNullDouble(limitOrder)){

                    Toast.makeText(DetailOrderEntryCanvas.this, "Total melebihi limit order, tidak dapat memproses order", Toast.LENGTH_LONG).show();
                    return;
                }

                if(iv.parseNullLong(edJumlah.getText().toString()) > iv.parseNullLong(stok)){
                    edJumlah.setError("Jumlah tidak boleh lebih dari " + iv.parseNullLong(stok));
                    edJumlah.requestFocus();
                    return;
                }

                if(isLoadingHarga){
                    Toast.makeText(DetailOrderEntryCanvas.this, "Tunggu hingga proses muat harga selesai", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(iv.parseNullDouble(edHargaTotal.getText().toString().replaceAll("[,.]", "")) < 0){

                }

                jumlah = edJumlah.getText().toString();
                /*int selectedUnitID = rbgUnitQuantity.getCheckedRadioButtonId();
                RadioButton selectedRadio = (RadioButton) findViewById(selectedUnitID);
                selectedSatuan = selectedRadio.getText().toString();*/

                try {
                    getGudangBySatuan(selectedSatuan);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i)) && (source.charAt(i) != '+')&& (source.charAt(i) != '.')) { // Accept only letter & digits ; otherwise just return
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
        isLoadingHarga = true;
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
                            edDiskon.setKeyListener(new EditText(DetailOrderEntryCanvas.this).getKeyListener());
                            edDiskon.setFocusableInTouchMode(true);

                            edHargaWithDiskon.setFocusable(true);
                            edHargaWithDiskon.setKeyListener(new DigitsKeyListener(true, true));
                            edHargaWithDiskon.setFocusableInTouchMode(true);
                        }else{
                            edDiskon.setKeyListener(null);
                            edHargaWithDiskon.setKeyListener(null);
                        }

                        /*double jumlahHarga = 0;

                        // Baik Pricelist(1) atau discount(2)
                        if(iv.parseNullDouble(hargaNetto) <= 0 ){
                            jumlahHarga = iv.parseNullDouble(harga) * iv.parseNullDouble(jumlah);
                        }else{
                            jumlahHarga = iv.parseNullDouble(hargaNetto) * iv.parseNullDouble(jumlah);
                        }

                        if(iv.parseNullDouble(hargaNetto) <= 0 && iv.parseNullInteger(flagHarga) == 2){ // if diskon with flag 1 or 2
                            hargaNetto = (iv.doubleToString(iv.parseNullDouble(harga) * iv.parseNullDouble(selectedIsiSatuan)));
                        }else{
                            hargaNetto = (iv.doubleToString(iv.parseNullDouble(hargaNetto) * iv.parseNullDouble(selectedIsiSatuan)));
                        }
                        String hargaText = (iv.doubleToString(iv.parseNullDouble(harga) * iv.parseNullDouble(selectedIsiSatuan)));
                        String totalHarga = iv.doubleToString(jumlahHarga * iv.parseNullDouble(selectedIsiSatuan));

                        edHarga.setText(iv.ChangeToCurrencyFormat(hargaText));

                        //hargaToSave = edHarga.getText().toString().replace(",", "").replace(".", "");
                        hargaToSave = edHarga.getText().toString().replaceAll("[,.]", "");
                        edDiskon.setText(diskon);
                        edHargaWithDiskon.setText(iv.ChangeToCurrencyFormat(hargaNetto));
                        //Log.d(TAG, "getHargaBarang: "+edHargaWithDiskon.getText().toString());
                        edHargaTotal.setText(iv.ChangeToCurrencyFormat(totalHarga));*/

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
                    isLoadingHarga = false;

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbLoading.setVisibility(View.GONE);
                        }
                    });
                    e.printStackTrace();
                    isLoadingHarga = false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbLoading.setVisibility(View.GONE);
                    }
                });

                isLoadingHarga = false;
            }

            @Override
            public void onError(String result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
                isLoadingHarga = false;

                edHarga.setText("0");
                edDiskon.setText("");
                edHargaWithDiskon.setText("0");
                edHargaTotal.setText("0");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
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

    private void doInsertSalesOrder() throws JSONException {

        /*
        * Insert to API
        * depend of Sales Order, if exist: add new order, if not: insert new Sales Order
        */

        //final JSONObject jsonBody = new JSONObject();

        String method = "POST";
        String hargaInput = edHarga.getText().toString().replaceAll("[,.]", "");

        jumlahpcs = String.valueOf(Long.parseLong(edJumlah.getText().toString()) * Integer.parseInt(selectedIsiSatuan));
        hargaToSave = hargaInput;

        //Get no bukti

        CustomListItem itemBarang = new CustomListItem(
                kodeBarang,         // 1. kdbrg
                namaBarang,         // 2. nama Barang
                jumlah,             // 3. jumlah
                //selectedSatuan,     // 4. satuan
                satuanAsli,     // 4. satuan
                selectedGudang,     // 5. gudang
                hargaToSave,        // 6. Harga
                jumlahpcs,          // 7. jumlah pcs
                noKonsinyasi,       // 8. no konsinyasi
                hargaPcs,           // 9. hargapcs
                edDiskon.getText().toString(),           // 10. diskon: default
                edHargaWithDiskon.getText().toString().replaceAll("[,.]", ""),  // 11. harganetto: default 0
                edHargaTotal.getText().toString().replaceAll("[,.]", ""),       // 12. total harga * jumlahpcs
                String.valueOf(iv.parseNullInteger(stok)/* - iv.parseNullInteger(jumlah)*/)       // 13. sisa stok
        );

        Gson gson = new Gson();
        final String barangToAdd = gson.toJson(itemBarang).toString();

        final Intent intent = new Intent(DetailOrderEntryCanvas.this, DetailCheckOutCanvas.class);

        if(updateOrderFlag){ // update Order

            intent.putExtra("nobukti", noBukti);
            int index = 0;
            for(CustomListItem item: selectedBarangList){

                if(item.getListItem1().equals(kodeBarang)) break;
                index++;
            }

            //selectedBarangList.remove(index);
            intent.putExtra("updatestatus", String.valueOf(index));
            intent.putExtra("selectedbarang", gson.toJson(selectedBarangList));
            intent.putExtra("barang", barangToAdd);
            intent.putExtra("kdcus", kdCus);
            intent.putExtra("nama", namaPelanggan);
            intent.putExtra("tanggal", tanggal);
            intent.putExtra("tanggaltempo", tanggalTempo);
            intent.putExtra("tempo", tempo);
            intent.putExtra("idTempo", idTempo);
            startActivity(intent);
            Toast.makeText(DetailOrderEntryCanvas.this, "Data berhasil ditambahkan", Toast.LENGTH_LONG).show();
            finish();

        }else{

            if(noBukti != null && !noBukti.trim().toLowerCase().equals("null") && noBukti.length() > 0){ // add another Order

                boolean statusDuplicate = false;
                for(CustomListItem item: selectedBarangList){

                    if(item.getListItem1().equals(kodeBarang)){
                        Toast.makeText(DetailOrderEntryCanvas.this, "Barang tersebut telah masuk pada daftar order", Toast.LENGTH_SHORT).show();
                        statusDuplicate = true;
                        break;

                    }
                }

                if(statusDuplicate) return;

                intent.putExtra("nobukti", noBukti);
                intent.putExtra("selectedbarang", gson.toJson(selectedBarangList).toString());
                intent.putExtra("barang", barangToAdd);
                intent.putExtra("kdcus", kdCus);
                intent.putExtra("nama", namaPelanggan);
                intent.putExtra("tanggal", tanggal);
                intent.putExtra("tanggaltempo", tanggalTempo);
                intent.putExtra("tempo", tempo);
                intent.putExtra("idTempo", idTempo);
                startActivity(intent);
                Toast.makeText(DetailOrderEntryCanvas.this, "Data berhasil ditambahkan", Toast.LENGTH_LONG).show();
                finish();

            }else{ // add new Order


                ApiVolley apiVolley = new ApiVolley(DetailOrderEntryCanvas.this, new JSONObject(), "GET", urlGenerateNobukti, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");


                            if(Integer.parseInt(status) == 200){

                                String noBukti = responseAPI.getJSONObject("response").getString("nobukti");
                                intent.putExtra("nobukti", noBukti);
                                intent.putExtra("barang", barangToAdd);
                                intent.putExtra("kdcus", kdCus);
                                intent.putExtra("nama", namaPelanggan);
                                intent.putExtra("tanggal", tanggal);
                                intent.putExtra("tanggaltempo", tanggalTempo);
                                intent.putExtra("tempo", tempo);
                                intent.putExtra("idTempo", idTempo);
                                startActivity(intent);
                                Toast.makeText(DetailOrderEntryCanvas.this, "Data berhasil ditambahkan", Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(DetailOrderEntryCanvas.this, result, Toast.LENGTH_LONG).show(); // show message response
                    }
                });
            }
        }
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
