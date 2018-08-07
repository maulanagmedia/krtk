package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter.ListLabaRugiAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class LabaRugiOmsetSetoran extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private List<OptionItem> listBulan;
    private Spinner spBulan;
    private EditText edtTahun;
    private LinearLayout llShow, llContainer;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private TextView tvTotal;
    private List<CustomListItem> masterList;
    private ListView lvListLabaRugi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laba_rugi_omset_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Laba Rugi Omset Setoran");
        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        edtTahun = (EditText) findViewById(R.id.edt_tahun);
        llShow = (LinearLayout) findViewById(R.id.ll_show);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        lvListLabaRugi = (ListView) findViewById(R.id.lv_list_laba_rugi);

        setDataBulan();

        initEvent();
    }

    private void initEvent() {

        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtTahun.getText().toString().isEmpty()){

                    edtTahun.setError("Tahun harap diisi");
                    edtTahun.requestFocus();
                    return;
                }else{
                    edtTahun.setError(null);
                }

                getData(edtTahun.getText().toString());
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtTahun.getText().toString().isEmpty()){

                    edtTahun.setError("Tahun harap diisi");
                    edtTahun.requestFocus();
                    return;
                }else{
                    edtTahun.setError(null);
                }

                getData(edtTahun.getText().toString());
            }
        });
    }

    private void getData(String year) {

        // Get All Customer
        iv.ProgressbarEvent(llContainer, pbLoading, btnRefresh,"SHOW");

        OptionItem item = (OptionItem) spBulan.getSelectedItem();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("date", year + item.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jsonBody, "POST", ServerURL.getLabaRugiOmsetSetoran, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();
                            double total = 0;

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(
                                            jo.getString("keterangan"),
                                            jo.getString("note"),
                                            jo.getString("total")
                                    ));

                                    total += iv.parseNullDouble(jo.getString("total"));
                                }
                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
                            getTable(masterList);
                            iv.ProgressbarEvent(llContainer, pbLoading, btnRefresh,"GONE");

                        }catch (Exception e){
                            getTable(null);
                            iv.ProgressbarEvent(llContainer, pbLoading, btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getTable(null);
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"ERROR");
                    }
                });
    }

    private void getTable(List<CustomListItem> listItems) {

        lvListLabaRugi.setAdapter(null);
        if(listItems != null && listItems.size() > 0){

            ListLabaRugiAdapter adapter = new ListLabaRugiAdapter((Activity) context, listItems);
            lvListLabaRugi.setAdapter(adapter);
        }
    }

    private void setDataBulan() {

        listBulan = new ArrayList<>();
        listBulan.add(new OptionItem("01", "Januari"));
        listBulan.add(new OptionItem("02", "Februari"));
        listBulan.add(new OptionItem("03", "Maret"));
        listBulan.add(new OptionItem("04", "April"));
        listBulan.add(new OptionItem("05", "Mei"));
        listBulan.add(new OptionItem("06", "Juni"));
        listBulan.add(new OptionItem("07", "Juli"));
        listBulan.add(new OptionItem("08", "Agustus"));
        listBulan.add(new OptionItem("09", "September"));
        listBulan.add(new OptionItem("10", "Oktober"));
        listBulan.add(new OptionItem("11", "Nopember"));
        listBulan.add(new OptionItem("12", "Desember"));

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listBulan);
        spBulan.setAdapter(adapter);
        String curMonth = iv.getCurrentDate("MM");
        String curYear = iv.getCurrentDate("yyyy");
        int x = 0;
        for(OptionItem item : listBulan) {
            if(item.getValue().equals(curMonth)){
                break;
            }
            x++;
        }
        spBulan.setSelection(x);
        edtTahun.setText(curYear);

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
