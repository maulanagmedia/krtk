package gmedia.net.id.kartikaelektrik.activityPengajuanTempo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPengajuanTempo.Adapter.BarangTempoAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class ActBarangPengajuanTempo extends AppCompatActivity {

    private AutoCompleteTextView actvListBarang;
    private String TAG = "BarangOrder";
    private ListView lvListBarang;
    private ItemValidation iv = new ItemValidation();
    private String kdCus = "", namaPelanggan = "", kodeKategoriBarang = "", namaKategoriBarang = "", kodeBarang = "", namaBarang = "", noSalesOrder, tempo = "";

    //Fragment Detail Barang
    private List<Barang> listMasterBarang, moreList;
    private ProgressBar pgbLoadBarang;
    private LinearLayout llProgressBarContainer;
    private Button btnRefreshBarang;
    private boolean firstLoadBarang = true;
    private int start = 0, count = 10;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private BarangTempoAdapter arrayAdapterString;
    private Context context;

    //remember, this page using "not best practice" process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_barang_pengajuan_tempo);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pilih Barang Order");

        context = this;
        initUI();
    }

    private void initUI() {

        actvListBarang = (AutoCompleteTextView) findViewById(R.id.actv_list_nama_barang);
        lvListBarang = (ListView) findViewById(R.id.lv_fragment_detail_category_barang);
        pgbLoadBarang = (ProgressBar) findViewById(R.id.pgb_nama_barang);
        llProgressBarContainer = (LinearLayout) findViewById(R.id.ll_pgb_container);
        btnRefreshBarang = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            kodeKategoriBarang = ""; // ALL Kategory
            kdCus = extras.getString("kdCus");
            namaPelanggan = extras.getString("namaPelanggan");
            tempo = extras.getString("tempo");
        }

        getDetailBarangAutocomplete();
        btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetailBarangAutocomplete();
            }
        });

    }

    public void getDetailBarangAutocomplete(){

        start = 0;
        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"SHOW");
        isLoading = true;

        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getJenisBarangTempo, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listMasterBarang = new ArrayList<Barang>();
                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    Barang item = new Barang(
                                            jo.getString("kdjenis"),
                                            jo.getString("merk"));

                                    item.setKdMerk(jo.getString("kdmerk"));
                                    item.setKdJenis(jo.getString("kdjenis"));
                                    listMasterBarang.add(item);
                                }
                            }

                            getListBarangAutocomplete(listMasterBarang);
                            getListBarangTable(listMasterBarang);

                        }catch (Exception e){
                            e.printStackTrace();
                            getListBarangAutocomplete(null);
                            getListBarangTable(null);
                        }
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"ERROR");
                    }
                });
    }

    private void getListBarangAutocomplete(List<Barang> listItems){

        actvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            /*BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(ListBarangSalesOrder.this, listItems.size(), listItems);

            //set adapter to autocomplete
            actvListBarang.setAdapter(arrayAdapterString);*/

            barangAutocompleteEvent();
        }
    }

    private void barangAutocompleteEvent(){

        actvListBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvListBarang.getText().toString();
                    getDetailBarangAutocomplete();
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

        if(firstLoadBarang){
            firstLoadBarang = false;
            actvListBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(editable.toString().length() <= 0) {

                        keyword = "";
                        getDetailBarangAutocomplete();
                    }
                }
            });
        }
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            arrayAdapterString = new BarangTempoAdapter((Activity) context, listItems.size(), listItems);

            //set adapter to autocomplete
            lvListBarang.setAdapter(arrayAdapterString);

            lvListBarang.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvListBarang.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvListBarang.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvListBarang.addFooterView(footerList);
                            start += count;
                            getMoreData();
                            //Log.i(TAG, "onScroll: last ");
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });

            lvListBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Barang barang = (Barang) adapterView.getItemAtPosition(i);
                    kodeBarang = barang.getKodeBarang();
                    namaBarang = barang.getNamaBarang();

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin menyimpan data?\n"+namaPelanggan+"\n"+"Tempo " + tempo+ "\n"+namaBarang)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    saveData(barang);
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();

                }
            });
        }
    }

    public void getMoreData(){

        isLoading = true;
        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getJenisBarangTempo, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        lvListBarang.removeFooterView(footerList);
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            moreList = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    Barang item = new Barang(
                                            jo.getString("kdjenis"),
                                            jo.getString("merk"));

                                    item.setKdMerk(jo.getString("kdmerk"));
                                    item.setKdJenis(jo.getString("kdjenis"));
                                    moreList.add(item);
                                }
                            }

                            if(arrayAdapterString != null) arrayAdapterString.addMoreData(moreList);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        lvListBarang.removeFooterView(footerList);
                        isLoading = false;
                    }
                });
    }

    private void saveData(Barang barang) {

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("kdmerk", barang.getKdMerk());
            jsonBody.put("kdjenis", barang.getKdJenis());
            jsonBody.put("kdcus", kdCus);
            jsonBody.put("tempo", tempo);
            jsonBody.put("toleransi", "4");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(this, jsonBody, "POST", ServerURL.savePengajuanTempo, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        progressDialog.dismiss();
                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String statusString = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(context,message, Toast.LENGTH_LONG).show();

                            if(iv.parseNullInteger(statusString) == 200){

                                Intent intent = new Intent(context, DashboardContainer.class);
                                intent.putExtra("kodemenu", "menupengajuantempo");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        }catch (Exception e){
                            Toast.makeText(context,"Terjadi kesalahan", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        progressDialog.dismiss();
                        Toast.makeText(context, result,Toast.LENGTH_LONG).show();
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
