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
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.DetailItemBonus;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailBonus extends AppCompatActivity {

    private String noBukti;
    private String urlTampilkanBonus;
    private AutoCompleteTextView actvMerk;
    private ListView lvListBonus;
    private List<DetailItemBonus> masterBonus;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private boolean firstLoad = true;
    private String TAG = "Bonus";
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bonus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Bonus");
        urlTampilkanBonus = getResources().getString(R.string.url_get_bonus_detail);
        Bundle bundle = getIntent().getExtras();
        actvMerk = (AutoCompleteTextView) findViewById(R.id.actv_merk);
        lvListBonus = (ListView) findViewById(R.id.lv_list_bonus);
        tvTitle = (TextView) findViewById(R.id.tv_rentang);


        if(bundle != null){
            noBukti = bundle.getString("nobukti");
            getListBonus();
        }
    }

    private void getListBonus() {

        String apiURL = urlTampilkanBonus + noBukti;
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
                            masterBonus = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseAPI.getJSONArray("response");
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    CustomListItem cli = new CustomListItem();
                                    cli.setListItem1(jo.getString("merk"));
                                    String bonus = "Nilai Bonus ";
                                    if(jo.getString("flag").toUpperCase().equals("P")){
                                        bonus += (jo.getString("nilai").toString() + " %");
                                    }else if(jo.getString("flag").toUpperCase().equals("R")){
                                        bonus += iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("nilai")));
                                    }else{
                                        bonus += jo.getString("nilai");
                                    }
                                    cli.setListItem2(bonus);
                                    cli.setListItem3("Bonus " + iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("bonus"))));
                                    cli.setListItem4(jo.getString("id"));
                                    masterList.add(cli);
                                    masterBonus.add(new DetailItemBonus(jo.getString("id"),jo.getString("nobukti"),jo.getString("noprogram"),jo.getString("total"),jo.getString("bonus"),iv.parseNullString(jo.getString("flag")),jo.getString("nilai"),jo.getString("keterangan"),jo.getString("merk")));
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

        actvMerk.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailBonus.this,listItems.size(),listItems, "");

            //set adapter to autocomplete
            actvMerk.setAdapter(arrayAdapterString);*/

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvMerk.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvMerk.getText().length() <= 0){
                        getTableList(tableList);
                    }
                }
            });
        }

        actvMerk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getTableList(items);
            }
        });

        actvMerk.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    String keyword = actvMerk.getText().toString().toUpperCase();
                    for(CustomListItem item: masterList){

                        if(item.getListItem1().toUpperCase().contains(keyword)) items.add(item);
                    }
                    getTableList(items);
                    iv.hideSoftKey(DetailBonus.this);
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
            arrayAdapter = new CustomListItemTableAdapter(DetailBonus.this, listItems.size(), listItems, "");
            lvListBonus.setAdapter(arrayAdapter);

            lvListBonus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem4();
                    DetailItemBonus selectedBonus = new DetailItemBonus();
                    for(DetailItemBonus dib : masterBonus){
                        if(dib.getId().equals(key)){
                            selectedBonus = dib;
                        }
                    }
                    Intent intent = new Intent(DetailBonus.this, SubDetailBonus.class);
                    Gson gson = new Gson();
                    intent.putExtra("bonus", gson.toJson(selectedBonus));
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
