package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomer.CustomerDetail;
import gmedia.net.id.kartikaelektrik.adapter.CustomerOrder.ListCustomerOrderKhususAdapter;
import gmedia.net.id.kartikaelektrik.adapter.CustomerOrder.ListTableCustomerOrderAdapter;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class MenuUtamaCustomerOrderKh extends Fragment {

    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private LinearLayout llAddCustomer;
    private boolean firstLoad = true;

    public MenuUtamaCustomerOrderKh(){}
    private AutoCompleteTextView actvNamaPelanggan;
    private String kdCus, customerName = "", lastCustomerName = "", customerAddress = "";
    private ListView lvListCustomer;
    private List<Customer> listCustomer, listCustomerTable;
    private String urlGetAllCustomer;
    private View layout;
    private Context context;
    private String TAG = "Test.Customer";
    private ItemValidation iv = new ItemValidation();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_customer_orderkh, container, false);
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
        actvNamaPelanggan = (AutoCompleteTextView) layout.findViewById(R.id.actv_list_customer);
        lvListCustomer = (ListView) layout.findViewById(R.id.lv_0_list_customer);
        llLoadCusxtomer = (LinearLayout) layout.findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) layout.findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);
        urlGetAllCustomer = context.getResources().getString(R.string.url_get_all_customer);
        llAddCustomer = (LinearLayout) layout.findViewById(R.id.ll_add_order);

        setListCustomerAutocomplete();

        llAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CustomerDetail.class);
                context.startActivity(intent);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListCustomerAutocomplete();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setListCustomerAutocomplete();
    }

    public void setListCustomerAutocomplete(){

        // Get All Customer
        llLoadCusxtomer.setVisibility(View.VISIBLE);
        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        listCustomer = new ArrayList<Customer>();
        listCustomerTable = new ArrayList<Customer>();
        ApiVolley restService = new ApiVolley(layout.getContext(), jsonBody, "GET", urlGetAllCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    listCustomer.add(new Customer(jo.getString("kdcus")
                                            ,jo.getString("nama")
                                            ,jo.getString("alamat")
                                            ,jo.getString("tempo")
                                            ,jo.getString("xdenda")
                                    ));

                                    listCustomerTable.add(
                                            new Customer(
                                                    jo.getString("kdcus")
                                                    ,jo.getString("nama")
                                                    ,jo.getString("alamat")
                                                    ,jo.getString("tempo")
                                                    ,jo.getString("xdenda")
                                            ));
                                }
                            }

                            getListCustomerAutocomplete(listCustomer);
                            // set list table customer
                            getListCustomerTable(listCustomerTable);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListCustomerTable(listCustomerTable);
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListCustomerTable(listCustomerTable);
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                    }
                });
    }

    // method to show autocomplete item
    private void getListCustomerAutocomplete(final List<Customer> listItemCustomer){

        // Listener event on change autocomplete
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
                    customerName = editable.toString();
                    if(customerName == "" || customerName.length() == 0){
                        getListCustomerTable(listCustomerTable);
                    }
                }
            });

        }

        actvNamaPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Customer customer = (Customer) adapterView.getItemAtPosition(i);
                kdCus = customer.getKodeCustomer();
                lastCustomerName = customer.getNamaCustomer().toString();
                customerAddress = customer.getAlamat();
                String tempo = customer.getTempo();

                // change table
                List<Customer> itemCustomer = new ArrayList<Customer>();
                itemCustomer.add(new Customer(kdCus,lastCustomerName,customerAddress,tempo));
                getListCustomerTable(itemCustomer);
            }
        });

        actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    // change table
                    List<Customer> itemCustomer = new ArrayList<Customer>();
                    String keyword = actvNamaPelanggan.getText().toString().trim().toUpperCase();
                    for(Customer item: listCustomer){

                        if(item.getNamaCustomer().toUpperCase().contains(keyword)){
                            itemCustomer.add(item);
                        }
                    }

                    getListCustomerTable(itemCustomer);
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

    }

    // method to show table customer
    private void getListCustomerTable(List<Customer> listItemCustomer){

        lvListCustomer.setAdapter(null);

        ListCustomerOrderKhususAdapter arrayAdapterString;

        //set adapter for autocomplete
        arrayAdapterString = new ListCustomerOrderKhususAdapter((Activity) layout.getContext(),listItemCustomer.size(),listItemCustomer);

        //set adapter to autocomplete
        lvListCustomer.setAdapter(arrayAdapterString);
    }

}
