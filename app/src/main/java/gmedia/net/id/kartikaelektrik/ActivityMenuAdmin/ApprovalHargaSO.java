package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin;

import android.app.Activity;
import android.content.Context;
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
import gmedia.net.id.kartikaelektrik.activityPermintaanHargaOrder.DetailPermintaanHargaOrder;
import gmedia.net.id.kartikaelektrik.adapter.NeedApproval.CustomNeedApprovalTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ApprovalHargaSO extends AppCompatActivity {

    private Context context;
    private AutoCompleteTextView actvNoSO;
    private ListView lvListSO;
    private ItemValidation iv = new ItemValidation();
    private LinearLayout llLoadSO;
    private ProgressBar pbLoadSO;
    private Button btnRefresh;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private boolean firstLoad = true;
    private String urlGetSOPermintaan;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_harga_so);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Approval Harga SO");
        context = this;

        initUI();
    }

    private void initUI() {

        urlGetSOPermintaan = context.getResources().getString(R.string.url_get_so_permintaan_harga);
        actvNoSO = (AutoCompleteTextView) findViewById(R.id.actv_no_so);
        lvListSO = (ListView) findViewById(R.id.lv_list_so);
        llLoadSO = (LinearLayout) findViewById(R.id.ll_load_so);
        pbLoadSO = (ProgressBar) findViewById(R.id.pb_load_so);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        session = new SessionManager(context);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getDataList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            getDataList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDataList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDataList() throws JSONException {

        iv.ProgressbarEvent(llLoadSO, pbLoadSO, btnRefresh, "SHOW");
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", urlGetSOPermintaan , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            lvListSO.setAdapter(null);
                            actvNoSO.setAdapter(null);
                            masterList = new ArrayList<>();
                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    CustomListItem cli = new CustomListItem();
                                    cli.setListItem1(jo.getString("nobukti"));
                                    cli.setListItem2(jo.getString("nama") + " (" + jo.getString("alamat")+")");
                                    String formatDate = context.getResources().getString(R.string.format_date);
                                    String formatDisplayDate = context.getResources().getString(R.string.format_date_display);
                                    cli.setListItem3(iv.ChangeFormatDateString(jo.getString("tgl"), formatDate, formatDisplayDate));
                                    cli.setListItem4(iv.ChangeToRupiahFormat(iv.parseNullFloat(jo.getString("total"))));
                                    cli.setListItem5(jo.getString("nama"));
                                    cli.setListItem6(jo.getString("status"));
                                    masterList.add(cli);
                                }
                            }

                            autocompleteList = new ArrayList<>(masterList);
                            tableList = new ArrayList<>(masterList);

                            iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh, "GONE");

                            getAutocomplete(autocompleteList);
                            getTable(tableList);

                        }catch (Exception e){
                            e.printStackTrace();
                            getAutocomplete(null);
                            getTable(null);
                            iv.ProgressbarEvent(llLoadSO, pbLoadSO, btnRefresh, "GONE");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoadSO, pbLoadSO, btnRefresh, "ERROR");
                    }
                });
    }

    private void getAutocomplete(List<CustomListItem> listItems) {

        actvNoSO.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(context, listItems.size(),listItems, "");

            //set adapter to autocomplete
            actvNoSO.setAdapter(arrayAdapterString);*/

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvNoSO.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNoSO.getText().length() <= 0){
                        getTable(tableList);
                    }
                }
            });
        }

        actvNoSO.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getTable(items);
            }
        });

        actvNoSO.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    String keyword = actvNoSO.getText().toString().trim().toUpperCase();

                    for(CustomListItem item: masterList){

                        if(item.getListItem1().toUpperCase().contains(keyword) ||item.getListItem5().toUpperCase().contains(keyword)){
                            items.add(item);
                        }
                    }

                    getTable(items);
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });
    }

    private void getTable(List<CustomListItem> listItems) {

        lvListSO.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomNeedApprovalTableAdapter arrayAdapter;
            arrayAdapter = new CustomNeedApprovalTableAdapter((Activity) context, listItems.size(), listItems);
            lvListSO.setAdapter(arrayAdapter);

            lvListSO.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem1();
                    Intent intent = new Intent(context, DetailPermintaanHargaOrder.class);
                    intent.putExtra("nobukti", key);
                    intent.putExtra("namapelanggan", cli.getListItem5());
                    intent.putExtra("namaalamat", cli.getListItem2());
                    context.startActivity(intent);
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
