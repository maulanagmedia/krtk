package gmedia.net.id.kartikaelektrik.activityOmsetPenjualan;

import android.content.Intent;
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

public class DetailOmsetCustomer extends AppCompatActivity {

    private AutoCompleteTextView actvNoNota;
    private TextView tvNamaCustomer;
    private ListView lvListOmset;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private String kdCus = "", tanggalAwal = "", tanggalAkhir = "";
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private String urlGetOmset = "", namaCustomer = "";
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_omset_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Detail Omset Customer");

        urlGetOmset = getResources().getString(R.string.url_get_omset);
        actvNoNota = (AutoCompleteTextView) findViewById(R.id.actv_no_nota);
        tvNamaCustomer = (TextView) findViewById(R.id.tv_nama);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        lvListOmset = (ListView) findViewById(R.id.lv_list_omset);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            namaCustomer = bundle.getString("nama");
            tvNamaCustomer.setText(namaCustomer);
            tvTotal.setText(bundle.getString("total"));
            kdCus  = bundle.getString("kdcus");
            tanggalAwal = bundle.getString("start");
            tanggalAkhir = bundle.getString("end");

            setListAutocomplete();

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
            jsonBody.put("flag","DN");
            jsonBody.put("kdcus",kdCus);
            jsonBody.put("tgl",tanggalAwal);
            jsonBody.put("tgl2",tanggalAkhir);
            jsonBody.put("nonota","");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(DetailOmsetCustomer.this, jsonBody, "POST", urlGetOmset, "", "", 0,
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

                                    masterList.add(new CustomListItem(jo.getString("nonota"), iv.ChangeFormatDateString(jo.getString("tgl"), dateFormat, dateFormatDisplay), jo.getString("alamat") + ", " + jo.getString("kota"), iv.ChangeFormatDateString(jo.getString("tgltempo"), dateFormat, dateFormatDisplay), iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("piutang"))), jo.getString("tempo")));

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

        actvNoNota.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailOmsetCustomer.this,listItemCustomer.size(),listItemCustomer, "L");

            //set adapter to autocomplete
            actvNoNota.setAdapter(arrayAdapterString);*/

            // Listener event on change autocomplete
            if(firstLoad){
                firstLoad = false;
                actvNoNota.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(actvNoNota.length() <= 0){
                            getListTable(tableList);
                        }
                    }
                });
            }

            actvNoNota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                    List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                    itemToAdd.add(item);
                    getListTable(itemToAdd);
                }
            });

            actvNoNota.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        String keyword = actvNoNota.getText().toString().toUpperCase();
                        for(CustomListItem item: masterList){

                            if(item.getListItem1().toUpperCase().contains(keyword)) itemToAdd.add(item);

                        }
                        getListTable(itemToAdd);
                        iv.hideSoftKey(DetailOmsetCustomer.this);
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
            arrayAdapterString = new OmsetPerCustomerTableAdapter(DetailOmsetCustomer.this,listItemCustomer.size(), listItemCustomer, 2);
            lvListOmset.setAdapter(arrayAdapterString);

            lvListOmset.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);
                    String noNota = item.getListItem1();

                    Intent intent = new Intent(DetailOmsetCustomer.this, DetailOmsetPerNota.class);
                    intent.putExtra("nonota", noNota);
                    intent.putExtra("nama", namaCustomer);
                    intent.putExtra("kdcus", kdCus);
                    intent.putExtra("start", tanggalAwal);
                    intent.putExtra("end", tanggalAkhir);

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
