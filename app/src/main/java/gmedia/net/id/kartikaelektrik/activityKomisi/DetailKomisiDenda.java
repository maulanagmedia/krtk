package gmedia.net.id.kartikaelektrik.activityKomisi;

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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemTableAdapter;
import gmedia.net.id.kartikaelektrik.adapter.KomisiDenda.KomisiDendaTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.KomisiDenda;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailKomisiDenda extends AppCompatActivity {

    private List<KomisiDenda> masterList;
    private List<CustomListItem> autocompleteList, tableList;
    private String TAG = "DetailKomisiDenda";
    private ItemValidation iv = new ItemValidation();
    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvKomisi;
    private boolean komisiMode = false;
    private boolean firstLoad = true;
    private String urlGetData = "";
    private TextView tvTotal, tvPeriode;
    private String urlTampilkanKomisi;
    private String tanggalAwal, tanggalAkhir;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_komisi_denda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        urlTampilkanKomisi = getResources().getString(R.string.url_post_komisi_denda_by_range);
        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_pelanggan);
        lvKomisi = (ListView) findViewById(R.id.lv_list_komisi);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvPeriode = (TextView) findViewById(R.id.tv_periode);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            String jenis = bundle.getString("jenis");
            String jenisPembayaran = bundle.getString("jenisPembayaran");
            tanggalAwal = bundle.getString("tanggalawal");
            tanggalAkhir = bundle.getString("tanggalakhir");
            String dateFormat = getResources().getString(R.string.format_date);
            String dateFormatDisplay = getResources().getString(R.string.format_date_display);

            tvPeriode.setText(iv.ChangeFormatDateString(tanggalAwal, dateFormat, dateFormatDisplay) + " s/d " + iv.ChangeFormatDateString(tanggalAkhir, dateFormat, dateFormatDisplay));

            if(jenis.equals("KOMISI")){
                komisiMode = true;
                setTitle("Detail Komisi Pembayaran");
            }else{
                setTitle("Detail Denda Pembayaran");
            }

            getKomisiDendaData();
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getKomisiDendaData();
                }
            });

        }
    }

    private void getKomisiDendaData(){

        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("date1", tanggalAwal);
            jsonBody.put("date2", tanggalAkhir);
            if(komisiMode){
                jsonBody.put("flag", "DTK");
            }else{
                jsonBody.put("flag", "DD");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(this, jsonBody, "POST", urlTampilkanKomisi , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();
                            autocompleteList = new ArrayList<>();
                            tableList = new ArrayList<>();
                            Double total = Double.valueOf(0);

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseAPI.getJSONArray("response");

                                List<CustomListItem> cli = new ArrayList<>();
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    KomisiDenda komisiDenda = new KomisiDenda();
                                    komisiDenda.setNamaSales(jo.getString("sales"));
                                    komisiDenda.setNamaPelanggan(jo.getString("customer"));
                                    komisiDenda.setNoNota(jo.getString("nonota"));
                                    komisiDenda.setTanggal(jo.getString("tgl"));
                                    komisiDenda.setPiutang(jo.getString("piutang"));
                                    komisiDenda.setPotongan(jo.getString("Potongan"));
                                    komisiDenda.setJumlah(jo.getString("jumlah"));
                                    komisiDenda.setTanggalBayar(jo.getString("tglbayar"));
                                    komisiDenda.setBayar(jo.getString("bayar"));
                                    komisiDenda.setSelisihHari(jo.getString("selisihhari"));
                                    if(komisiMode){
                                        komisiDenda.setPersenKomisiDenda(jo.getString("persen_komisi"));
                                        komisiDenda.setNilaiKomisiDenda(jo.getString("komisi"));
                                        total += iv.parseNullDouble(jo.getString("komisi"));
                                    }else{
                                        komisiDenda.setPersenKomisiDenda(jo.getString("persen_denda"));
                                        komisiDenda.setNilaiKomisiDenda(jo.getString("Denda"));
                                        total += iv.parseNullDouble(jo.getString("Denda"));
                                    }
                                    komisiDenda.setPembayaran(jo.getString("pembayaran"));
                                    masterList.add(komisiDenda);

                                }


                                tvTotal.setText(iv.ChangeToRupiahFormat(total));

                                String jenisPembayaran = "";
                                Double lastTotalPembayaran = Double.valueOf(0);

                                int x = 0;
                                for(KomisiDenda komisiDenda: masterList){

                                    //Header
                                    if(!jenisPembayaran.equals(komisiDenda.getPembayaran())){
                                        autocompleteList.add(new CustomListItem(komisiDenda.getPembayaran(),""));
                                        tableList.add(new CustomListItem("HEADER", komisiDenda.getPembayaran(),"",""));
                                        jenisPembayaran = komisiDenda.getPembayaran();
                                    }

                                    //Mid value
                                    CustomListItem item = new CustomListItem(komisiDenda.getNoNota(),komisiDenda.getNamaPelanggan(), iv.ChangeToRupiahFormat(Float.parseFloat(komisiDenda.getNilaiKomisiDenda())), komisiDenda.getPembayaran());
                                    tableList.add(item);
                                    lastTotalPembayaran += iv.parseNullDouble(komisiDenda.getNilaiKomisiDenda());

                                    //footer
                                    if(x+1 < masterList.size()){
                                        KomisiDenda jo2 = masterList.get(x+1);
                                        String nextName = jo2.getPembayaran();
                                        if(!nextName.equals(komisiDenda.getPembayaran())){
                                            tableList.add(new CustomListItem("FOOTER", iv.ChangeToRupiahFormat(lastTotalPembayaran), komisiDenda.getPembayaran(),""));
                                            lastTotalPembayaran = Double.valueOf(0);
                                        }
                                    }else if(x == masterList.size() - 1){
                                        tableList.add(new CustomListItem("FOOTER", iv.ChangeToRupiahFormat(lastTotalPembayaran), komisiDenda.getPembayaran(),""));
                                    }
                                    x++;
                                }

                                getAutocompleteList(autocompleteList);
                                getTableList(tableList);
                            }else{
                                tvTotal.setText(iv.ChangeToRupiahFormat((double) 0));
                            }
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                        }catch (Exception e){
                            e.printStackTrace();
                            getAutocompleteList(null);
                            getTableList(null);
                            tvTotal.setText(iv.ChangeToRupiahFormat((double) 0));
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getAutocompleteList(null);
                        getTableList(null);
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    private void getAutocompleteList(List<CustomListItem> listItems) {

        actvNamaPelanggan.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailKomisiDenda.this,listItems.size(),listItems, "L");

            //set adapter to autocomplete
            actvNamaPelanggan.setAdapter(arrayAdapterString);*/

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvNamaPelanggan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNamaPelanggan.getText().length() <= 0){
                        getTableList(tableList);
                    }
                }
            });
        }

        actvNamaPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();


                // change table
                String jenisPembayaranSelected = cli.getListItem1();
                long totalPerCustomer = 0;

                items.add(new CustomListItem("HEADER", jenisPembayaranSelected, ""));

                for (CustomListItem item: tableList){

                    String jenisBayar = item.getListItem4();
                    if(jenisPembayaranSelected.trim().equals(jenisBayar.trim())){
                        items.add(item);
                    }
                }

                for (CustomListItem item1: tableList){

                    if(item1.getListItem1().trim().equals("FOOTER") && (jenisPembayaranSelected.trim().equals(item1.getListItem3()))){
                        items.add(item1);
                        break;
                    }
                }

                getTableList(items);
            }
        });

        actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();

                    String keyword = actvNamaPelanggan.getText().toString().toUpperCase();

                    for (CustomListItem item: tableList){

                        String jenisBayar = item.getListItem4().toUpperCase();

                        // Header
                        if(item.getListItem1().equals("HEADER") && item.getListItem2().toUpperCase().contains(keyword)){
                            items.add(item);
                        }

                        // Isi
                        if(!item.getListItem1().equals("HEADER") && !item.getListItem1().trim().equals("FOOTER") && jenisBayar.contains(keyword)){
                            items.add(item);
                        }

                        // footer
                        if(item.getListItem1().trim().equals("FOOTER") && (item.getListItem3().toUpperCase().contains(keyword))){
                            items.add(item);
                        }
                    }

                    getTableList(items);
                    iv.hideSoftKey(DetailKomisiDenda.this);
                    return true;
                }
                return false;
            }
        });

    }

    private void getTableList(List<CustomListItem> listItems) {

        lvKomisi.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            KomisiDendaTableAdapter arrayAdapter;
            arrayAdapter = new KomisiDendaTableAdapter(DetailKomisiDenda.this, listItems.size(), listItems);
            lvKomisi.setAdapter(arrayAdapter);

            lvKomisi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem1();

                    if(!key.equals("HEADER") && !key.equals("FOOTER")){
                        KomisiDenda selectedKomisiDenda = new KomisiDenda();
                        for(KomisiDenda komisiDenda: masterList){
                            if(komisiDenda.getNoNota().equals(key)){
                                selectedKomisiDenda = komisiDenda;
                            }
                        }

                        Intent intent = new Intent(DetailKomisiDenda.this, SubDetailKomisiDendaPembayaran.class);
                        Gson gson = new Gson();
                        intent.putExtra("list", gson.toJson(selectedKomisiDenda));
                        String jenis = "DENDA";
                        if(komisiMode) jenis = "KOMISI";
                        intent.putExtra("jenis", jenis);
                        startActivity(intent);
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
