package gmedia.net.id.kartikaelektrik.activityHistoryPenjualanBarang;

import android.app.Activity;
import android.content.Context;
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
import gmedia.net.id.kartikaelektrik.activityInfoStok.DetailStokBarang;
import gmedia.net.id.kartikaelektrik.adapter.BarangTakLaku.ListBarangTakLakuAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class BarangPalingLaku extends AppCompatActivity {

    private static Context context;
    private ItemValidation iv = new ItemValidation();
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvBarang;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private List<CustomListItem> listBarang;
    private boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_paling_laku);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Barang Paling Laku");

        context = this;
        initUI();
    }

    private void initUI() {

        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        firstLoad = true;
        getDataBarangTakLaku();
    }

    private void getDataBarangTakLaku() {

        // Get All Customer
        iv.ProgressbarEvent(llLoad,pbLoad, btnRefresh,"SHOW");

        String formatDate = context.getResources().getString(R.string.format_date);
        String tgl = iv.sumDate(iv.getCurrentDate(formatDate), -90, formatDate);
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("tgl", tgl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(context, jsonBody, "POST", ServerURL.getBarangPalingLaku, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listBarang = new ArrayList<CustomListItem>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listBarang.add(new CustomListItem(
                                            jo.getString("kodebrg"),
                                            jo.getString("namabrg"),
                                            jo.getString("hargabeli"),
                                            jo.getString("hargajual"),
                                            jo.getString("sisa"),
                                            jo.getString("satuan")));
                                }
                            }

                            getListCustomerAutocomplete(listBarang);
                            getListTable(listBarang);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListCustomerAutocomplete(null);
                            getListTable(null);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListCustomerAutocomplete(null);
                        getListTable(null);
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    private void getListTable(List<CustomListItem> listItems) {

        lvBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            ListBarangTakLakuAdapter arrayAdapterString = new ListBarangTakLakuAdapter((Activity) context, listItems);

            //set adapter to autocomplete
            lvBarang.setAdapter(arrayAdapterString);

            lvBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomListItem selectedItem = (CustomListItem) adapterView.getItemAtPosition(i);

                    Intent intent = new Intent(context, DetailStokBarang.class);
                    intent.putExtra("id", selectedItem.getListItem1());
                    intent.putExtra("nama", selectedItem.getListItem2());
                    ((Activity) context).startActivity(intent);
                }
            });
        }
    }

    private void getListCustomerAutocomplete(final List<CustomListItem> listItem){

        actvNamaBarang.setAdapter(null);

        if(listItem != null && listItem.size() > 0){
            /*CustomerAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomerAdapter(context,listItemCustomer.size(),listItemCustomer);

            //set adapter to autocomplete
            actvNamaPelanggan.setAdapter(arrayAdapterString);*/

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
                            getListTable(listItem);
                        }
                    }
                });
            }

            actvNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem selected = (CustomListItem) adapterView.getItemAtPosition(i);

                    // change table
                    List<CustomListItem> newList = new ArrayList<CustomListItem>();
                    newList.add(selected);
                    getListTable(newList);
                }
            });

            actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        String keyword = actvNamaBarang.getText().toString().trim();
                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        for(CustomListItem item: listBarang){
                            String nama = item.getListItem2().toString();
                            if(nama.toUpperCase().contains(keyword.toUpperCase())){
                                itemToAdd.add(item);
                            }
                        }
                        getListTable(itemToAdd);
                        iv.hideSoftKey(context);
                        return  true;
                    }
                    return false;
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
