package gmedia.net.id.kartikaelektrik.navMenuUtama;

/**
 * Created by indra on 20/12/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.OrderJSONHandler;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.adapter.CustomerOrder.ListSalesOrderAdapter;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuUtamaSalesOrder extends android.app.Fragment {

    private AutoCompleteTextView actSalesOrder;
    private ListView lvSalesOrder;
    private View layout;
    private Context context;
    private LinearLayout llLoadSO;
    private ProgressBar pbLoadSO;
    private Button btnRefresh;
    private ItemValidation iv = new ItemValidation();
    private final String TAG = "Menu.Utama";
    private boolean firstLoad = true;
    private List<CustomListItem> masterList;
    private String formatDate = "", formatDateDisplay = "";
    private String tanggalAwal = "", tanggalAkhir = "";
    private EditText edtAwal, edtAkhir;
    private LinearLayout llShow;
    private TextView tvTotal;
    private Spinner spnStatus;
    private List<OptionItem> listStatus = new ArrayList<>();
    private String selectedStatus = "";

    public MenuUtamaSalesOrder(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout 		= inflater.inflate(R.layout.menu_utama_sales_order,
                container, false);
        context = getActivity();
        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;
        initUI();
    }

    private void initUI(){

        // init all element
        actSalesOrder = (AutoCompleteTextView) layout.findViewById(R.id.autocomplete_SalesOrder);
        lvSalesOrder = (ListView) layout.findViewById(R.id.lv_1_order_salesOrder);
        llLoadSO = (LinearLayout) layout.findViewById(R.id.ll_load_so);
        pbLoadSO = (ProgressBar) layout.findViewById(R.id.pb_load_so);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh_so);
        spnStatus = (Spinner) layout.findViewById(R.id.spn_status);

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        edtAwal = (EditText) layout.findViewById(R.id.edt_awal);
        edtAkhir = (EditText) layout.findViewById(R.id.edt_akhir);
        llShow = (LinearLayout) layout.findViewById(R.id.ll_show);
        tvTotal = (TextView) layout.findViewById(R.id.tv_total);

        edtAwal.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        edtAkhir.setText(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));

        initValidation();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListAutocomplete();
            }
        });

        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getListAutocomplete();
            }
        });

        getDataStatus();
        getListAutocomplete();
    }

    private void getDataStatus() {

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", ServerURL.getStatus, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listStatus = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){

                                JSONArray ja = responseAPI.getJSONArray("response");
                                for(int i = 0; i < ja.length(); i++){

                                    JSONObject jo = ja.getJSONObject(i);
                                    listStatus.add(new OptionItem(
                                            jo.getString("kode")
                                            , jo.getString("status")
                                    ));
                                }
                            }

                        }catch (Exception e){

                            e.printStackTrace();
                            Toast.makeText(context, "Keslaahan saat memuat data status, harap refresh halaman", Toast.LENGTH_LONG).show();
                        }

                        setStatusAdapter(listStatus);
                        getListAutocomplete();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Keslaahan saat memuat data status, harap refresh halaman", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setStatusAdapter(List<OptionItem> listItem) {

        spnStatus.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spnStatus.setAdapter(adapter);

            spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OptionItem item = (OptionItem) parent.getItemAtPosition(position);
                    selectedStatus = item.getValue();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spnStatus.setSelection(0);
            selectedStatus = listStatus.get(0).getValue();
        }
    }

    private void initValidation() {

        iv.datePickerEvent(context, edtAwal, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        iv.datePickerEvent(context, edtAkhir, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
    }

    @Override
    public void onResume() {
        super.onResume();
        //getListAutocomplete();
    }

    public void getListAutocomplete(){
        // Get All No bukti
        String baseURL = ServerURL.getSO;
        iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"SHOW");

        actSalesOrder.setAdapter(null);
        lvSalesOrder.setAdapter(null);
        tanggalAwal = iv.ChangeFormatDateString(edtAwal.getText().toString(), formatDateDisplay, formatDate);
        tanggalAkhir = iv.ChangeFormatDateString(edtAkhir.getText().toString(), formatDateDisplay, formatDate);
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", baseURL + "barang/index?datestart="+tanggalAwal+"&dateend="+tanggalAkhir+"&status="+selectedStatus, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"GONE");

                        JSONObject responseAPI = new JSONObject();
                        String status = "0";
                        double total = 0;
                        tvTotal.setText(iv.ChangeToRupiahFormat(total));

                        try {

                            responseAPI = new JSONObject(result);
                            status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();

                            if(Integer.parseInt(status) == 200){
                                showJSON(result);

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                String[] itemString = new String[arrayJSON.length()];

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    itemString[i] = jo.getString("nobukti");

                                    masterList.add(new CustomListItem(
                                            jo.getString("nobukti"),
                                            jo.getString("nama"),
                                            jo.getString("alamat"),
                                            jo.getString("tgl"),
                                            jo.getString("total"),
                                            jo.getString("status"),
                                            jo.getString("kiriman_text"),
                                            jo.getString("nama_sales"),
                                            jo.getString("kiriman")));

                                    total += iv.parseNullDouble(jo.getString("total"));
                                }
                                getAutocomplete(itemString);

                                tvTotal.setText(iv.ChangeToRupiahFormat(total));
                                if(arrayJSON.length() > 0){

                                    if(!actSalesOrder.getText().toString().isEmpty()){
                                        searchNoBukti(actSalesOrder.getText().toString());
                                    }
                                }
                            }else{
                                showJSON(null);
                                getAutocomplete(null);
                            }

                        }catch (Exception e){

                            e.printStackTrace();
                            showJSON(null);
                            if(Integer.parseInt(status) != 404){
                                iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"ERROR");
                            }
                        }
                    }

                    @Override
                    public void onError(String result) {
                        showJSON(null);
                        iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"ERROR");
                    }
                });
    }

    // method to show autocomplete item
    private void getAutocomplete(String[] arrayString){

        actSalesOrder.setAdapter(null);

        /*ArrayAdapter<String> arrayAdapterString;

        //set adapter for autocomplete
        arrayAdapterString = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, arrayString);

        //set adapter to autocomplete
        actSalesOrder.setAdapter(arrayAdapterString);*/

        // Listener event on change autocomplete
        if(firstLoad){
            firstLoad = false;
            actSalesOrder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(actSalesOrder.getText().toString().equals("")){
                        lvSalesOrder.setAdapter(null);
                        getListAutocomplete();
                    }

                }
            });
        }

        /*actSalesOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onNoBuktiChange(adapterView.getItemAtPosition(i).toString());
            }
        });*/

        actSalesOrder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String keyword = actSalesOrder.getText().toString().toUpperCase().trim();
                    searchNoBukti(keyword);
                    iv.hideSoftKey(context);
                    return  true;
                }

                return false;
            }
        });
    }

    private void searchNoBukti(String keyword){

        int index = 0;
        keyword = keyword.toUpperCase();
        double total = 0;

        List<CustomListItem> bufferList = new ArrayList<>();
        for(CustomListItem item: masterList){
            if(item.getListItem1().toUpperCase().contains(keyword) || item.getListItem2().toUpperCase().contains(keyword)){
                bufferList.add(item);
                index ++;
            }
        }

        String[] stringNoBukti = new String[index];
        String[] namaString = new String[index];
        String[] alamatString = new String[index];
        String[] tanggals = new String[index];
        String[] totals = new String[index];
        String[] status = new String[index];
        String[] kirimanText = new String[index];
        String[] kiriman = new String[index];
        String[] namaSales = new String[index];

        int i = 0;
        for(CustomListItem add: bufferList){
            stringNoBukti[i] = add.getListItem1();
            namaString[i] = add.getListItem2();
            alamatString[i] = add.getListItem3();
            tanggals[i] = add.getListItem4();
            totals[i] = add.getListItem5();
            status[i] = add.getListItem6();
            kirimanText[i] = add.getListItem7();
            namaSales[i] = add.getListItem8();
            kiriman[i] = add.getListItem9();
            total += iv.parseNullDouble(add.getListItem5());

            i++;
        }

        tvTotal.setText(iv.ChangeToRupiahFormat(total));
        ListSalesOrderAdapter cl = new ListSalesOrderAdapter((Activity) context, stringNoBukti, namaString, alamatString, tanggals, totals, status, kirimanText, namaSales, kiriman);
        lvSalesOrder.setAdapter(cl);
    }

    /*private void onNoBuktiChange(final String noBukti){
        // Get All No bukti
        String baseURL = context.getResources().getString(R.string.url_get_so_detail_by_id) + noBukti;

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", baseURL, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            JSONObject jsonObject = responseAPI.getJSONObject("response");

                            try{

                                JSONObject joHeader = jsonObject.getJSONObject("so_header");

                                String[] stringNoBukti = new String[1];
                                stringNoBukti[0] = joHeader.getString("nobukti");
                                String[] namaString = new String[1];
                                namaString[0] = joHeader.getString("nama");
                                String[] alamatString = new String[1];
                                alamatString[0] = joHeader.getString("alamat");
                                String[] tanggals = new String[1];
                                tanggals[0] = joHeader.getString("tgl");
                                String[] totals = new String[1];
                                totals[0] = joHeader.getString("total");
                                String[] status = new String[1];
                                status[0] = joHeader.getString("status");
                                String[] kirimanText = new String[1];
                                kirimanText[0] = joHeader.getString("kiriman_text");
                                String[] namaSales = new String[1];
                                namaSales[0] = joHeader.getString("nama_sales");

                                ListSalesOrderAdapter cl = new ListSalesOrderAdapter((Activity) context, stringNoBukti, namaString, alamatString, tanggals, totals, status, kirimanText, namaSales);
                                lvSalesOrder.setAdapter(cl);

                            }catch (Exception e){
                                e.printStackTrace();

                            }

                        }catch (Exception e){
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }*/

    private void showJSON(String json){

        lvSalesOrder.setAdapter(null);
        if(json != null){
            OrderJSONHandler pj = new OrderJSONHandler(json,"all");
            pj.ParseOrderJSON();

            ListSalesOrderAdapter cl = new ListSalesOrderAdapter((Activity) context, pj.nobukti, pj.nama, pj.alamat, pj.tgl, pj.total, pj.status, pj.kirimanText, pj.namaSales, pj.kiriman);
            lvSalesOrder.setAdapter(cl);
        }
    }

}
