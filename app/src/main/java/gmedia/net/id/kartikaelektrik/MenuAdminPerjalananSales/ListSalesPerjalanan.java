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
    private List<CustomListItem> listItem = new ArrayList<>(), masterlist = new ArrayList<>(), moreItem;
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

        listItem = new ArrayList<>();
        adapter = new ListSalesAdapter((Activity) context, listItem);
        lvSales.setAdapter(adapter);

        actvSales.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    String keyword = actvSales.getText().toString().toLowerCase();
                    List<CustomListItem> items = new ArrayList<>();
                    if(masterlist != null && masterlist.size() > 0){

                        for(CustomListItem item : masterlist){
                            if(item.getListItem2().toLowerCase().contains(keyword)){
                                items.add(item);
                            }
                        }

                        listItem.clear();
                        listItem.addAll(items);
                        adapter.notifyDataSetChanged();
                    }

                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
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

        ApiVolley restService = new ApiVolley(context, jBody, "GET", ServerURL.getSalesAdmin, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listItem.clear();
                            masterlist.clear();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    masterlist.add(new CustomListItem(
                                            jo.getString("nik"),
                                            jo.getString("nama"),
                                            jo.getString("jabatan")));

                                    listItem.add(new CustomListItem(
                                            jo.getString("nik"),
                                            jo.getString("nama"),
                                            jo.getString("jabatan")));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();

                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"ERROR");
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
