package gmedia.net.id.kartikaelektrik.navMenuUtama;

/**
 * Created by indra on 20/12/2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.AddNewCanvasOrder;
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.DetailBarangCanvas;
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.DetailEntryBarangCanvas;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.CustomerAdapter;
import gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas.ListCanvasCustomerTableAdapter;
import gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas.ListCanvasOrderTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class MenuUtamaEntryCanvas extends Fragment {

    private View layout;
    private Context context;
    private AutoCompleteTextView actvNoNota;
    private ListView lvListCanvas;
    private ItemValidation iv = new ItemValidation();
    private String urlGetCanvasOrder;
    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private boolean firstLoad = true;
    private FloatingActionButton fabAddOrder;
    private EditText edtAwal, edtAkhir;
    private LinearLayout llShow;
    private String formatDate = "", formatDateDisplay = "", tanggalAwal = "", tanggalAkhir = "";
    private String keyword = "";
    private TextView tvTotal, tvJmlPelanggan, tvJmlNota;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_entry_canvas, container, false);
        context = getActivity();

        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;
        initUI();
    }

    private void initUI() {

        urlGetCanvasOrder = context.getResources().getString(R.string.url_get_canvas_header);

        actvNoNota = (AutoCompleteTextView) layout.findViewById(R.id.actv_no_nota);
        lvListCanvas = (ListView) layout.findViewById(R.id.lv_list_canvas);
        fabAddOrder = (FloatingActionButton) layout.findViewById(R.id.fab_add_order);
        llLoadCusxtomer = (LinearLayout) layout.findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) layout.findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);
        tvTotal = (TextView) layout.findViewById(R.id.tv_total);
        tvJmlPelanggan = (TextView) layout.findViewById(R.id.tv_jml_customer);
        tvJmlNota = (TextView) layout.findViewById(R.id.tv_jml_nota);

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        edtAwal = (EditText) layout.findViewById(R.id.edt_awal);
        edtAkhir = (EditText) layout.findViewById(R.id.edt_akhir);
        llShow = (LinearLayout) layout.findViewById(R.id.ll_show);

        edtAwal.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        edtAkhir.setText(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));

        keyword = "";

        initValidation();

        setListCustomerAutocomplete();

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setListCustomerAutocomplete();
            }
        });

        fabAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AddNewCanvasOrder.class);
                context.startActivity(intent);
            }
        });

        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keyword = actvNoNota.getText().toString();
                setListCustomerAutocomplete();
            }
        });
    }

    private void initValidation() {

        iv.datePickerEvent(context, edtAwal, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        iv.datePickerEvent(context, edtAkhir, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
    }

    public void setListCustomerAutocomplete(){

        // Get All Customer
        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"SHOW");
        final String formatDate = context.getResources().getString(R.string.format_date);
        final String formatDateDisplay = context.getResources().getString(R.string.format_date_display1);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("tgl_awal", iv.ChangeFormatDateString(edtAwal.getText().toString(), formatDateDisplay, formatDate));
            jsonBody.put("tgl_akhir",iv.ChangeFormatDateString(edtAkhir.getText().toString(), formatDateDisplay, formatDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jsonBody, "POST", urlGetCanvasOrder, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        double total = 0;
                        tvTotal.setText(iv.ChangeToRupiahFormat(total));
                        tvJmlPelanggan.setText(iv.ChangeToCurrencyFormat("0"));
                        tvJmlNota.setText(iv.ChangeToCurrencyFormat("0"));

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();

                            List<String> listKdcus = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    if(jo.getString("customer").toLowerCase().contains(keyword.toLowerCase())){

                                        masterList.add(new CustomListItem(jo.getString("nobukti"),
                                                iv.ChangeFormatDateString(jo.getString("tgl"), formatDate, formatDateDisplay),
                                                iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("total"))),
                                                jo.getString("customer")));

                                        total += iv.parseNullDouble(jo.getString("total"));

                                        boolean isExist = false;
                                        for(String kdcus: listKdcus){

                                            isExist = false;
                                            if(kdcus.equals(jo.getString("kdcus"))){
                                                isExist = true;
                                                break;
                                            }
                                        }

                                        if(!isExist) listKdcus.add(jo.getString("kdcus"));
                                    }
                                }

                                tvJmlPelanggan.setText(iv.ChangeToCurrencyFormat(String.valueOf(listKdcus.size())));
                                tvJmlNota.setText(iv.ChangeToCurrencyFormat(String.valueOf(arrayJSON.length())));
                            }



                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
                            autocompleteList = new ArrayList<CustomListItem>(masterList);
                            tableList = new ArrayList<CustomListItem>(masterList);
                            getListCustomerAutocomplete(autocompleteList);
                            getListCustomerTable(autocompleteList);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListCustomerAutocomplete(null);
                            getListCustomerTable(null);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListCustomerAutocomplete(null);
                        getListCustomerTable(null);
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");
                    }
                });
    }

    // method to show autocomplete item
    private void getListCustomerAutocomplete(final List<CustomListItem> listItem){

        actvNoNota.setAdapter(null);

        if(listItem != null && listItem.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(context, android.R.layout.simple_list_item_1, listItem, "L");

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
                            //getListCustomerTable(tableList);
                            keyword = "";
                            setListCustomerAutocomplete();
                        }
                    }
                });
            }

            actvNoNota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem itemAtPosition = (CustomListItem) adapterView.getItemAtPosition(i);

                    // change table
                    List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                    itemToAdd.add(itemAtPosition);
                    getListCustomerTable(itemToAdd);
                }
            });

            /*actvNoNota.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        String keyword = actvNoNota.getText().toString().trim().toUpperCase();

                        for(CustomListItem item: masterList){

                            if(item.getListItem1().toUpperCase().contains(keyword) || item.getListItem4().toUpperCase().contains(keyword)){
                                itemToAdd.add(item);
                            }

                        }
                        getListCustomerTable(itemToAdd);
                        iv.hideSoftKey(context);
                        return true;
                    }
                    return false;
                }
            });*/
        }

    }

    // method to show table customer
    private void getListCustomerTable(List<CustomListItem> listItem){

        lvListCanvas.setAdapter(null);

        if (listItem != null && listItem.size() > 0){
            ListCanvasOrderTableAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListCanvasOrderTableAdapter((Activity) context, R.layout.adapter_list_canvas_order, listItem);

            //set adapter to autocomplete
            lvListCanvas.setAdapter(arrayAdapterString);

            lvListCanvas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    Intent intent = new Intent(context, DetailBarangCanvas.class);
                    intent.putExtra("nama", item.getListItem4());
                    intent.putExtra("nobukti", item.getListItem1());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        actvNoNota.setText("");
        setListCustomerAutocomplete();
    }
}
