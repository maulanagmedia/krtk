package gmedia.net.id.kartikaelektrik.MenuAdminPerjalananSales;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
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
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.Adapter.ListSalesAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ListSalesPerjalanan extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private Context context;
    private AutoCompleteTextView actvSales;
    private ListView lvSales;
    private LinearLayout llContainer;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private int start = 0, count = 20;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private List<CustomListItem> listItem, moreItem;
    private boolean firstLoad = true;
    private ListSalesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sales_perjalanan);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pilih Sales");

        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        actvSales = (AutoCompleteTextView) findViewById(R.id.actv_sales);
        lvSales = (ListView) findViewById(R.id.lv_sales);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        getDataSales();
    }

    private void getDataSales() {

        start = 0;
        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"SHOW");
        isLoading = true;

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getListSales, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listItem = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listItem.add(new CustomListItem(
                                            jo.getString("nik"),
                                            jo.getString("nama"),
                                            jo.getString("alamat")));
                                }
                            }

                            getListAutocomplete(listItem);
                            getListTable(listItem);

                        }catch (Exception e){
                            e.printStackTrace();
                            getListAutocomplete(null);
                            getListTable(null);
                        }
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        getListAutocomplete(null);
                        getListTable(null);
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"ERROR");
                    }
                });
    }

    private void getListAutocomplete(List<CustomListItem> listItem) {

        actvSales.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvSales.getText().toString();
                    getDataSales();
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

        if(firstLoad){
            firstLoad = false;
            actvSales.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(editable.toString().length() <= 0) {

                        keyword = "";
                        getDataSales();
                    }
                }
            });
        }
    }

    private void getListTable(List<CustomListItem> listItems){

        lvSales.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            adapter = new ListSalesAdapter((Activity) context, listItems.size(), listItems);

            //set adapter to autocomplete
            lvSales.setAdapter(adapter);

            lvSales.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvSales.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvSales.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvSales.addFooterView(footerList);
                            start += count;
                            getMoreData();
                            //Log.i(TAG, "onScroll: last ");
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });

            lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                    Intent intent = new Intent(context, PerjalananSales.class);
                    intent.putExtra("nik", item.getListItem1());
                    intent.putExtra("nama", item.getListItem2());
                    startActivity(intent);
                }
            });
        }
    }

    private void getMoreData() {

        isLoading = true;
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getListSales, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        isLoading = false;
                        lvSales.removeFooterView(footerList);

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            moreItem = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    moreItem.add(new CustomListItem(jo.getString("nik"),
                                            jo.getString("nama"),
                                            jo.getString("alamat")));
                                }
                            }

                            if(adapter != null) adapter.addMoreData(moreItem);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        lvSales.removeFooterView(footerList);
                    }
                });
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
