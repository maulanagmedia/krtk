package gmedia.net.id.kartikaelektrik.activityOrderCustom;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
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

import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomerOrder.OrderDetail;
import gmedia.net.id.kartikaelektrik.activityEntryPaket.OrderDetailPaket;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;

public class OrderCustomDetail extends AppCompatActivity {

    private LinearLayout llNoSO, llSaveContainer;
    private TextInputLayout tilNoSO, tilTanggalTempo, tilKeterangan, tilJumlah, tilSatuan, tilDiskon, tilHargaTotal, tilUkuran;
    private EditText edNoSO, edNamaPelanggan, edNamaBarang, edTanggal, edTanggalTempo, edKeterangan, edJumlah, edSatuan, edHarga, edDiskon, edHargaWithDiskon, edHargaTotal;
    private TextView tvSave;
    private String kdCus = "", namaCus = "";
    private ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";
    private String tanggal = "", tanggalTempo = "";
    private String jumlah = "";
    private boolean afterCalculate = false;
    private int statusChangeHarga = 1;
    private String noBukti = "", keterangan = "",satuan = "" , harga = "", diskon = "", hargadiskon = "", total = "";
    private String urlSaveOC = "", urlSaveOCDeteil = "", urlGetHarga = "";
    private String statusSO = "1"; // Default baru
    private boolean updateMode = false;
    private String idOrder = "", method = "POST";
    private String kdBrg = "", namaBrg = "";
    private EditText edUkuran;
    private EditText edTotalJumlah;
    private TextView tvSatuan1, tvSatuan2;
    private ProgressBar pbLoading;
    private boolean isLoading = false;
    private String satuanBarang = "", satuanJumlah = "", flag = "", crBayar = "", ukuran = "";
    private String satuanKabel = "ROLL", satuanLampu = "PCS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_custom_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Detail Order Custom");

        urlSaveOC = getResources().getString(R.string.url_save_custom_order);
        urlSaveOCDeteil = getResources().getString(R.string.url_save_custom_detail);
        urlGetHarga = getResources().getString(R.string.url_get_harga_so_detail);

        llNoSO = (LinearLayout) findViewById(R.id.ll_no_so_container);
        tilNoSO = (TextInputLayout) findViewById(R.id.til_no_so);
        edNoSO = (EditText) findViewById(R.id.edt_no_so);
        edNamaPelanggan = (EditText) findViewById(R.id.edt_nama_pelanggan);
        edNamaBarang = (EditText) findViewById(R.id.edt_nama_barang);
        edTanggal = (EditText) findViewById(R.id.edt_tanggal);
        tilTanggalTempo = (TextInputLayout) findViewById(R.id.til_tanggal_tempo);
        edTanggalTempo = (EditText) findViewById(R.id.edt_tanggal_tempo);
        tilUkuran = (TextInputLayout) findViewById(R.id.til_ukuran);
        edUkuran = (EditText) findViewById(R.id.edt_ukuran);
        tilJumlah = (TextInputLayout) findViewById(R.id.til_jumlah);
        edJumlah = (EditText) findViewById(R.id.edt_jumlah);
        edTotalJumlah = (EditText) findViewById(R.id.edt_total_jumlah);
        tvSatuan1 = (TextView) findViewById(R.id.tv_satuan1);
        tvSatuan2 = (TextView) findViewById(R.id.tv_satuan2);
        tilKeterangan = (TextInputLayout) findViewById(R.id.til_keterangan);
        edKeterangan = (EditText) findViewById(R.id.edt_keterangan);
        tilSatuan = (TextInputLayout) findViewById(R.id.til_satuan);
        edSatuan = (EditText) findViewById(R.id.edt_satuan);
        edHarga = (EditText) findViewById(R.id.edt_harga);
        tilDiskon = (TextInputLayout) findViewById(R.id.til_diskon);
        edDiskon = (EditText) findViewById(R.id.edt_diskon);
        edHargaWithDiskon = (EditText) findViewById(R.id.edt_harga_with_dicsount);
        tilHargaTotal = (TextInputLayout) findViewById(R.id.til_harga_total);
        edHargaTotal = (EditText) findViewById(R.id.edt_harga_total);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);

        Bundle bundle = getIntent().getExtras();

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);

        if(bundle != null){

            noBukti = (bundle.getString("nobukti") != null && bundle.getString("nobukti") != "") ? bundle.getString("nobukti") : "";
            namaCus = bundle.getString("nama");
            kdCus = bundle.getString("kdcus");
            kdBrg = bundle.getString("kdbrg");
            namaBrg = bundle.getString("namabrg");
            satuanBarang = bundle.getString("satuan");
            flag = bundle.getString("flag");

            if(flag.equals("K")) {
                satuanJumlah = satuanKabel;
                //if(!satuanBarang.toUpperCase().equals("ROLL")) satuanBarang = "ROLL";
            }else{
                satuanJumlah = satuanLampu;
                //if(!satuanBarang.toUpperCase().equals("BOX")) satuanBarang = "BOX";
            }

            tvSatuan1.setText(satuanBarang);
            tvSatuan2.setText(satuanBarang);
            edNamaPelanggan.setText(namaCus);
            tvSave.setText("Simpan Order Custom");

            edNamaBarang.setText(namaBrg);
            String formatDate = getResources().getString(R.string.format_date);
            String formatDateDisplay = getResources().getString(R.string.format_date_display);
            tanggal = iv.getCurrentDate(formatDate);
            edTanggal.setText(iv.ChangeFormatDateString(tanggal, formatDate, formatDateDisplay));

            tanggalTempo = iv.sumDate(tanggal, 30, formatDate);
            edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));
            iv.datePickerEvent(OrderCustomDetail.this, edTanggalTempo, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));

            // Tambah barang baru atau edit barang
            if(noBukti.length()>0){

                llNoSO.setVisibility(View.VISIBLE);
                edNoSO.setText(noBukti);

                tanggal = bundle.getString("tgl");
                tanggalTempo = bundle.getString("tgltempo");
                edTanggal.setText(iv.ChangeFormatDateString(tanggal, formatDate, formatDateDisplay));
                edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo,formatDate, formatDateDisplay));
                statusSO = bundle.getString("status");
                idOrder = (bundle.getString("id") != null && bundle.getString("id") != "" ) ? bundle.getString("id"): "";
                if(idOrder.length() > 0){

                    updateMode = true;
                    method = "PUT";
                    Type tipeItem = new TypeToken<CustomListItem>(){}.getType();
                    Gson gson = new Gson();
                    String itemString = (bundle.getString("item") != null && bundle.getString("item") != "") ? bundle.getString("item") : "";
                    if(itemString.length()>0){

                        CustomListItem item = gson.fromJson(itemString,tipeItem);
                        fillForm(item);
                    }
                }else{

                    urlSaveOC = getResources().getString(R.string.url_save_custom_detail);
                }
            }

            getDiskonEvent();
            getSaveEvent();

        }
    }

    private void fillForm(CustomListItem item){

        crBayar = item.getListItem13();
        edNamaBarang.setText(item.getListItem11());
        edKeterangan.setText(item.getListItem1());
        edJumlah.setText(item.getListItem3());
        edUkuran.setText(item.getListItem12());
        Double totalJumlah = iv.parseNullDouble(item.getListItem3()) / iv.parseNullDouble(item.getListItem12());
        List<String> totalArray = new ArrayList<String>(Arrays.asList(iv.doubleToString(totalJumlah).split("\\.")));
        //String totalJumlahString = (totalArray.size() > 1 && totalArray.get(1).equals("0")) ? totalArray.get(0) :iv.doubleToString(totalJumlah);
        String totalJumlahString = (totalArray.size() > 1 && totalArray.get(1).equals("0")) ? totalArray.get(0) :String.valueOf(iv.parseNullLong(totalArray.get(0) + 1));
        edTotalJumlah.setText(totalJumlahString+" "+ satuanJumlah);
        jumlah = item.getListItem3();
        edSatuan.setText(item.getListItem4());
        edHarga.setText(item.getListItem2());
        edDiskon.setText(item.getListItem5());
        edHargaWithDiskon.setText(item.getListItem6());
        edHargaTotal.setText(item.getListItem7());

    }

    private void getDiskonEvent() {

        edDiskon.setFocusable(true);
        edDiskon.setKeyListener(new EditText(OrderCustomDetail.this).getKeyListener());
        edDiskon.setFocusableInTouchMode(true);

        edHargaWithDiskon.setFocusable(true);
        edHargaWithDiskon.setKeyListener(new DigitsKeyListener(true,true));
        edHargaWithDiskon.setFocusableInTouchMode(true);

        statusChangeHarga = 1;

        edUkuran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CalculateTotalJumlah();
                ChangeLabelName();
            }
        });

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
                                    if(edJumlah.getText().length() > 0){

                                        jumlah = edJumlah.getText().toString();
                                        //CalculateHargaByDiskon();

                                    }else if(edJumlah.getText().length() <= 0 ){
                                        ClearHargaField();
                                    }

                                    CalculateTotalJumlah();
                                }
                            }
                        },
                        delay
                );
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

                edHarga.removeTextChangedListener(this);
                edHarga.addTextChangedListener(this);

                statusChangeHarga = 1;
                CalculateHargaByDiskon();
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

                edHargaWithDiskon.removeTextChangedListener(this);
                edHargaWithDiskon.addTextChangedListener(this);

                if(!editable.toString().equals("0") && !afterCalculate){
                    statusChangeHarga = 2;
                    CalculateHargaByDiskon();
                }
            }
        });
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
    }

    private void CalculateTotalJumlah(){

        final String ukuranString = edUkuran.getText().toString();
        String jumlahString = edJumlah.getText().toString();

        Double ukuranDouble = iv.parseNullDouble(ukuranString);
        Double jumlahDouble = iv.parseNullDouble(jumlahString);

        Double totalJumlah = Double.valueOf(0);

        try {
            totalJumlah = (ukuranDouble > 0 && jumlahDouble>0) ? (jumlahDouble / ukuranDouble) : 0;
        }catch (Exception e){
            e.printStackTrace();
        }

        final Double finalTotalJumlah = totalJumlah;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                List<String> totalArray = new ArrayList<String>(Arrays.asList(iv.doubleToString(finalTotalJumlah).split("\\.")));
                //String totalJumlahString = (totalArray.size() > 1 && totalArray.get(1).equals("0")) ? totalArray.get(0) :iv.doubleToString(finalTotalJumlah);
                String totalJumlahString = (totalArray.size() > 1 && totalArray.get(1).equals("0")) ? totalArray.get(0) : String.valueOf(iv.parseNullLong(totalArray.get(0)) + 1);
                edTotalJumlah.setText(totalJumlahString+ " " + satuanJumlah);
            }
        });

        isLoading = true;
        getHargaBarang(jumlahString);
        //getHargaBarang(String.valueOf(finalTotalJumlah.longValue()));
    }

    private void ChangeLabelName(){

        final String ukuranString = edUkuran.getText().toString();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                edKeterangan.setText(namaBrg + " "+ ukuranString + " " + satuanBarang);
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
                edDiskon.setText("0");
                edHargaWithDiskon.setText("0");
                edHargaTotal.setText("0");
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

    private void CalculateHargaByDiskon(){

        String changedString = edDiskon.getText().toString().replaceAll("[+]", ",");

        Double ukuranDouble = iv.parseNullDouble(edUkuran.getText().toString());
        Double jumlahDouble = iv.parseNullDouble(edJumlah.getText().toString());

        final Double totalJumlah = jumlahDouble / ukuranDouble;

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
            double hargaAwal = iv.parseNullDouble(edHarga.getText().toString());
            //long newHargaNetto = 0;
            double newHargaNetto = 0;
            Integer index = 1;

            for(Double i: diskonListDouble){
                if(index == 1){
                    double minDiskon = i / 100 * hargaAwal;
                    newHargaNetto = hargaAwal - (i / 100 * hargaAwal);
                }else{
                    newHargaNetto = newHargaNetto - ( i / 100 * newHargaNetto);
                }
                index++;
            }

            if(statusChangeHarga != 2){

                final double finalNewHargaNetto1 = newHargaNetto;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        afterCalculate = true;
                        edHargaWithDiskon.setText(iv.doubleToString(finalNewHargaNetto1));
                        edHargaTotal.setText(iv.doubleToString(finalNewHargaNetto1 * totalJumlah));
                    }
                });
            }else{

                if(Double.parseDouble(edHargaWithDiskon.getText().toString()) > 0){
                    newHargaNetto = Double.parseDouble(edHargaWithDiskon.getText().toString());
                }

                final double finalNewHargaNetto = newHargaNetto;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edHargaTotal.setText(iv.doubleToString(finalNewHargaNetto * totalJumlah));
                    }
                });
            }

            afterCalculate = false;

        }else{
            // clear field

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edHargaWithDiskon.setText("0");
                    edHargaTotal.setText("0");
                }
            });
        }
    }

    private void getHargaBarang(final String jumlahTotal) {

        /*
        * get Harga & total
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.VISIBLE);
            }
        });

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("kode_brg", kdBrg);
            jsonBody.put("qty", jumlahTotal);
            jsonBody.put("flag", "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                        crBayar = responseAPI.getJSONObject("response").getString("crbayar");
                        String diskon = responseAPI.getJSONObject("response").getString("diskon");
                        String hargaNetto = responseAPI.getJSONObject("response").getString("harganetto");
                        edHarga.setText(harga);
                        edDiskon.setText(diskon);

                        if(diskon.length()>0 && !diskon.equals("0")){

                            CalculateHargaByDiskon();
                        }else{

                            Double total = (iv.parseNullDouble(hargaNetto) > 0 ) ? (iv.parseNullDouble(hargaNetto) * iv.parseNullDouble(jumlahTotal)): (iv.parseNullDouble(harga) * iv.parseNullDouble(jumlahTotal));

                            edHargaWithDiskon.setText(hargaNetto);
                            edHargaTotal.setText(iv.doubleToString(total));
                        }
                    }else{
                        edHarga.setText("0");
                        edDiskon.setText("0");
                        edHargaWithDiskon.setText("0");
                        edHargaTotal.setText("0");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbLoading.setVisibility(View.GONE);
                        }
                    });

                    isLoading = false;
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
                isLoading = false;
            }

            @Override
            public void onError(String result) {
                edHarga.setText("0");
                edDiskon.setText("0");
                edHargaWithDiskon.setText("0");
                edHargaTotal.setText("0");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
                isLoading = false;
            }
        });
    }

    private void getSaveEvent() {

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //region validate before saving
                tanggal = iv.ChangeFormatDateString(edTanggal.getText().toString(), formatDateDisplay, formatDate);
                tanggalTempo = iv.ChangeFormatDateString(edTanggalTempo.getText().toString(), formatDateDisplay, formatDate);

                if(iv.parseNullLong(edUkuran.getText().toString()) <= 0){

                    tilUkuran.setError("Mohon isi ukuran");
                    edUkuran.requestFocus();
                    return;
                }

                if(iv.parseNullLong(edJumlah.getText().toString()) <= 0){

                    tilJumlah.setError("Mohon isi jumlah");
                    edJumlah.requestFocus();
                    return;
                }

                if(isLoading){

                    Toast.makeText(OrderCustomDetail.this, "Tunggu hingga proses selesai",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(tanggalTempo.length() <= 0){

                    Toast.makeText(OrderCustomDetail.this, "Tanggal tempo harap diisi",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!iv.validateCustomDate(edTanggalTempo, formatDateDisplay)){

                    Toast.makeText(OrderCustomDetail.this, "Format Tanggal Tempo tidak benar",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!iv.isMoreThanCurrentDate(edTanggalTempo, edTanggal, formatDateDisplay)){

                    Toast.makeText(OrderCustomDetail.this, "Tanggal Tempo harus lebih dari tanggal order",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Jumlah
                if((edJumlah.getText().length() <= 0)){
                    edJumlah.setError("Jumlah harus lebih dari 0");
                    edJumlah.requestFocus();
                    return;
                }

                if(iv.parseNullInteger(statusSO) == 3 || iv.parseNullInteger(statusSO) == 7 || iv.parseNullInteger(statusSO) == 9){
                    String peringatan = "Tidak Dapat Menanbah Order";
                    switch (iv.parseNullInteger(statusSO)){
                        case 3:
                            peringatan = "Tidak dapat menambah Order yang telah disetujui";
                            break;
                        case 7:
                            peringatan = "Tidak dapat menambah Order yang telah di Posting";
                            break;
                        case 9:
                            peringatan = "Tidak dapat menambah Order yang ditolak";
                            break;
                    }
                    Toast.makeText(OrderCustomDetail.this, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                keterangan = edKeterangan.getText().toString();
                ukuran = edUkuran.getText().toString();
                jumlah = edJumlah.getText().toString();
                satuan = edSatuan.getText().toString();
                harga = edHarga.getText().toString();
                diskon = edDiskon.getText().toString();
                hargadiskon = edHargaWithDiskon.getText().toString();
                total = edHargaTotal.getText().toString();

                AlertDialog builder = new AlertDialog.Builder(OrderCustomDetail.this)
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

                                simpanData();
                            }
                        }).show();
            }
        });
    }

    private void simpanData() {

        // Header
        JSONObject header = new JSONObject();
        try {
            header.put("nobukti", noBukti);
            header.put("kdcus", kdCus);
            header.put("tgl", tanggal);
            header.put("tgltempo", tanggalTempo);
            header.put("total", total);
            header.put("status", statusSO);
            header.put("flag", flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Detail
        JSONObject detail = new JSONObject();
        try {
            detail.put("id", idOrder);
            detail.put("nobukti", noBukti);
            detail.put("keterangan", keterangan);
            detail.put("kdbrg", kdBrg);
            detail.put("harga", harga);
            detail.put("ukuran", ukuran);
            detail.put("diskon", diskon);
            detail.put("jumlah", jumlah);
            detail.put("satuan", satuanBarang);
            detail.put("total", total);
            detail.put("hargadiskon", hargadiskon);
            detail.put("crbayar", crBayar);
            detail.put("gudang", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("header", header);
            jsonBody.put("detail", detail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(OrderCustomDetail.this, jsonBody, method, urlSaveOC, "", "", 0,

                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(OrderCustomDetail.this, dialog, Toast.LENGTH_LONG).show(); // show message response
                            if(iv.parseNullInteger(statusI) == 200){

                                if(noBukti.length() == 0) new LocationUpdateHandler(OrderCustomDetail.this,"Tambah Order "+ responseAPI.getJSONObject("response").getString("nobukti"));
                                noBukti = responseAPI.getJSONObject("response").getString("nobukti");
                                Intent intent = new Intent(OrderCustomDetail.this, DetailOrderCustom.class);
                                intent.putExtra("nobukti", noBukti);
                                intent.putExtra("kdcus", kdCus);
                                intent.putExtra("nama", namaCus);
                                startActivity(intent);
                                finish();
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
