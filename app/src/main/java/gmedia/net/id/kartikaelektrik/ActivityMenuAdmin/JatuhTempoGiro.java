package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter.ListJatuhTempoGiroAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class JatuhTempoGiro extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private AutoCompleteTextView actvKeyword;
    private EditText edtAwal, edtAkhir;
    private LinearLayout llShow, llContainer;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private ListView lvGiro;
    private String formatDate = "", formatDateDisplay = "", tanggalAwal = "", tanggalAkhir = "", keyword = "";
    private List<CustomListItem> masterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jatuh_tempo_giro);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Jatuh Tempo Giro");
        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        actvKeyword = (AutoCompleteTextView) findViewById(R.id.actv_keyword);
        edtAwal = (EditText) findViewById(R.id.edt_awal);
        edtAkhir = (EditText) findViewById(R.id.edt_akhir);
        llShow = (LinearLayout) findViewById(R.id.ll_show);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        lvGiro = (ListView) findViewById(R.id.lv_giro);

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        edtAwal.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        edtAkhir.setText(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));

        keyword = "";
        iv.datePickerEvent(context, edtAwal, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        iv.datePickerEvent(context, edtAkhir, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));

        initEvent();

        getData();
    }

    private void getData() {

        // Get All Customer
        iv.ProgressbarEvent(llContainer, pbLoading, btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("tgl_awal", iv.ChangeFormatDateString(edtAwal.getText().toString(), formatDateDisplay, formatDate));
            jsonBody.put("tgl_akhir", iv.ChangeFormatDateString(edtAkhir.getText().toString(), formatDateDisplay, formatDate));
            jsonBody.put("keyword", actvKeyword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jsonBody, "POST", ServerURL.getGiroJatuhTempo, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(
                                            jo.getString("sales"),
                                            jo.getString("tanggal"),
                                            jo.getString("customer"),
                                            jo.getString("daribank"),
                                            jo.getString("kebank"),
                                            jo.getString("tgljatuhtempo"),
                                            jo.getString("umur")
                                            ));
                                }
                            }

                            getListCustomerTable(masterList);
                            iv.ProgressbarEvent(llContainer, pbLoading, btnRefresh,"GONE");

                        }catch (Exception e){
                            getListCustomerTable(null);
                            iv.ProgressbarEvent(llContainer, pbLoading, btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListCustomerTable(null);
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
                    }
                });
    }

    // method to show table customer
    private void getListCustomerTable(List<CustomListItem> listItem){

        lvGiro.setAdapter(null);

        if (listItem != null && listItem.size() > 0){

            ListJatuhTempoGiroAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListJatuhTempoGiroAdapter((Activity) context, R.layout.adapter_list_canvas_order, listItem);

            //set adapter to autocomplete
            lvGiro.setAdapter(arrayAdapterString);

            lvGiro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);


                }
            });
        }
    }

    private void initEvent() {

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData();
            }
        });

        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData();
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
