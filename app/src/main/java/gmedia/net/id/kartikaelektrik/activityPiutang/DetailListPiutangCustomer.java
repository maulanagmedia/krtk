package gmedia.net.id.kartikaelektrik.activityPiutang;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
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
import gmedia.net.id.kartikaelektrik.adapter.Piutang.DetailCustomerPiutangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaTagihanPiutang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailListPiutangCustomer extends AppCompatActivity {

    private String kode = "";
    private Button btnRefresh;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private AutoCompleteTextView actvNoNota;
    private TextView tvNama;
    private TextView tvAlamat;
    private ListView lvListPiutang;
    private TextView tvTotal;
    private boolean firstLoad = true;
    private ItemValidation iv = new ItemValidation();
    private String urlGetPiutang = "";
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private String kdCus = "", namaCus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list_piutang_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        urlGetPiutang = getResources().getString(R.string.url_get_all_piutang);
        actvNoNota = (AutoCompleteTextView) findViewById(R.id.actv_no_nota);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvAlamat = (TextView) findViewById(R.id.tv_alamat);
        lvListPiutang = (ListView) findViewById(R.id.lv_list_piutang);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            try {
                kode = bundle.getString("kode");

                if(kode.equals(ListPiutangPerCustomer.kode)){

                    setTitle("Detail Piutang Customer");
                    namaCus = bundle.getString("nama");
                    String alamat = bundle.getString("alamat");
                    String total = bundle.getString("total");
                    kdCus  = bundle.getString("kdcus");

                    tvNama.setText(namaCus);
                    tvAlamat.setText(alamat);
                    tvTotal.setText(total);
                }else if(kode.equals(MenuUtamaTagihanPiutang.kodeJatuhTempo)){
                    setTitle("Piutang Jatuh Tempo");
                }

                setListPiutangCustomerAutocomplete();

                btnRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setListPiutangCustomerAutocomplete();
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setListPiutangCustomerAutocomplete() {

        // Get All Customer
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        final String dateFormat = getResources().getString(R.string.format_date);
        final String dateFormatDisplay = getResources().getString(R.string.format_date_display1);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("flag","DC");
            jsonBody.put("kdcus",kdCus);
            jsonBody.put("tgltempo","");
            jsonBody.put("tgltempo2","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(DetailListPiutangCustomer.this, jsonBody, "POST", urlGetPiutang, "", "", 0,
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
                                    masterList.add(new CustomListItem(
                                            jo.getString("nonota"),
                                            iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("piutang"))),
                                            iv.ChangeFormatDateString(jo.getString("tgltempo"), dateFormat, dateFormatDisplay),
                                            jo.getString("tempo"),
                                            iv.ChangeFormatDateString(jo.getString("tgl"), dateFormat, dateFormatDisplay)
                                    ));
                                }
                            }

                            autocompleteList = new ArrayList<CustomListItem>(masterList);
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
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailListPiutangCustomer.this,listItemCustomer.size(),listItemCustomer, "L");

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
                    CustomListItem customer = (CustomListItem) adapterView.getItemAtPosition(i);

                    // change table
                    List<CustomListItem> itemCustomer = new ArrayList<CustomListItem>();
                    itemCustomer.add(customer);
                    getListTable(itemCustomer);
                }
            });

            actvNoNota.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<>();
                        String keyword = actvNoNota.getText().toString().trim().toUpperCase();
                        for(CustomListItem item: masterList){

                            if(item.getListItem1().toUpperCase().contains(keyword)){
                                itemToAdd.add(item);
                            }
                        }

                        getListTable(itemToAdd);
                        iv.hideSoftKey(DetailListPiutangCustomer.this);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table
    private void getListTable(List<CustomListItem> listItemCustomer){

        lvListPiutang.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){

            DetailCustomerPiutangTableAdapter arrayAdapterString;
            arrayAdapterString = new DetailCustomerPiutangTableAdapter(DetailListPiutangCustomer.this,listItemCustomer.size(),listItemCustomer);
            lvListPiutang.setAdapter(arrayAdapterString);

            lvListPiutang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    Intent intent = new Intent(DetailListPiutangCustomer.this, SubDetailPiutangJatuhTempo.class);
                    intent.putExtra("nobukti", item.getListItem1());
                    intent.putExtra("namacus", namaCus);
                    intent.putExtra("total", item.getListItem2());
                    intent.putExtra("flag", true);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        actvNoNota.setText("");
        setListPiutangCustomerAutocomplete();
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
