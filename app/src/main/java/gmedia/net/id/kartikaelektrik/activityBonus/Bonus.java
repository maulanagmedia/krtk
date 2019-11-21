package gmedia.net.id.kartikaelektrik.activityBonus;

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
import android.widget.ListView;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Bonus extends AppCompatActivity {

    private String tanggalAwal;
    private String tanggalAkhir;
    private String urlTampilkanBonus;
    private AutoCompleteTextView actvPembayaran;
    private ListView lvListBonus;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private boolean firstLoad = true;
    private String TAG = "Bonus";
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle;
    private String formatDate = "", formatDateDisplay = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Bonus");
        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        urlTampilkanBonus = getResources().getString(R.string.url_get_bonus_header);
        Bundle bundle = getIntent().getExtras();
        actvPembayaran = (AutoCompleteTextView) findViewById(R.id.actv_bonus);
        lvListBonus = (ListView) findViewById(R.id.lv_list_bonus);
        tvTitle = (TextView) findViewById(R.id.tv_rentang);


        if(bundle != null){
            tanggalAwal = bundle.getString("tanggalawal");
            tanggalAkhir = bundle.getString("tanggalakhir");

            tvTitle.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay) + " s/d " + iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
            getListBonus();
        }
    }

    private void getListBonus() {

        String apiURL = urlTampilkanBonus + tanggalAwal+"/"+tanggalAkhir;
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", apiURL , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseAPI.getJSONArray("response");
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    CustomListItem cli = new CustomListItem();
                                    cli.setListItem1(jo.getString("keterangan"));
                                    cli.setListItem2(jo.getString("nobukti"));
                                    cli.setListItem3("Total Bonus " + iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("totalbonus"))));
                                    masterList.add(cli);
                                }
                            }

                            autocompleteList = new ArrayList<>(masterList);
                            tableList = new ArrayList<>(masterList);

                            getAutocompleteList(autocompleteList);
                            getTableList(tableList);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }

    private void getAutocompleteList(List<CustomListItem> listItems) {

        actvPembayaran.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(Bonus.this,listItems.size(),listItems, "C");

            //set adapter to autocomplete
            actvPembayaran.setAdapter(arrayAdapterString);*/

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvPembayaran.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvPembayaran.getText().length() <= 0){
                        getTableList(tableList);
                    }
                }
            });
        }

        actvPembayaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getTableList(items);
            }
        });

        actvPembayaran.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    String keyword = actvPembayaran.getText().toString().toUpperCase();
                    for(CustomListItem item: masterList){

                        if(item.getListItem1().toUpperCase().contains(keyword)) items.add(item);
                    }
                    getTableList(items);
                    iv.hideSoftKey(Bonus.this);
                    return true;
                }
                return false;
            }
        });
    }

    private void getTableList(List<CustomListItem> listItems) {

        lvListBonus.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomListItemTableAdapter arrayAdapter;
            arrayAdapter = new CustomListItemTableAdapter(Bonus.this, listItems.size(), listItems, "L");
            lvListBonus.setAdapter(arrayAdapter);

            lvListBonus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem2();
                    Intent intent = new Intent(Bonus.this, DetailBonus.class);
                    intent.putExtra("nobukti", key);
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
