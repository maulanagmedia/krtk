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
import gmedia.net.id.kartikaelektrik.adapter.Piutang.PiutangPerNotaTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailPiutangPerNota extends AppCompatActivity {

    private String urlGetPiutang = "";
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private AutoCompleteTextView actvNoNota;
    private ListView lvListPiutang;
    private boolean firstLoad = true;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private ItemValidation iv = new ItemValidation();
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_piutang_per_nota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Detail Per Nota");

        urlGetPiutang = getResources().getString(R.string.url_get_all_piutang);
        actvNoNota = (AutoCompleteTextView) findViewById(R.id.actv_no_nota);
        lvListPiutang = (ListView) findViewById(R.id.lv_list_piutang);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        setListPiutangCustomerAutocomplete();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListPiutangCustomerAutocomplete();
            }
        });
    }

    private void setListPiutangCustomerAutocomplete() {

        // Get All Customer
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        final String dateFormat = getResources().getString(R.string.format_date);
        final String dateFormatDisplay = getResources().getString(R.string.format_date_display1);

        /*//TODO: Dummy
        masterList = new ArrayList<>();
        for(int i = 0; i < 6;i ++){
            masterList.add(new CustomListItem("Nota"+i, iv.ChangeToRupiahFormat(Long.parseLong("10")), iv.ChangeFormatDateString("2017-02-02", dateFormat, dateFormatDisplay), i + "0"));
        }

        autocompleteList = new ArrayList<CustomListItem>(masterList);
        tableList= new ArrayList<CustomListItem>(masterList);
        getListAutocomplete(autocompleteList);
        getListTable(tableList);
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");*/

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("flag","DA");
            jsonBody.put("kdcus","");
            jsonBody.put("tgltempo","");
            jsonBody.put("tgltempo2","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(DetailPiutangPerNota.this, jsonBody, "POST", urlGetPiutang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();
                            autocompleteList = new ArrayList<CustomListItem>();

                            String jenisPembayaran = "";
                            Double lastTotalPembayaran = Double.valueOf(0);

                            double total = 0;
                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    //Header
                                    if(!jenisPembayaran.equals(jo.getString("customer"))){
                                        autocompleteList.add(new CustomListItem(jo.getString("customer"),""));
                                        masterList.add(new CustomListItem("HEADER", jo.getString("customer"),"",""));
                                        jenisPembayaran = jo.getString("customer");
                                    }

                                    masterList.add(new CustomListItem(jo.getString("nonota"),jo.getString("customer"),iv.ChangeFormatDateString(jo.getString("tgl"), dateFormat, dateFormatDisplay),iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("piutang"))),iv.ChangeFormatDateString(jo.getString("tgltempo"), dateFormat, dateFormatDisplay),jo.getString("tempo")));
                                    lastTotalPembayaran += iv.parseNullDouble(jo.getString("piutang"));
                                    total += iv.parseNullDouble(jo.getString("piutang"));

                                    //footer
                                    if(i+1 < arrayJSON.length()){

                                        JSONObject jo2 = arrayJSON.getJSONObject(i+1);

                                        String nextName = jo2.getString("customer");
                                        if(!nextName.equals(jo.getString("customer"))){
                                            masterList.add(new CustomListItem("FOOTER", iv.ChangeToRupiahFormat(lastTotalPembayaran), jo.getString("customer"),""));
                                            lastTotalPembayaran = Double.valueOf(0);
                                        }
                                    }else if(i == arrayJSON.length() - 1){
                                        masterList.add(new CustomListItem("FOOTER", iv.ChangeToRupiahFormat(lastTotalPembayaran), jo.getString("customer"),""));
                                    }
                                }
                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));

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
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailPiutangPerNota.this,listItemCustomer.size(),listItemCustomer, "L");

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

                    //itemCustomer.add(new CustomListItem("HEADER", customer.getListItem1(), ""));

                    for (CustomListItem item: tableList){

                        String customerName = item.getListItem2();
                        if(customerName.trim().equals(customer.getListItem1())){
                            itemCustomer.add(item);
                        }
                    }

                    for (CustomListItem item1: tableList){

                        if(item1.getListItem1().trim().equals("FOOTER") && (customer.getListItem1().trim().equals(item1.getListItem3()))){
                            itemCustomer.add(item1);
                            break;
                        }
                    }

                    getListTable(itemCustomer);
                }
            });

            actvNoNota.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        // change table
                        List<CustomListItem> itemCustomer = new ArrayList<CustomListItem>();
                        String keyword = actvNoNota.getText().toString().toUpperCase();

                        for(CustomListItem item : tableList){

                            if(!item.getListItem1().trim().equals("FOOTER") && item.getListItem2().toUpperCase().contains(keyword)  && !item.getListItem1().trim().equals("FOOTER")){
                                itemCustomer.add(item);
                            }

                            if(item.getListItem1().trim().equals("FOOTER") && item.getListItem3().toUpperCase().contains(keyword)){
                                itemCustomer.add(item);
                            }
                        }

                        getListTable(itemCustomer);

                        iv.hideSoftKey(DetailPiutangPerNota.this);
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

            PiutangPerNotaTableAdapter arrayAdapterString;
            arrayAdapterString = new PiutangPerNotaTableAdapter(DetailPiutangPerNota.this,listItemCustomer.size(),listItemCustomer);
            lvListPiutang.setAdapter(arrayAdapterString);

            lvListPiutang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    if(!item.getListItem1().equals("HEADER") && !item.getListItem1().equals("FOOTER")){
                        Intent intent = new Intent(DetailPiutangPerNota.this, SubDetailPiutangJatuhTempo.class);
                        intent.putExtra("nobukti", item.getListItem1());
                        intent.putExtra("namacus", item.getListItem2());
                        intent.putExtra("total", item.getListItem4());
                        intent.putExtra("flag", true);
                        startActivity(intent);
                    }
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
