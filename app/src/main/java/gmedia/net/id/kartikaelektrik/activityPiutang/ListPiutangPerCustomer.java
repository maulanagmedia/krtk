package gmedia.net.id.kartikaelektrik.activityPiutang;

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
import gmedia.net.id.kartikaelektrik.adapter.Piutang.ListCustomerPiutangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class ListPiutangPerCustomer extends AppCompatActivity {

    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvListPelanggan;
    private ItemValidation iv = new ItemValidation();
    private String urlGetAllCustomer;
    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private List<CustomListItem> masterListCustomer , listCustomerAutocomplete, listCustomerTable;
    private boolean firstLoad = true;
    private TextView tvTotal;
    private final String TAG = "ListPiutang";
    public static String kode = "PC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_piutang_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI(){

        urlGetAllCustomer = getResources().getString(R.string.url_get_all_piutang);

        setTitle("Rekap Per Customer");
        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_nama_pelanggan);
        lvListPelanggan = (ListView) findViewById(R.id.lv_list_pelanggan);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        llLoadCusxtomer = (LinearLayout) findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        setListCustomerAutocomplete();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListCustomerAutocomplete();
            }
        });
    }

    public void setListCustomerAutocomplete(){

        // Get All Customer
        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"SHOW");
        final long[] total = {0};

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("flag","R");
            jsonBody.put("kdcus","");
            jsonBody.put("tgltempo","");
            jsonBody.put("tgltempo2","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(ListPiutangPerCustomer.this, jsonBody, "POST", urlGetAllCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterListCustomer = new ArrayList<CustomListItem>();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterListCustomer.add(new CustomListItem(jo.getString("customer"),jo.getString("kdcus"),iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("piutang"))),iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("max_piutang"))), jo.getString("alamat"), jo.getString("kota")));
                                    total[0] += Float.parseFloat(jo.getString("piutang"));
                                }
                            }

                            listCustomerAutocomplete = new ArrayList<CustomListItem>(masterListCustomer);
                            listCustomerTable = new ArrayList<CustomListItem>(masterListCustomer);
                            tvTotal.setText(iv.ChangeToRupiahFormat(total[0]));
                            getListCustomerAutocomplete(listCustomerAutocomplete);
                            getListCustomerTable(listCustomerTable);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListCustomerAutocomplete(null);
                            getListCustomerTable(null);
                            tvTotal.setText(iv.ChangeToRupiahFormat(total[0]));
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListCustomerAutocomplete(null);
                        getListCustomerTable(null);
                        tvTotal.setText(iv.ChangeToRupiahFormat(total[0]));
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");
                    }
                });
    }

    // method to show autocomplete item
    private void getListCustomerAutocomplete(final List<CustomListItem> listItemCustomer){

        actvNamaPelanggan.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(ListPiutangPerCustomer.this,listItemCustomer.size(),listItemCustomer, "L");

            //set adapter to autocomplete
            actvNamaPelanggan.setAdapter(arrayAdapterString);*/

            // Listener event on change autocomplete
            if(firstLoad){
                firstLoad = false;
                actvNamaPelanggan.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(actvNamaPelanggan.length() <= 0){
                            getListCustomerTable(listCustomerTable);
                        }
                    }
                });
            }

            actvNamaPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem customer = (CustomListItem) adapterView.getItemAtPosition(i);

                    // change table
                    List<CustomListItem> itemCustomer = new ArrayList<CustomListItem>();
                    itemCustomer.add(customer);
                    getListCustomerTable(itemCustomer);
                }
            });

            actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<>();
                        String keyword = actvNamaPelanggan.getText().toString().trim().toUpperCase();
                        for(CustomListItem item: masterListCustomer){

                            if(item.getListItem1().toUpperCase().contains(keyword)){
                                itemToAdd.add(item);
                            }
                        }

                        getListCustomerTable(itemToAdd);
                        iv.hideSoftKey(ListPiutangPerCustomer.this);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table customer
    private void getListCustomerTable(List<CustomListItem> listItemCustomer){

        lvListPelanggan.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){
            ListCustomerPiutangTableAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListCustomerPiutangTableAdapter(ListPiutangPerCustomer.this,listItemCustomer.size(),listItemCustomer);

            //set adapter to autocomplete
            lvListPelanggan.setAdapter(arrayAdapterString);

            lvListPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CustomListItem customer = (CustomListItem) parent.getItemAtPosition(position);

                    Intent intent = new Intent(ListPiutangPerCustomer.this, DetailListPiutangCustomer.class);
                    intent.putExtra("kode", kode);
                    intent.putExtra("kdcus", customer.getListItem2());
                    intent.putExtra("nama", customer.getListItem1());
                    intent.putExtra("alamat", customer.getListItem5() + ", "+ customer.getListItem6());
                    intent.putExtra("total", customer.getListItem3());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        actvNamaPelanggan.setText("");
        setListCustomerAutocomplete();
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
