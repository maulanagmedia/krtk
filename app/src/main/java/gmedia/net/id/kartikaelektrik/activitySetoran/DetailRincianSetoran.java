package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.DetailRekapSetoranAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailRincianSetoran extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ListView lvRincianSetoran;
    private LinearLayout llContainer;
    private Button btnRefresh;
    private String kodeBank = "", tanggal = "", kdcus = "", customer = "", bank = "";
    private ProgressBar pbLoading;
    private List<CustomListItem> listSetoran = new ArrayList<>();
    private TextView tvTotal;
    private AutoCompleteTextView actvKeyword;
    private boolean firstLoad = true;
    private DetailRekapSetoranAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rincian_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Rincian Setoran");
        context = this;
        session = new SessionManager(context);
        firstLoad = true;

        initUI();
    }

    private void initUI() {

        lvRincianSetoran = (ListView) findViewById(R.id.lv_rincian_setoran);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        actvKeyword = (AutoCompleteTextView) findViewById(R.id.actv_keyword);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kodeBank = bundle.getString("kode_bank", "");
            tanggal = bundle.getString("tanggal", "");
            kdcus = bundle.getString("kdcus", "");
            customer = bundle.getString("customer", "");
            bank = bundle.getString("bank", "");

            setTitle(customer);
            getSupportActionBar().setSubtitle(bank);

            initEvent();
        }

        listSetoran = new ArrayList<>();
        adapter = new DetailRekapSetoranAdapter((Activity) context, listSetoran);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataSetoran();
    }

    private void initEvent() {

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llContainer.setVisibility(View.GONE);
                getDataSetoran();
            }
        });

        if(firstLoad){

            firstLoad = false;

            actvKeyword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if(s.length() == 0){
                        getDataSetoran();
                    }
                }
            });
        }

        actvKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    String keyword = actvKeyword.getText().toString().toLowerCase();
                    List<CustomListItem> items = new ArrayList<>();
                    if(listSetoran != null && listSetoran.size() > 0){

                        for(CustomListItem item : listSetoran){
                            if(item.getListItem2().toLowerCase().contains(keyword)){
                                items.add(item);
                            }
                        }

                        listSetoran.clear();
                        listSetoran.addAll(items);
                        adapter.notifyDataSetChanged();
                    }

                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });
    }

    private void getDataSetoran() {

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kode_bank", kodeBank);
            jBody.put("tanggal", tanggal);
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDetailRekapSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listSetoran = new ArrayList<>();
                    double total = 0;

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listSetoran.add(new CustomListItem(
                                    jo.getString("id"),
                                    jo.getString("nama_customer"),
                                    jo.getString("total")
                            ));

                            total += iv.parseNullDouble(jo.getString("total"));
                        }

                    }else{
                        onBackPressed();
                    }

                    tvTotal.setText(iv.ChangeToRupiahFormat(total));


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    llContainer.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                llContainer.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
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
