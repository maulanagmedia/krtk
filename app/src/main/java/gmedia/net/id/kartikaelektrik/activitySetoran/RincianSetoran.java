package gmedia.net.id.kartikaelektrik.activitySetoran;

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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPengeluaran.DetailPengeluaran;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.DetailSetoranAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class RincianSetoran extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ListView lvRincianSetoran;
    private LinearLayout llContainer;
    private Button btnRefresh;
    private String kodeBank = "", tanggalAwal ="" , tanggalAkhir = "";
    private ProgressBar pbLoading;
    private List<CustomListItem> listSetoran = new ArrayList<>(), moreList = new ArrayList<>();
    private TextView tvTotal, tvTotalPengeluaran;
    private AutoCompleteTextView actvKeyword;
    private boolean firstLoad = true;
    private LinearLayout llTotal, llTotalPengeluaran;
    private LinearLayout llFooter;
    private double totalPengeluaran = 0;
    private String khusus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Rincian Setoran");
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
        tvTotalPengeluaran = (TextView) findViewById(R.id.tv_total_pengeluaran);
        actvKeyword = (AutoCompleteTextView) findViewById(R.id.actv_keyword);
        llTotal = (LinearLayout) findViewById(R.id.ll_total);
        llTotalPengeluaran = (LinearLayout) findViewById(R.id.ll_total_pengeluaran);
        llFooter = (LinearLayout) findViewById(R.id.ll_footer_sub);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kodeBank = bundle.getString("kode_bank", "");
            tanggalAwal = bundle.getString("tgl_awal", "");
            tanggalAkhir = bundle.getString("tgl_akhir", "");
            khusus = bundle.getString("khusus", "");

            if(!kodeBank.equals("1009")){

                llTotalPengeluaran.setVisibility(View.GONE);
                llFooter.setWeightSum(1);
            }

            initEvent();
        }
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
                        setAdapter(listSetoran);
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

                        setAdapter(items);
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
            jBody.put("tgl_awal", tanggalAwal);
            jBody.put("tgl_akhir", tanggalAkhir);
            jBody.put("khusus", khusus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSetoranDetail, "", "", 0, new ApiVolley.VolleyCallback() {
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
                                    jo.getString("bank"),
                                    jo.getString("total"),
                                    jo.getString("tanggal"),
                                    jo.getString("daribank"),
                                    jo.getString("kode_bank"),
                                    jo.getString("kdcus"),
                                    jo.getString("nobukti")
                            ));

                            total += iv.parseNullDouble(jo.getString("total"));
                        }

                    }else{
                        onBackPressed();
                    }

                    //tvTotal.setText(iv.ChangeToRupiahFormat(total));
                    if(kodeBank.equals("1009")){

                        getDataSetoranPengeluaran();
                    }else{

                        setAdapter(listSetoran);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setAdapter(null);
                    llContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                llContainer.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setAdapter(null);
            }
        });
    }

    private void getDataSetoranPengeluaran() {

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kode_bank", "0000"); // akun pengeluaran
            jBody.put("tgl_awal", tanggalAwal);
            jBody.put("tgl_akhir", tanggalAkhir);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSetoranDetail, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    moreList = new ArrayList<>();
                    totalPengeluaran = 0;

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            moreList.add(new CustomListItem(
                                    jo.getString("id"),
                                    jo.getString("nama_customer"),
                                    jo.getString("bank"),
                                    jo.getString("total"),
                                    jo.getString("tanggal"),
                                    jo.getString("daribank"),
                                    jo.getString("kode_bank"),
                                    jo.getString("kdcus"),
                                    jo.getString("nobukti")
                            ));

                            totalPengeluaran += iv.parseNullDouble(jo.getString("total"));
                        }

                        listSetoran.addAll(moreList);

                    }

                    //tvTotal.setText(iv.ChangeToRupiahFormat(total));
                    setAdapter(listSetoran);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setAdapter(null);
                    llContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                llContainer.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setAdapter(null);
            }
        });
    }

    private void setAdapter(List<CustomListItem> listItems) {

        lvRincianSetoran.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            double total = 0;
            for(CustomListItem item : listItems){

                total += iv.parseNullDouble(item.getListItem4());
            }

            total -= totalPengeluaran;

            tvTotal.setText(iv.ChangeToRupiahFormat(total));
            tvTotalPengeluaran.setText(iv.ChangeToRupiahFormat(totalPengeluaran));
            DetailSetoranAdapter adapter = new DetailSetoranAdapter((Activity) context, listItems);
            lvRincianSetoran.setAdapter(adapter);

            lvRincianSetoran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    if(!item.getListItem1().isEmpty()){

                        /*Intent intent = new Intent(context, DetailRincianSetoran.class);
                        intent.putExtra("tanggal", item.getListItem5());
                        intent.putExtra("kode_bank", item.getListItem7());
                        intent.putExtra("kdcus", item.getListItem8());
                        intent.putExtra("customer", item.getListItem2());
                        intent.putExtra("bank", item.getListItem3());
                        startActivity(intent);*/

                        if(item.getListItem7().equals("0000")){

                            Intent intent = new Intent(context, DetailPengeluaran.class);
                            intent.putExtra("id", item.getListItem1());
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, DetailSetoranPerNota.class);
                            intent.putExtra("nobukti", item.getListItem9());
                            intent.putExtra("namacus", item.getListItem2());
                            startActivity(intent);
                        }

                    }else{
                        Toast.makeText(context, "Data merupakan mutasi", Toast.LENGTH_LONG).show();
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
