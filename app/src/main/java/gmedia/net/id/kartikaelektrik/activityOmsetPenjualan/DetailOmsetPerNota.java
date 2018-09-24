package gmedia.net.id.kartikaelektrik.activityOmsetPenjualan;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.OmsetPenjualan.OmsetPerCustomerTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailOmsetPerNota extends AppCompatActivity {

    private AutoCompleteTextView actvNamaBarang;
    private TextView tvNamaCustomer;
    private ListView lvListOmset;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private String kdCus = "", tanggalAwal = "", tanggalAkhir = "";
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private String urlGetOmset = "", noNota = "";
    private TextView tvNoNota;
    private String flag = "DB";
    private SessionManager session;
    private String nik = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_omset_per_nota);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        session = new SessionManager(this);
        initUI();
    }

    private void initUI() {

        setTitle("List Barang Omset");

        urlGetOmset = getResources().getString(R.string.url_get_omset);
        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        tvNamaCustomer = (TextView) findViewById(R.id.tv_nama);
        tvNoNota = (TextView) findViewById(R.id.tv_no_nota);
        lvListOmset = (ListView) findViewById(R.id.lv_list_omset);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            nik = bundle.getString("nik", session.getNik());
            tvNamaCustomer.setText(bundle.getString("nama",""));
            noNota = bundle.getString("nonota","");
            tvNoNota.setText(noNota);
            kdCus  = bundle.getString("kdcus","");
            tanggalAwal = bundle.getString("start","");
            tanggalAkhir = bundle.getString("end","");
            String title = bundle.getString("title","");
            if(bundle.getBoolean("flagbarang", false)) flag = "BN";
            setListAutocomplete();

            if(!title.isEmpty()) setTitle(title);

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setListAutocomplete();
                }
            });
        }
    }

    private void setListAutocomplete() {

        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        final String dateFormat = getResources().getString(R.string.format_date);
        final String dateFormatDisplay = getResources().getString(R.string.format_date_display1);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nik", nik);
            jsonBody.put("flag",flag);
            jsonBody.put("kdcus",kdCus);
            jsonBody.put("tgl",tanggalAwal);
            jsonBody.put("tgl2",tanggalAkhir);
            jsonBody.put("nonota",noNota);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(DetailOmsetPerNota.this, jsonBody, "POST", urlGetOmset, "", "", 0,
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
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    masterList.add(new CustomListItem(jo.getString("namabrg"), jo.getString("jumlah") + " " + jo.getString("satuan"), iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("hargajual"))), jo.getString("diskon"), iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("hargadiskon"))), iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("hpp"))),iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("total")))));

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

        actvNamaBarang.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailOmsetPerNota.this,listItemCustomer.size(),listItemCustomer, "L");

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);*/

            // Listener event on change autocomplete
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
                        if(actvNamaBarang.length() <= 0){
                            getListTable(tableList);
                        }
                    }
                });
            }

            actvNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                    List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                    itemToAdd.add(item);
                    getListTable(itemToAdd);
                }
            });

            actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        String keyword = actvNamaBarang.getText().toString().toUpperCase();
                        for (CustomListItem item: masterList){

                            if(item.getListItem1().toUpperCase().contains(keyword)) itemToAdd.add(item);
                        }
                        getListTable(itemToAdd);
                        iv.hideSoftKey(DetailOmsetPerNota.this);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table
    private void getListTable(List<CustomListItem> listItemCustomer){

        lvListOmset.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){

            OmsetPerCustomerTableAdapter arrayAdapterString;
            arrayAdapterString = new OmsetPerCustomerTableAdapter(DetailOmsetPerNota.this,listItemCustomer.size(), listItemCustomer, 3);
            lvListOmset.setAdapter(arrayAdapterString);
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
