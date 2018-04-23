package gmedia.net.id.kartikaelektrik.activityDenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityKomisi.DetailKomisiDendaPembayaran;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.model.KomisiDenda;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DendaPembayaran extends AppCompatActivity {

    private String tanggalAwal;
    private String tanggalAkhir;
    private String urlTampilkanDenda;
    private AutoCompleteTextView actvPembayaran;
    private ListView lvListDenda;
    private List<KomisiDenda> masterListDenda;
    private HashMap<String, List<KomisiDenda>> filteredHashDenda;
    private List<CustomListItem> autocompleteList, tableList;
    private boolean firstLoad = true;
    private String TAG = "DendaPembayaran";
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle;
    private String jenisTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denda_pembayaran);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        initUI();
    }

    private void initUI() {

        setTitle("Denda");
        urlTampilkanDenda = getResources().getString(R.string.url_post_komisi_denda_by_range);
        Bundle bundle = getIntent().getExtras();
        actvPembayaran = (AutoCompleteTextView) findViewById(R.id.actv_pembayaran);
        lvListDenda = (ListView) findViewById(R.id.lv_list_denda_by_pembayaran);
        tvTitle = (TextView) findViewById(R.id.tv_rentang);

        String dateFormat = getResources().getString(R.string.format_date);
        String dateFormatDisplay = getResources().getString(R.string.format_date_display);

        if(bundle != null){
            tanggalAwal = bundle.getString("tanggalawal");
            tanggalAkhir = bundle.getString("tanggalakhir");
            jenisTanggal = bundle.getString("jenistanggal");

            tvTitle.setText(iv.ChangeFormatDateString(tanggalAwal, dateFormat, dateFormatDisplay) + " s/d " + iv.ChangeFormatDateString(tanggalAkhir, dateFormat, dateFormatDisplay));
            getListDendaPembayaran();
        }
    }

    private void getListDendaPembayaran() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("date1", tanggalAwal);
            jsonBody.put("date2", tanggalAkhir);
            jsonBody.put("flag", jenisTanggal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(this, jsonBody, "POST", urlTampilkanDenda , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterListDenda = new ArrayList<>();
                            autocompleteList = new ArrayList<>();
                            tableList = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseAPI.getJSONArray("response");

                                List<CustomListItem> cli = new ArrayList<>();
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    Float dendaFloat = iv.parseNullFloat(jo.getString("Denda"));
                                    if(dendaFloat > 0){
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
                                        komisiDenda.setPersenKomisiDenda(jo.getString("persen_denda"));
                                        komisiDenda.setNilaiKomisiDenda(jo.getString("Denda"));
                                        komisiDenda.setPembayaran(jo.getString("pembayaran"));
                                        masterListDenda.add(komisiDenda);
                                    }
                                }
                                FilterDendaByPembayaran();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }

    private void FilterDendaByPembayaran() {

        // Filter By Pembayaran
        filteredHashDenda = new HashMap<String, List<KomisiDenda>>();
        for (KomisiDenda denda : masterListDenda) {
            String key  = denda.getPembayaran();
            if(filteredHashDenda.containsKey(key)){
                List<KomisiDenda> list = filteredHashDenda.get(key);
                list.add(denda);

            }else{
                List<KomisiDenda> list = new ArrayList<KomisiDenda>();
                list.add(denda);
                filteredHashDenda.put(key, list);
            }
        }

        // Autocomplete & Table item
        autocompleteList = new ArrayList<>();
        tableList = new ArrayList<>();
        for(String key: filteredHashDenda.keySet()){
            List<KomisiDenda> list = filteredHashDenda.get(key);
            float jmlDenda = 0;
            for(KomisiDenda denda: list){
                jmlDenda += iv.parseNullFloat(denda.getNilaiKomisiDenda());
            }

            CustomListItem cli = new CustomListItem(key, String.valueOf(list.size()) + " item", "Total " + iv.ChangeToRupiahFormat(jmlDenda));
            autocompleteList.add(cli);
            tableList.add(cli);
        }

        getAutocompleteList(autocompleteList);
        getTableList(tableList);
    }

    private void getAutocompleteList(List<CustomListItem> listItems) {

        actvPembayaran.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DendaPembayaran.this,listItems.size(),listItems, "");

            //set adapter to autocomplete
            actvPembayaran.setAdapter(arrayAdapterString);

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvPembayaran.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvPembayaran.getText().length() <= 0){
                        getTableList(tableList);
                    }
                }
            });
        }

        actvPembayaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getTableList(items);
            }
        });
    }

    private void getTableList(List<CustomListItem> listItems) {

        lvListDenda.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomListItemTableAdapter arrayAdapter;
            arrayAdapter = new CustomListItemTableAdapter(DendaPembayaran.this, listItems.size(), listItems, "L");
            lvListDenda.setAdapter(arrayAdapter);

            lvListDenda.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem1();
                    List<KomisiDenda> list = filteredHashDenda.get(key);
                    Intent intent = new Intent(DendaPembayaran.this, DetailKomisiDendaPembayaran.class);
                    Gson gson = new Gson();
                    intent.putExtra("list", gson.toJson(list));
                    intent.putExtra("jenis", "DENDA");
                    startActivity(intent);
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
