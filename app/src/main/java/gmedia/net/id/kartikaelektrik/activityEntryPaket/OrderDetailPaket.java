package gmedia.net.id.kartikaelektrik.activityEntryPaket;

import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySalesOrderDetail.DetailSalesOrder;
import gmedia.net.id.kartikaelektrik.adapter.EntryPaket.ListBarangOrderAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;

public class OrderDetailPaket extends AppCompatActivity {

    private String noBukti, kdCus, namaCus, kdPaket, namaPaket, jenisPaket;
    private LinearLayout llNoSO;
    private TextInputLayout tilNoSO, tilTanggalTempo;
    private EditText edNoSO, edNamaPelanggan, edJenisPaket, edNamaPaket, edTanggal, edTanggalTempo;
    private RecyclerView rvBarang;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    public static TextView tvTotalJumlah, tvTotalHarga;
    private List<CustomListItem> masterList, tableList;
    private String listBarangPaket = "";
    private static ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";
    private String tanggal = "", tanggalTempo = "";
    public static long totalJumlah = 0;
    private long totalJumlahFinal = 0;
    public static Double totalharga = Double.valueOf(0);
    private String urlSaveSO = "";
    private String urlGetBarang = "";
    private String kodeBarang = "", satuan = "", gudang = "";
    public static boolean onEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_paket);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Detail Barang Paket");

        llNoSO = (LinearLayout) findViewById(R.id.ll_no_so_container);
        tilNoSO = (TextInputLayout) findViewById(R.id.til_no_so);
        edNoSO = (EditText) findViewById(R.id.edt_no_so);
        edNamaPelanggan = (EditText) findViewById(R.id.edt_nama_pelanggan);
        edJenisPaket = (EditText) findViewById(R.id.edt_jenis_paket);
        edNamaPaket = (EditText) findViewById(R.id.edt_nama_paket);
        edTanggal = (EditText) findViewById(R.id.edt_tanggal);
        tilTanggalTempo = (TextInputLayout) findViewById(R.id.til_tanggal_tempo);
        edTanggalTempo = (EditText) findViewById(R.id.edt_tanggal_tempo);
        rvBarang = (RecyclerView) findViewById(R.id.rv_barang);
        tvTotalJumlah = (TextView) findViewById(R.id.tv_total_jumlah);
        tvTotalHarga = (TextView) findViewById(R.id.tv_total_harga);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);

        urlSaveSO = getResources().getString(R.string.url_save_so_paket);
        urlGetBarang = getResources().getString(R.string.url_get_barang_paket);

        Bundle bundle =  getIntent().getExtras();
        if(bundle != null){

            noBukti = (bundle.getString("nobukti") != null) ? bundle.getString("nobukti") : "" ;
            kdCus = bundle.getString("kdcus");
            namaCus = bundle.getString("nama");
            kdPaket = bundle.getString("kdpaket");
            namaPaket = bundle.getString("namapaket");
            jenisPaket = bundle.getString("jenispaket");

            edNamaPelanggan.setText(namaCus);
            edJenisPaket.setText(jenisPaket);
            edNamaPaket.setText(namaPaket);

            formatDate = getResources().getString(R.string.format_date);
            formatDateDisplay = getResources().getString(R.string.format_date_display);
            tanggal = iv.getCurrentDate(formatDate);
            edTanggal.setText(iv.ChangeFormatDateString(tanggal, formatDate, formatDateDisplay));

            tanggalTempo = iv.sumDate(tanggal,30, formatDate);
            edTanggalTempo.setText(iv.ChangeFormatDateString(tanggalTempo, formatDate, formatDateDisplay));
            edTanggalTempo.setKeyListener(null);
            iv.datePickerEvent(OrderDetailPaket.this, edTanggalTempo, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalTempo, formatDate, formatDateDisplay));

            tvSave.setText("Simpan Order Baru");
            if(!noBukti.equals("")) tvSave.setText("Tambah Barang Paket");

            getAllData();
            llSaveContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateBeforeSaving();
                }
            });
        }
    }

    private void getAllData() {

        onEditing = true;
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetBarang + kdPaket, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();
                            tableList = new ArrayList<>();
                            totalJumlah = 0;
                            totalJumlahFinal = 0;
                            totalharga = Double.valueOf(0);

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    CustomListItem item1 = new CustomListItem(jo.getString("id"), jo.getString("kdbrg"), jo.getString("namabrg"), jo.getString("jumlah"), jo.getString("satuan"), jo.getString("harga"), iv.doubleToString(iv.parseNullDouble(jo.getString("jumlah")) * iv.parseNullDouble(jo.getString("harga"))));
                                    CustomListItem item2 = new CustomListItem(jo.getString("id"), jo.getString("kdbrg"), jo.getString("namabrg"), jo.getString("jumlah"), jo.getString("satuan"), jo.getString("harga"), iv.doubleToString(iv.parseNullDouble(jo.getString("jumlah")) * iv.parseNullDouble(jo.getString("harga"))));

                                    masterList.add(item1);
                                    tableList.add(item2);

                                    totalJumlah += iv.parseNullDouble(jo.getString("jumlah"));
                                    totalJumlahFinal += iv.parseNullDouble(jo.getString("jumlah"));
                                    totalharga += (iv.parseNullDouble(jo.getString("jumlah")) * iv.parseNullDouble(jo.getString("harga")));
                                }

                                setTotal(totalJumlah, totalharga);
                            }

                            setTableList(masterList);

                            onEditing = false;

                        }catch (Exception e){
                            e.printStackTrace();
                            setTableList(null);
                            onEditing = true;
                        }
                    }

                    @Override
                    public void onError(String result) {
                        setTableList(null);
                        onEditing = true;
                    }
                });

    }

    private void setTableList(List<CustomListItem> listItem){

        rvBarang.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ListBarangOrderAdapter adapter = new ListBarangOrderAdapter(OrderDetailPaket.this, listItem);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(OrderDetailPaket.this, 1);
            rvBarang.setLayoutManager(mLayoutManager);
            rvBarang.setItemAnimator(new DefaultItemAnimator());
            rvBarang.setAdapter(adapter);
        }
    }

    public static void setTotal(long jumlah, Double harga){

        totalJumlah = jumlah;
        totalharga = harga;
        tvTotalJumlah.setText(String.valueOf(jumlah));
        tvTotalHarga.setText(iv.ChangeToRupiahFormat(totalharga));
        onEditing = false;
    }

    private void validateBeforeSaving(){

        tanggal = iv.ChangeFormatDateString(edTanggal.getText().toString(), formatDateDisplay, formatDate);
        tanggalTempo = iv.ChangeFormatDateString(edTanggalTempo.getText().toString(), formatDateDisplay, formatDate);

        if(onEditing){
            Toast.makeText(OrderDetailPaket.this, "Mohon tunggu hingga data termuat",Toast.LENGTH_SHORT).show();
            return;
        }

        if(iv.parseNullLong(tvTotalJumlah.getText().toString()) < totalJumlahFinal){
            Toast.makeText(OrderDetailPaket.this, "Jumlah tidak boleh kurang dari " + totalJumlahFinal,Toast.LENGTH_SHORT).show();
            return;
        }

        if(tanggalTempo.length() <= 0){
            Toast.makeText(OrderDetailPaket.this, "Tanggal tempo harap diisi",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!iv.validateCustomDate(edTanggalTempo, formatDateDisplay)){
            Toast.makeText(OrderDetailPaket.this, "Format Tanggal Tempo tidak benar",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!iv.isMoreThanCurrentDate(edTanggalTempo,edTanggal,formatDateDisplay)){
            //tilTanggalTempo.setErrorEnabled(true);
            //tilTanggalTempo.setError("Tanggal Tempo Tidak Dapat Sebelum Tanggal Order");
            Toast.makeText(OrderDetailPaket.this, "Tanggal Tempo harus lebih dari tanggal order",Toast.LENGTH_SHORT).show();
            //edTanggalTempo.requestFocus();
            return;
        }

        AlertDialog builder = new AlertDialog.Builder(OrderDetailPaket.this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin meyimpan data?")
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        simpanOrder();
                    }
                }).show();
    }

    private void simpanOrder(){

        String status = "1";

        ListBarangOrderAdapter adapter = (ListBarangOrderAdapter) rvBarang.getAdapter();
        List<CustomListItem> selectedBarangList = adapter.getMasterData();

        for(int i = 0; i < tableList.size(); i++){
            if(!tableList.get(i).getListItem4().equals(selectedBarangList.get(i).getListItem4())){
                status = "4";
            }
        }

        // Header
        JSONArray header = new JSONArray();
        JSONObject header0 = new JSONObject();
        try {
            header0.put("nobukti", noBukti);
            header0.put("kdcus", kdCus);
            header0.put("tgl", tanggal);
            header0.put("tgltempo", tanggalTempo);
            header0.put("total", iv.doubleToString(totalharga));
            header0.put("status", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        header.put(header0);

        // Detail
        JSONArray detail = new JSONArray();
        JSONObject detail0 = new JSONObject();
        for(CustomListItem item: selectedBarangList){

            if(iv.parseNullLong(item.getListItem4()) > 0){ // Jumlah tidak kosong

                detail0 = new JSONObject();
                try {
                    detail0.put("nobukti", noBukti);
                    detail0.put("kdbrg", item.getListItem2());
                    detail0.put("harga", item.getListItem6());
                    detail0.put("jumlah", item.getListItem4());
                    detail0.put("jumlahpcs", item.getListItem4());
                    detail0.put("hargapcs", item.getListItem6());
                    detail0.put("diskon", "");
                    detail0.put("satuan", item.getListItem5());
                    detail0.put("total", item.getListItem7());
                    detail0.put("kdpaket", kdPaket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                detail.put(detail0);
            }
        }

        header.put(header0);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("header", header);
            jsonBody.put("detail", detail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(OrderDetailPaket.this, jsonBody, "POST", urlSaveSO, "", "", 0,

                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(OrderDetailPaket.this, dialog, Toast.LENGTH_LONG).show(); // show message response

                            if(iv.parseNullInteger(statusI) == 200){

                                String insertedNoBukti = responseAPI.getJSONObject("response").getString("nobukti");
                                if(noBukti.length() == 0) new LocationUpdateHandler(OrderDetailPaket.this,"Tambah Order "+ insertedNoBukti);
                                String insertedStatus = responseAPI.getJSONObject("response").getString("status");
                                Intent intent = new Intent(OrderDetailPaket.this, DetailSalesOrder.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("paket", true);
                                intent.putExtra("nosalesorder",insertedNoBukti);
                                intent.putExtra("status",insertedStatus);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
