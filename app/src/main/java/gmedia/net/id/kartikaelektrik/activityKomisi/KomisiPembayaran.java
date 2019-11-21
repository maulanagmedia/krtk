package gmedia.net.id.kartikaelektrik.activityKomisi;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
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

public class KomisiPembayaran extends AppCompatActivity {

    private String tanggalAwal;
    private String tanggalAkhir;
    private String urlTampilkanKomisi;
    private AutoCompleteTextView actvPembayaran;
    private ListView lvListKommisi;
    private List<KomisiDenda> masterListKomisi;
    private HashMap<String, List<KomisiDenda>> filteredKomisi;
    private List<CustomListItem> autocompleteList, tableList;
    private boolean firstLoad = true;
    private String TAG = "KomisiPembayaran";
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle;
    private String jenisTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komisi_pembayaran);

        initUI();
    }

    private void initUI() {

        setTitle("Komisi");
        urlTampilkanKomisi = getResources().getString(R.string.url_post_komisi_denda_by_range);
        Bundle bundle = getIntent().getExtras();
        actvPembayaran = (AutoCompleteTextView) findViewById(R.id.actv_pembayaran);
        lvListKommisi = (ListView) findViewById(R.id.lv_list_komisi_by_pembayaran);
        tvTitle = (TextView) findViewById(R.id.tv_rentang);

        String dateFormat = getResources().getString(R.string.format_date);
        String dateFormatDisplay = getResources().getString(R.string.format_date_display);

        if(bundle != null){
            tanggalAwal = bundle.getString("tanggalawal");
            tanggalAkhir = bundle.getString("tanggalakhir");
            jenisTanggal = bundle.getString("jenistanggal");

            tvTitle.setText(iv.ChangeFormatDateString(tanggalAwal, dateFormat, dateFormatDisplay) + " s/d " + iv.ChangeFormatDateString(tanggalAkhir, dateFormat, dateFormatDisplay));
            getListKomisiPembayaran();
        }
    }

    private void getListKomisiPembayaran() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("date1", tanggalAwal);
            jsonBody.put("date2", tanggalAkhir);
            jsonBody.put("flag", jenisTanggal);
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
                            masterListKomisi = new ArrayList<>();
                            autocompleteList = new ArrayList<>();
                            tableList = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseAPI.getJSONArray("response");

                                List<CustomListItem> cli = new ArrayList<>();
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    Float dendaFloat = iv.parseNullFloat(jo.getString("Denda"));
                                    if(dendaFloat == 0){
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
                                        komisiDenda.setPersenKomisiDenda(jo.getString("persen_komisi"));
                                        komisiDenda.setNilaiKomisiDenda(jo.getString("komisi"));
                                        komisiDenda.setPembayaran(jo.getString("pembayaran"));
                                        masterListKomisi.add(komisiDenda);
                                    }
                                }

                                FilterKomisiByPembayarang();
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

    private void FilterKomisiByPembayarang() {

        // Filter By Pembayaran
        filteredKomisi = new HashMap<String, List<KomisiDenda>>();
        for (KomisiDenda komisi : masterListKomisi) {
            String key  = komisi.getPembayaran();
            if(filteredKomisi.containsKey(key)){
                List<KomisiDenda> list = filteredKomisi.get(key);
                list.add(komisi);

            }else{
                List<KomisiDenda> list = new ArrayList<KomisiDenda>();
                list.add(komisi);
                filteredKomisi.put(key, list);
            }
        }

        // Autocomplete & Table item
        autocompleteList = new ArrayList<>();
        tableList = new ArrayList<>();
        for(String key: filteredKomisi.keySet()){
            List<KomisiDenda> list = filteredKomisi.get(key);
            float jmlKomisi = 0;
            for(KomisiDenda komiisi: list){
                jmlKomisi += iv.parseNullFloat(komiisi.getNilaiKomisiDenda());
            }

            CustomListItem cli = new CustomListItem(key, String.valueOf(list.size()) + " item", "Total " + iv.ChangeToRupiahFormat(jmlKomisi));
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
            arrayAdapterString = new CustomListItemAutocompleteAdapter(KomisiPembayaran.this,listItems.size(),listItems, "");

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

        lvListKommisi.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomListItemTableAdapter arrayAdapter;
            arrayAdapter = new CustomListItemTableAdapter(KomisiPembayaran.this, listItems.size(), listItems, "L");
            lvListKommisi.setAdapter(arrayAdapter);

            lvListKommisi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem1();
                    List<KomisiDenda> list = filteredKomisi.get(key);
                    Intent intent = new Intent(KomisiPembayaran.this, DetailKomisiDendaPembayaran.class);
                    Gson gson = new Gson();
                    intent.putExtra("list", gson.toJson(list));
                    intent.putExtra("jenis", "KOMISI");
                    startActivity(intent);
                }
            });
        }
    }
}
