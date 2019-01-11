package gmedia.net.id.kartikaelektrik.activityPermintaanHargaOrder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter.DetailSOApproveAdapter;
import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailPermintaanHargaOrder extends AppCompatActivity {

    private AutoCompleteTextView actvNamaBarang;
    private ListView lvListBarang;
    private TextView tvTotalSO;
    private LinearLayout llApprove;
    private LinearLayout llDeny;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private List<SalesOrderDetail> masterSOList;
    private ItemValidation iv = new ItemValidation();
    private String noBukti, tanggal, tanggalTempo;
    private String urlGetDetailSO, urlUpdateSOStatus;
    private LinearLayout llLoadSO;
    private ProgressBar pbLoadSO;
    private Button btnRefresh;
    private boolean firstLoad = true;
    private String status;
    private String namaPelanggan, namaAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_permintaan_harga_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        urlGetDetailSO = ServerURL.getApproveSOBarang;
        urlUpdateSOStatus = getResources().getString(R.string.url_update_status_so);
        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        lvListBarang = (ListView) findViewById(R.id.lv_list_barang);
        tvTotalSO = (TextView) findViewById(R.id.tv_total_so);
        llApprove = (LinearLayout) findViewById(R.id.ll_approve);
        llDeny = (LinearLayout) findViewById(R.id.ll_denial);
        llLoadSO = (LinearLayout) findViewById(R.id.ll_load_so);
        pbLoadSO = (ProgressBar) findViewById(R.id.pb_load_so);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            // Set Approve / deny action
            llApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String message = "Apakah Anda yakin akan MENYETUJUI Order ini?";

                    String hargaJual = "", hargaBeli = "", barang = "";
                    boolean isExist = false;

                    for(CustomListItem item: masterList){

                        if(iv.parseNullDouble(item.getListItem6()) < iv.parseNullDouble(item.getListItem7())){ // harga jual <  harga beli

                            barang = item.getListItem1();
                            isExist = true;
                            break;
                        }
                    }

                    if(isExist){

                        message = "Apakah Anda yakin akan MENYETUJUI Order ini?, harga jual item " + barang + " lebih kecil dari harga beli.";
                    }

                    AlertDialog alert = new AlertDialog.Builder(DetailPermintaanHargaOrder.this)
                            .setTitle("Konfirmasi")
                            .setMessage(message)
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    status = "3";
                                    UpdateApprovement();
                                }
                            })
                            .show();
                }
            });

            llDeny.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    AlertDialog alert = new AlertDialog.Builder(DetailPermintaanHargaOrder.this)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah Anda yakin akan MENOLAK Order ini?")
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    status = "9";
                                    UpdateApprovement();
                                }
                            })
                            .show();
                }
            });

            namaPelanggan = bundle.getString("namapelanggan","");
            namaAlamat = bundle.getString("namaalamat","");
            noBukti = bundle.getString("nobukti","");
            setTitle("Persetujuan Order "+ noBukti);

            setDetailSO();
        }

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDetailSO();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDetailSO();
    }

    private void setDetailSO() {

        /*
        * Access the detail SO
        */

        // Get Detail Barang
        final JSONObject jsonBody = new JSONObject();
        iv.ProgressbarEvent(llLoadSO, pbLoadSO, btnRefresh, "SHOW");
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetDetailSO+noBukti, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            JSONObject responseJSON = responseAPI.getJSONObject("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();
                            masterSOList = new ArrayList<SalesOrderDetail>();

                            if(Integer.parseInt(status) == 200){

                                // Get Header
                                try{
                                    tanggal = responseJSON.getJSONObject("so_header").getString("tgl");
                                    tanggalTempo = responseJSON.getJSONObject("so_header").getString("tgltempo");
                                    float totalHarga = iv.parseNullFloat(responseJSON.getJSONObject("so_header").getString("total"));
                                    tvTotalSO.setText(iv.ChangeToRupiahFormat(totalHarga));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                // Get Detail
                                JSONArray jsonArray = responseJSON.getJSONArray("so_detail");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);

                                    SalesOrderDetail sod = new SalesOrderDetail(jo.getString("id"), jo.getString("kdbrg"), jo.getString("namabrg"),jo.getString("jumlah"),jo.getString("satuan"),jo.getString("total"));
                                    sod.setJumlahpcs(jo.getString("jumlahpcs"));
                                    sod.setHarga(jo.getString("harga"));
                                    sod.setHargapcs(jo.getString("hargapcs"));
                                    sod.setDiskon(jo.getString("diskon"));
                                    sod.setStatus(responseJSON.getJSONObject("so_header").getString("status"));
                                    masterSOList.add(sod);

                                    CustomListItem cli = new CustomListItem();
                                    cli.setListItem1(jo.getString("namabrg"));
                                    cli.setListItem2(jo.getString("jumlah") + " " + jo.getString("satuan"));
                                    cli.setListItem3(iv.ChangeToRupiahFormat(iv.parseNullFloat(jo.getString("total"))));
                                    cli.setListItem4(jo.getString("id"));
                                    cli.setListItem5(iv.ChangeToRupiahFormat(iv.parseNullFloat(jo.getString("hargabeli3"))));
                                    cli.setListItem6(jo.getString("harga"));
                                    cli.setListItem7(jo.getString("hargabeli3"));
                                    masterList.add(cli);
                                }

                                autocompleteList = new ArrayList<>(masterList);
                                tableList = new ArrayList<>(masterList);

                                getAutocomplete(autocompleteList);
                                getTable(tableList);
                            }else{
                                getAutocomplete(null);
                                getTable(null);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            getAutocomplete(null);
                            getTable(null);
                        }
                        iv.ProgressbarEvent(llLoadSO, pbLoadSO, btnRefresh, "GONE");
                    }

                    @Override
                    public void onError(String result) {
                        getAutocomplete(null);
                        getTable(null);
                        iv.ProgressbarEvent(llLoadSO, pbLoadSO, btnRefresh, "ERROR");
                    }
                });
    }

    private void UpdateApprovement() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(this, jsonBody, "PUT", urlUpdateSOStatus+noBukti, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String statusString = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");
                            if(iv.parseNullInteger(statusString) == 200){
                                if(iv.parseNullInteger(status) == 3){
                                    Toast.makeText(DetailPermintaanHargaOrder.this, "Order "+noBukti+" Disetujui", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(DetailPermintaanHargaOrder.this, "Order "+noBukti+" Ditolak", Toast.LENGTH_LONG).show();
                                }
                                Intent intent = new Intent(DetailPermintaanHargaOrder.this, DashboardContainer.class);
                                intent.putExtra("kodemenu", "permintaanhargaorder");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(DetailPermintaanHargaOrder.this,message, Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Toast.makeText(DetailPermintaanHargaOrder.this,"Terjadi kesalahan", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(DetailPermintaanHargaOrder.this, result,Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getAutocomplete(List<CustomListItem> listItems) {

        actvNamaBarang.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailPermintaanHargaOrder.this, listItems.size(),listItems, "");

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);*/

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvNamaBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNamaBarang.getText().length() <= 0){
                        getTable(tableList);
                    }
                }
            });
        }

        actvNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getTable(items);
            }
        });

        actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    String keyword = actvNamaBarang.getText().toString().trim().toUpperCase();
                    for(CustomListItem item: masterList){

                        if(item.getListItem1().toUpperCase().contains(keyword)){
                            items.add(item);
                        }
                    }
                    getTable(items);
                    iv.hideSoftKey(DetailPermintaanHargaOrder.this);
                    return true;
                }

                return false;
            }
        });
    }

    private void getTable(List<CustomListItem> listItems) {

        lvListBarang.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            DetailSOApproveAdapter arrayAdapter;
            arrayAdapter = new DetailSOApproveAdapter(DetailPermintaanHargaOrder.this, listItems.size(), listItems);
            lvListBarang.setAdapter(arrayAdapter);

            lvListBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem4();
                    SalesOrderDetail selectedSOD = new SalesOrderDetail();
                    for(SalesOrderDetail sod: masterSOList){
                        if(key.trim().equals(sod.getSoDetailID())){
                            selectedSOD = sod;
                        }
                    }
                    Intent intent = new Intent(DetailPermintaanHargaOrder.this, SubDetailPermintaanHargaOrder.class);
                    Gson gson = new Gson();
                    intent.putExtra("sod", gson.toJson(selectedSOD));
                    intent.putExtra("namapelanggan", namaPelanggan);
                    intent.putExtra("tanggal", tanggal);
                    startActivity(intent);
                }
            });
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
