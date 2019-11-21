package gmedia.net.id.kartikaelektrik.activityPiutang;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailPiutang extends AppCompatActivity {

    private String noNota;
    private String idPiutang;
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvListDetailPiutang;
    private LinearLayout llLoading;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private String urlGetDetailPiutang;
    private TextView tvTotalPiutang;
    private List<CustomListItem> masterList, autocompleteList, tableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_piutang);

        initUI();
    }

    private void initUI() {

        urlGetDetailPiutang = getResources().getString(R.string.url_get_detail_piutang_by_id);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            noNota = bundle.getString("no_nota");
            setTitle("Detail Piutang "+noNota);
            idPiutang = String.valueOf(bundle.getInt("id_piutang"));
            String totalPiutang = iv.ChangeToRupiahFormat(Float.parseFloat(bundle.getString("total")));
            actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
            lvListDetailPiutang = (ListView) findViewById(R.id.lv_list_detail_piutang);
            tvTotalPiutang = (TextView) findViewById(R.id.tv_total_piutang);
            llLoading = (LinearLayout) findViewById(R.id.ll_load);
            pbLoading = (ProgressBar) findViewById(R.id.pb_load);
            btnRefresh = (Button) findViewById(R.id.btn_refresh);

            tvTotalPiutang.setText("Total Piutang "+totalPiutang);

            setDataListDetailPiutang();
        }
    }

    private void setDataListDetailPiutang() {

        /*
        * Access the detail Piutang
        */

        iv.ProgressbarEvent(llLoading, pbLoading, btnRefresh,"SHOW");
        // Get List Piutang
        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetDetailPiutang + idPiutang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            JSONObject responseJSON = responseAPI.getJSONObject("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){

                                // Get Detail
                                JSONArray jsonArray = responseJSON.getJSONArray("postpiutang");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    String diskonString = "Diskon ";
                                    if(jo.getString("diskon").length() > 0){
                                        diskonString = diskonString + jo.getString("diskon");
                                    }else{
                                        diskonString = "Tanpa diskon";
                                    }
                                    masterList.add(new CustomListItem(jo.getString("namabrg"),"Harga " + iv.ChangeToRupiahFormat(iv.parseNullFloat(jo.getString("harga"))),jo.getString("jumlah")+ " " + jo.getString("satuan"),diskonString, iv.ChangeToRupiahFormat(iv.parseNullFloat(jo.getString("total")))));
                                }

                                autocompleteList  = new ArrayList<>(masterList);
                                tableList = new ArrayList<>(masterList);
                                getListPiutangAutocomplete(autocompleteList);
                                getListPiutangTable(autocompleteList);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        iv.ProgressbarEvent(llLoading, pbLoading, btnRefresh,"GONE");
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoading, pbLoading, btnRefresh,"ERROR");
                    }
                });
    }

    private void getListPiutangAutocomplete(List<CustomListItem> listItems) {

        actvNamaBarang.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailPiutang.this, listItems.size(), listItems, "");

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvNamaBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNamaBarang.getText().length() <= 0){
                        setDataListDetailPiutang();
                    }
                }
            });
        }

        actvNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getListPiutangTable(items);
            }
        });
    }

    private void getListPiutangTable(List<CustomListItem> listItems) {

        lvListDetailPiutang.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomListItemTableAdapter arrayAdapter;
            arrayAdapter = new CustomListItemTableAdapter(DetailPiutang.this, listItems.size(), listItems, "5");
            lvListDetailPiutang.setAdapter(arrayAdapter);
        }

    }
}
