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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOrderCustom.AddNewCustomOrder;
import gmedia.net.id.kartikaelektrik.adapter.CustomOrder.ListCustomTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class MenuUtamaOrderCustom extends Fragment {

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
    private FloatingActionButton fbtAdd;
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private String urlOCHeader = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_order_custom, container, false);
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
        urlOCHeader = context.getResources().getString(R.string.url_get_custom_header);

        actSalesOrder = (AutoCompleteTextView) layout.findViewById(R.id.autocomplete_SalesOrder);
        lvSalesOrder = (ListView) layout.findViewById(R.id.lv_1_order_salesOrder);
        llLoadSO = (LinearLayout) layout.findViewById(R.id.ll_load_so);
        pbLoadSO = (ProgressBar) layout.findViewById(R.id.pb_load_so);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh_so);
        fbtAdd = (FloatingActionButton) layout.findViewById(R.id.fbt_add);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListAutocomplete();
            }
        });

        getListAutocomplete();

        fbtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AddNewCustomOrder.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getListAutocomplete();
    }

    public void getListAutocomplete(){

        // Get All No bukti
        iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", urlOCHeader, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        String status = "0";
                        try {

                            responseAPI = new JSONObject(result);
                            status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    masterList.add(new CustomListItem(jo.getString("nobukti"),jo.getString("customer"),jo.getString("kdcus"), jo.getString("tgl"), jo.getString("tgltempo"), jo.getString("total"), jo.getString("status")));
                                }
                            }

                            autocompleteList = new ArrayList<>(masterList);
                            tableList = new ArrayList<>(masterList);
                            getAutocomplete(autocompleteList);
                            getTable(tableList);

                            iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"GONE");

                        }catch (Exception e){
                            e.printStackTrace();
                            getAutocomplete(null);
                            getTable(null);
                            if(Integer.parseInt(status) != 404){
                                iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"ERROR");
                            }else{
                                iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"GONE");
                            }
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getAutocomplete(null);
                        getTable(null);
                        iv.ProgressbarEvent(llLoadSO, pbLoadSO,btnRefresh,"ERROR");
                    }
                });
    }

    // method to show autocomplete item
    private void getAutocomplete(List<CustomListItem> listItems){

        actSalesOrder.setAdapter(null);

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

        actSalesOrder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    String keyword = actSalesOrder.getText().toString().toUpperCase().trim();
                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    for (CustomListItem item: masterList){

                        if(item.getListItem1().toUpperCase().contains(keyword) ||item.getListItem2().toUpperCase().contains(keyword) ) items.add(item);
                    }

                    getTable(items);

                    iv.hideSoftKey(context);
                    return  true;
                }

                return false;
            }
        });
    }

    // method to show table customer
    private void getTable(List<CustomListItem> listItem){

        lvSalesOrder.setAdapter(null);

        if (listItem != null && listItem.size() > 0){
            ListCustomTableAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListCustomTableAdapter((Activity) context, listItem);

            //set adapter to autocomplete
            lvSalesOrder.setAdapter(arrayAdapterString);
        }
    }

}
