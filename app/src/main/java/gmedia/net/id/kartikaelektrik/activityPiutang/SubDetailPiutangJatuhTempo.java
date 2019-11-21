package gmedia.net.id.kartikaelektrik.activityPiutang;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.Piutang.SubJatuhTempoTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class SubDetailPiutangJatuhTempo extends AppCompatActivity {

    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private boolean firstLoad = true;
    private ItemValidation iv = new ItemValidation();
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private String tanggalAwal = "", tanggalAkhir = "";
    private String urlGetPiutang = "";
    private AutoCompleteTextView actcNamaBarang;
    private TextView tvNama, tvTotal;
    private ListView lvBarang;
    private String noBukti = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail_piutang_jatuh_tempo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Detail Barang");

        urlGetPiutang = getResources().getString(R.string.url_get_all_piutang);
        actcNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            noBukti = bundle.getString("nobukti");
            String nama = bundle.getString("namacus");
            String total = bundle.getString("total");
            if(bundle.getBoolean("flag", false)){
                //setTitle("Detail Barang Customer");
            }
            tvNama.setText(nama);
            tvTotal.setText(total);

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAllData();
                }
            });

            getAllData();
        }
    }

    private void getAllData() {

        // Get All Customer
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("flag", "DP");
            jsonBody.put("kdcus", "");
            jsonBody.put("tgltempo", "");
            jsonBody.put("tgltempo2", "");
            jsonBody.put("tgltempo2", "");
            jsonBody.put("nobukti", noBukti);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(SubDetailPiutangJatuhTempo.this, jsonBody, "POST", urlGetPiutang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length(); i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(jo.getString("kdbrg"), jo.getString("namabrg"), jo.getString("jumlah") +" "+ jo.getString("satuan"), jo.getString("diskon"),iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("hargadiskon") )), iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("total")))));

                                }
                            }

                            autocompleteList= new ArrayList<CustomListItem>(masterList);
                            tableList= new ArrayList<CustomListItem>(masterList);
                            getListAutocomplete(autocompleteList);
                            getListTable(tableList);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListAutocomplete(null);
                            getListTable(null);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListAutocomplete(null);
                        getListTable(null);
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    // method to show autocomplete item
    private void getListAutocomplete(final List<CustomListItem> listItemCustomer){

        actcNamaBarang.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){

            // Listener event on change autocomplete
            if(firstLoad){
                firstLoad = false;
                actcNamaBarang.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(actcNamaBarang.length() <= 0){
                            getListTable(masterList);
                        }
                    }
                });
            }

            actcNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        String keyword = actcNamaBarang.getText().toString().trim().toUpperCase();
                        for(CustomListItem item: masterList){

                            if(item.getListItem2().toUpperCase().contains(keyword)){
                                itemToAdd.add(item);
                            }
                        }
                        getListTable(itemToAdd);
                        iv.hideSoftKey(SubDetailPiutangJatuhTempo.this);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    // method to show table
    private void getListTable(List<CustomListItem> listItemCustomer){

        lvBarang.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){

            SubJatuhTempoTableAdapter arrayAdapterString;
            arrayAdapterString = new SubJatuhTempoTableAdapter(SubDetailPiutangJatuhTempo.this,R.layout.adapter_sub_jatuh_tempo,listItemCustomer);
            lvBarang.setAdapter(arrayAdapterString);
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
