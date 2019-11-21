package gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter.CustomerHapusDendaAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class DetailApvHapusDenda extends AppCompatActivity {

    private Context context;
    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvListPelanggan;
    private ItemValidation iv = new ItemValidation();
    private CustomerHapusDendaAdapter adapter;
    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private List<CustomListItem> masterList = new ArrayList<>();
    private boolean firstLoad = true;
    private String currentString = "";
    private View footerList;
    private int start = 0, count = 10;
    private String keyword = "";
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_apv_hapus_denda);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Verifikasi Hapus Denda");

        context = this;
        initUI();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start = 0;
        masterList.clear();
        initData();
    }

    private void initUI() {

        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_nama_pelanggan);
        lvListPelanggan = (ListView) findViewById(R.id.lv_list_pelanggan);
        llLoadCusxtomer = (LinearLayout) findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvListPelanggan.addFooterView(footerList);
        adapter = new CustomerHapusDendaAdapter((Activity) context, masterList);
        lvListPelanggan.removeFooterView(footerList);
        lvListPelanggan.setAdapter(adapter);

        lvListPelanggan.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvListPelanggan.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvListPelanggan.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                masterList.clear();
                start = 0;
                initData();
            }
        });

    }

    private void initEvent() {

        actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvNamaPelanggan.getText().toString();
                    start = 0;
                    masterList.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        lvListPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailApvHapusDendaCustomer.class);
                intent.putExtra("kdcus", item.getListItem1());
                intent.putExtra("nama", item.getListItem2());
                intent.putExtra("nik", item.getListItem5());
                startActivity(intent);
            }
        });
    }

    public void initData(){

        // Get All Customer
        isLoading = true;
        if(start == 0) iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"SHOW");
        JSONObject jBody = new JSONObject();
        lvListPelanggan.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getPengajuanHapusDendaCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        lvListPelanggan.removeFooterView(footerList);
                        if(start == 0) {

                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");

                        }
                        String message = "";
                        isLoading = false;

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(
                                            jo.getString("kdcus"),
                                            jo.getString("customer"),
                                            jo.getString("alamat"),
                                            jo.getString("piutang"),
                                            jo.getString("nik")
                                    ));
                                }

                                int total = masterList.size();

                            }

                        }catch (Exception e){
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
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
