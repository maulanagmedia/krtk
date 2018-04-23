package gmedia.net.id.kartikaelektrik.activityPiutang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.Piutang.ListPiutangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.Piutang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.Piutang.ListPiutangAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListPiutang extends AppCompatActivity {

    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvListPiutang;
    private String tanggalAwal = "",tanggalAkhir = "";
    private String urlGetListPiutang;
    private List<Piutang> masterListPiutang, listPiutangAutocomplete, listPiutangTable;
    private final String TAG = "List.Piutang";
    private String urlGetDetailPiutang;
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private TextView tvRentang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_piutang);

        initUI();
    }

    private void initUI() {

        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_nama_pelanggan);
        lvListPiutang = (ListView) findViewById(R.id.lv_list_piutang);
        tvRentang = (TextView) findViewById(R.id.tv_rentang);
        setTitle("Piutang / Tagihan");

        urlGetListPiutang = getResources().getString(R.string.url_get_piutang_by_range);
        urlGetDetailPiutang = getResources().getString(R.string.url_get_detail_piutang_by_id);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            String dateFormat = getResources().getString(R.string.format_date);
            String dateFormatDisplay = getResources().getString(R.string.format_date_display);

            tanggalAwal = bundle.getString("tanggalawal");
            tanggalAkhir = bundle.getString("tanggalakhir");
            tvRentang.setText(iv.ChangeFormatDateString(tanggalAwal, dateFormat, dateFormatDisplay) +" s/d "+iv.ChangeFormatDateString(tanggalAkhir, dateFormat, dateFormatDisplay));
        }

        setDataListPiutang();
    }

    private void setDataListPiutang() {

        // Get List Piutang
        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetListPiutang + tanggalAwal+"/"+tanggalAkhir, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            JSONObject responseJSON = responseAPI.getJSONObject("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listPiutangAutocomplete = new ArrayList<Piutang>();
                            listPiutangTable = new ArrayList<Piutang>();
                            masterListPiutang = new ArrayList<Piutang>();

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseJSON.getJSONArray("postpiutang");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);

                                    String statusPiutang = "";
                                    float selisihPiutang = (iv.parseNullFloat(jo.getString("jmlcn")) + iv.parseNullFloat(jo.getString("potongan")));
                                    float piutangFloat = iv.parseNullFloat(jo.getString("piutang"));
                                    if(selisihPiutang == piutangFloat){
                                        statusPiutang = "Lunas";
                                    }else if(selisihPiutang < piutangFloat){
                                        statusPiutang = "Belum Lunas";
                                    }
                                    masterListPiutang.add(new Piutang(Integer.parseInt(jo.getString("id")),jo.getString("nonota"),jo.getString("tgl"),jo.getString("kdcus"),jo.getString("nama"),jo.getString("piutang"), statusPiutang));
                                }

                                listPiutangAutocomplete  = new ArrayList<>(masterListPiutang);
                                listPiutangTable = new ArrayList<>(masterListPiutang);
                                getListPiutangAutocomplete(listPiutangAutocomplete);
                                getListPiutangTable(listPiutangTable);
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

    private void getListPiutangAutocomplete(List<Piutang> listItems) {

        actvNamaPelanggan.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            ListPiutangAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new ListPiutangAutocompleteAdapter(ListPiutang.this,listItems.size(),listItems);

            //set adapter to autocomplete
            actvNamaPelanggan.setAdapter(arrayAdapterString);

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
                        setDataListPiutang();
                    }
                }
            });
        }

        actvNamaPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Piutang piutang = (Piutang) adapterView.getItemAtPosition(i);
                List<Piutang> items = new ArrayList<Piutang>();
                items.add(piutang);
                getListPiutangTable(items);
            }
        });
    }

    private void getListPiutangTable(List<Piutang> listItems) {

        lvListPiutang.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            ListPiutangTableAdapter arrayAdapter;
            arrayAdapter = new ListPiutangTableAdapter(ListPiutang.this,listItems.size(),listItems);
            lvListPiutang.setAdapter(arrayAdapter);
            setTableEvent();
        }

    }

    private void setTableEvent(){

        lvListPiutang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Piutang piutang = (Piutang) adapterView.getItemAtPosition(i);
                getDetailPiutang(piutang);
            }
        });
    }

    private void getDetailPiutang(final Piutang piutang) {

        /*
        * Access the detail SO
        */

        Intent intent = new Intent(ListPiutang.this, DetailPiutang.class);
        intent.putExtra("id_piutang", piutang.getId());
        intent.putExtra("no_nota", piutang.getNoNota());
        intent.putExtra("total", piutang.getPiutang());
        startActivity(intent);

        /*// Get List Piutang
        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetDetailPiutang+String.valueOf(piutang.getId()), "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            JSONObject responseJSON = responseAPI.getJSONObject("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){
                                try {
                                    String tanggal = responseJSON.getJSONObject("postpiutang").getString("tgl");
                                    String tanggalTempo = responseJSON.getJSONObject("postpiutang").getString("tgltempo");

                                    FragmentManager fm = getSupportFragmentManager();
                                    final DetailPiutangDF detailFragment = DetailPiutangDF.newInstance(piutang.getNoNota(),piutang.getNamaCustomer(),tanggal,tanggalTempo,piutang.getPiutang());
                                    detailFragment.show(fm, "fr_piutang_detail");

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });*/
    }

}
