package gmedia.net.id.kartikaelektrik.activityPiutang;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
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
import android.widget.EditText;
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
import gmedia.net.id.kartikaelektrik.adapter.Piutang.DetailJatuhTempoPiutangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailPiutangJatuhTempo extends AppCompatActivity {

    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private TextInputLayout tilTanggalAwal, tilTanggalAkhir;
    private EditText edTanggalAwal, edtTanggalAkhir;
    private Button btnTampilkan;
    private AutoCompleteTextView actvNoNota;
    private boolean firstLoad = true;
    private ItemValidation iv = new ItemValidation();
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private ListView lvListPiutang;
    private String tanggalAwal = "", tanggalAkhir = "";
    private String urlGetPiutang = "";
    private String formatDate = "", formatDateDisplay = "";
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_piutang_jatuh_tempo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Piutang Jatuh Tempo");

        urlGetPiutang = getResources().getString(R.string.url_get_all_piutang);
        tilTanggalAwal = (TextInputLayout) findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        actvNoNota = (AutoCompleteTextView) findViewById(R.id.actv_no_nota);
        lvListPiutang = (ListView) findViewById(R.id.lv_list_piutang);
        btnTampilkan = (Button) findViewById(R.id.btn_tampilkan);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        edTanggalAwal.setText(iv.getToday(formatDateDisplay));
        edtTanggalAkhir.setText(iv.getToday(formatDateDisplay));

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTampilkanPiutang();
            }
        });

        initValidation();
    }

    private void initValidation() {

        iv.datePickerEvent(DetailPiutangJatuhTempo.this, edTanggalAwal, "RIGHT", formatDateDisplay);
        iv.datePickerEvent(DetailPiutangJatuhTempo.this, edtTanggalAkhir, "RIGHT", formatDateDisplay);

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTampilkanPiutang();
            }
        });
    }

    private void validateTampilkanPiutang(){

        /*// Mondatory
        if(!iv.mondatoryEdittext(tilTanggalAwal, edTanggalAwal, "Tanggal Awal Tidak Boleh Kosong")){
            return;
        }*/

        if(!iv.mondatoryEdittext(tilTanggalAkhir, edtTanggalAkhir, "Tanggal Akhir Tidak Boleh Kosong")){
            return;
        }

        /*if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal,"yyyy-MM-dd")){
            tilTanggalAkhir.setErrorEnabled(true);
            tilTanggalAkhir.setError("Tanggal Akhir Tidak Dapat Sebelum Tanggal Awal");
            edtTanggalAkhir.requestFocus();
            return;
        }else{
            tilTanggalAkhir.setError(null);
            tilTanggalAkhir.setErrorEnabled(false);
        }*/

        tanggalAwal = iv.ChangeFormatDateString(edTanggalAwal.getText().toString(), formatDateDisplay, formatDate);
        tanggalAkhir = iv.ChangeFormatDateString(edtTanggalAkhir.getText().toString(), formatDateDisplay, formatDate);

        setListPiutangAutocomplete();
    }

    private void setListPiutangAutocomplete() {

        // Get All Customer
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("flag","PT");
            jsonBody.put("kdcus","");
            jsonBody.put("tgltempo",tanggalAwal);
            jsonBody.put("tgltempo2",tanggalAkhir);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(DetailPiutangJatuhTempo.this, jsonBody, "POST", urlGetPiutang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();
                            autocompleteList = new ArrayList<CustomListItem>();
                            String namaCustomer = "";
                            long totalPerCustomer = 0;
                            double total = 0;

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    if(!namaCustomer.equals(jo.getString("customer"))){
                                        autocompleteList.add(new CustomListItem(jo.getString("customer"),""));
                                        masterList.add(new CustomListItem("HEADER", jo.getString("customer"),""));
                                        namaCustomer = jo.getString("customer");
                                    }

                                    masterList.add(new CustomListItem(jo.getString("nonota"), jo.getString("customer"), jo.getString("alamat") +", "+jo.getString("kota"),iv.ChangeToRupiahFormat(Float.parseFloat(jo.getString("piutang"))),iv.ChangeFormatDateString(jo.getString("tgltempo"), formatDate, formatDateDisplay),jo.getString("tempo"),jo.getString("selisih")));
                                    totalPerCustomer += Float.parseFloat(jo.getString("piutang"));
                                    total += Double.parseDouble(jo.getString("piutang"));

                                    if(i+1 < arrayJSON.length()){

                                        JSONObject jo2 = arrayJSON.getJSONObject(i+1);
                                        String nextName = jo2.getString("customer");
                                        if(!nextName.equals(namaCustomer)){
                                            masterList.add(new CustomListItem("FOOTER", iv.ChangeToRupiahFormat(totalPerCustomer), jo.getString("customer")));
                                            totalPerCustomer = 0;
                                        }
                                    }else if(i == arrayJSON.length() - 1){
                                        masterList.add(new CustomListItem("FOOTER", iv.ChangeToRupiahFormat(totalPerCustomer), jo.getString("customer")));
                                    }
                                }
                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
                            tableList= new ArrayList<CustomListItem>(masterList);
                            getListAutocomplete(autocompleteList);
                            getListTable(tableList);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListAutocomplete(null);
                            getListTable(null);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListAutocomplete(null);
                        getListTable(null);
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    // method to show autocomplete item
    private void getListAutocomplete(final List<CustomListItem> listItemCustomer){

        actvNoNota.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailPiutangJatuhTempo.this,listItemCustomer.size(),listItemCustomer, "L");

            //set adapter to autocomplete
            actvNoNota.setAdapter(arrayAdapterString);*/

            // Listener event on change autocomplete
            if(firstLoad){
                firstLoad = false;
                actvNoNota.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(actvNoNota.length() <= 0){
                            getListTable(tableList);
                        }
                    }
                });
            }

            actvNoNota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem customer = (CustomListItem) adapterView.getItemAtPosition(i);

                    String nama = customer.getListItem1();

                    // change table
                    long totalPerCustomer = 0;
                    List<CustomListItem> itemCustomer = new ArrayList<CustomListItem>();
                    //itemCustomer.add(new CustomListItem("HEADER", nama, ""));

                    for (CustomListItem item: tableList){

                        String namaCustomer = item.getListItem2();
                        if(nama.trim().equals(namaCustomer.trim())){
                            itemCustomer.add(item);
                        }
                    }

                    for (CustomListItem item1: tableList){

                        if(item1.getListItem1().trim().equals("FOOTER") && (nama.trim().equals(item1.getListItem3()))){
                            itemCustomer.add(item1);
                            break;
                        }
                    }

                    getListTable(itemCustomer);
                }
            });

            actvNoNota.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<>();
                        String keyword = actvNoNota.getText().toString().trim().toUpperCase();

                        for(CustomListItem item: masterList){

                            // Header & body
                            if(!item.getListItem1().trim().equals("FOOTER") && item.getListItem2().toUpperCase().contains(keyword)){
                                itemToAdd.add(item);
                            }

                            // Footer
                            if(item.getListItem1().trim().equals("FOOTER") && item.getListItem3().toUpperCase().contains(keyword)){
                                itemToAdd.add(item);
                            }
                        }

                        getListTable(itemToAdd);
                        iv.hideSoftKey(DetailPiutangJatuhTempo.this);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table
    private void getListTable(List<CustomListItem> listItemCustomer){

        lvListPiutang.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){

            DetailJatuhTempoPiutangTableAdapter arrayAdapterString;
            arrayAdapterString = new DetailJatuhTempoPiutangTableAdapter(DetailPiutangJatuhTempo.this,listItemCustomer.size(),listItemCustomer);
            lvListPiutang.setAdapter(arrayAdapterString);

            lvListPiutang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    if(!item.getListItem1().equals("HEADER") && !item.getListItem1().equals("FOOTER")){

                        Intent intent = new Intent(DetailPiutangJatuhTempo.this, SubDetailPiutangJatuhTempo.class);
                        intent.putExtra("nobukti", item.getListItem1());
                        intent.putExtra("namacus", item.getListItem2());
                        intent.putExtra("total", item.getListItem4());
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        actvNoNota.setText("");
        validateTampilkanPiutang();
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
