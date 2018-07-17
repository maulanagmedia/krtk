package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.ListCustomerSetoranAdapter;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class CustomerSetoran extends AppCompatActivity {

    private Context context;
    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvListPelanggan;
    private FloatingActionButton fbAddPelanggan;
    private ItemValidation iv = new ItemValidation();
    private String urlGetAllCustomer;
    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private List<Customer> masterListCustomer , listCustomerAutocomplete, listCustomerTable;
    private boolean firstLoad = true;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;

        setTitle("Setoran Pelanggan");

        initUI();
    }

    private void initUI() {

        urlGetAllCustomer = context.getResources().getString(R.string.url_get_all_customer);

        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_nama_pelanggan);
        lvListPelanggan = (ListView) findViewById(R.id.lv_list_pelanggan);
        fbAddPelanggan = (FloatingActionButton) findViewById(R.id.fbt_add_customer);
        llLoadCusxtomer = (LinearLayout) findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        setListCustomerAutocomplete();

        initEvent();

        actvNamaPelanggan.setText("");
        setListCustomerAutocomplete();
    }

    private void initEvent(){

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

        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", urlGetAllCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterListCustomer = new ArrayList<Customer>();
                            double total = 0;

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterListCustomer.add(new Customer(jo.getString("kdcus"),
                                            jo.getString("nama"),
                                            jo.getString("alamat"),
                                            jo.getString("kota"),
                                            iv.parseNullString(jo.getString("total_piutang")),
                                            jo.getString("max_piutang")));

                                    total += iv.parseNullDouble(iv.parseNullString(jo.getString("total_piutang")));
                                }

                                //int total = masterListCustomer.size();

                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
                            listCustomerAutocomplete = new ArrayList<Customer>(masterListCustomer);
                            listCustomerTable = new ArrayList<Customer>(masterListCustomer);
                            getListCustomerAutocomplete(listCustomerAutocomplete);
                            getListCustomerTable(listCustomerTable);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListCustomerAutocomplete(null);
                            getListCustomerTable(null);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListCustomerAutocomplete(null);
                        getListCustomerTable(null);
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");
                    }
                });
    }

    // method to show autocomplete item
    private void getListCustomerAutocomplete(final List<Customer> listItemCustomer){

        actvNamaPelanggan.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){
            /*CustomerAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomerAdapter(context,listItemCustomer.size(),listItemCustomer);

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
                    Customer customer = (Customer) adapterView.getItemAtPosition(i);
                    String kdCus = customer.getKodeCustomer();
                    String lastCustomerName = customer.getNamaCustomer().toString();
                    String customerAddress = customer.getAlamat();
                    String customerCity = customer.getKota();
                    String customerPiutang = iv.parseNullString(customer.getTotalPiutang());
                    String customerMaxPiutang = customer.getMaxPiutang();

                    // change table
                    List<Customer> itemCustomer = new ArrayList<Customer>();
                    itemCustomer.add(new Customer(kdCus,lastCustomerName,customerAddress, customerCity, customerPiutang, customerMaxPiutang));
                    getListCustomerTable(itemCustomer);
                }
            });

            actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        String keyword = actvNamaPelanggan.getText().toString().trim();
                        List<Customer> itemCustomer = new ArrayList<Customer>();
                        for(Customer customer: masterListCustomer){
                            String customerName = customer.getNamaCustomer().toString();
                            if(customerName.toUpperCase().contains(keyword.toUpperCase())){
                                String kdCustomer = customer.getKodeCustomer();
                                String lastCustomerName = customer.getNamaCustomer().toString();
                                String customerAddress = customer.getAlamat();
                                String customerCity = customer.getKota();
                                String customerPiutang = iv.parseNullString(customer.getTotalPiutang());
                                String customerMaxPiutang = customer.getMaxPiutang();
                                itemCustomer.add(new Customer(kdCustomer,lastCustomerName,customerAddress, customerCity, customerPiutang, customerMaxPiutang));
                            }
                        }
                        getListCustomerTable(itemCustomer);
                        iv.hideSoftKey(context);
                        return  true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table customer
    private void getListCustomerTable(List<Customer> listItemCustomer){

        lvListPelanggan.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){
            ListCustomerSetoranAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListCustomerSetoranAdapter((Activity) context,listItemCustomer.size(),listItemCustomer);

            //set adapter to autocomplete
            lvListPelanggan.setAdapter(arrayAdapterString);

            lvListPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Customer item = (Customer) parent.getItemAtPosition(position);
                    Intent intent = new Intent(context, DetailFormSetoran.class);
                    intent.putExtra("kdcus", item.getKodeCustomer());
                    intent.putExtra("namacus", item.getNamaCustomer());
                    startActivity(intent);
                    finish();
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
