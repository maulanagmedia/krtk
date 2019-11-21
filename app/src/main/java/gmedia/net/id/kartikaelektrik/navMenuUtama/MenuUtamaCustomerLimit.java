package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomerLimit.ListCustomerLimitAdapter;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaCustomerLimit extends Fragment {

    private View layout;
    private Context context;
    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvListPelanggan;
    private ItemValidation iv = new ItemValidation();
    private String urlGetAllCustomer;
    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private List<Customer> masterListCustomer , listCustomerAutocomplete, listCustomerTable;
    private boolean firstLoad = true;
    private String currentString = "";
    private DialogBox dialogBox;

    public MenuUtamaCustomerLimit(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_customer_limit, container, false);
        getActivity().setTitle("Customer Limit");
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

        urlGetAllCustomer = ServerURL.getCustomerLimit;

        actvNamaPelanggan = (AutoCompleteTextView) layout.findViewById(R.id.actv_nama_pelanggan);
        lvListPelanggan = (ListView) layout.findViewById(R.id.lv_list_pelanggan);
        llLoadCusxtomer = (LinearLayout) layout.findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) layout.findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);

        setListCustomerAutocomplete();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListCustomerAutocomplete();
            }
        });
    }

    public void setListCustomerAutocomplete(){

        // Get All Customer
        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"SHOW");

        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(layout.getContext(), jsonBody, "GET", urlGetAllCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterListCustomer = new ArrayList<Customer>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterListCustomer.add(new Customer(jo.getString("kdcus"),
                                            jo.getString("nama"),
                                            jo.getString("alamat"),
                                            jo.getString("kota"),
                                            iv.parseNullString(jo.getString("total_piutang")),
                                            jo.getString("max_piutang"),
                                            jo.getString("pengajuan")));
                                }

                                int total = masterListCustomer.size();

                            }

                            listCustomerAutocomplete = new ArrayList<Customer>(masterListCustomer);
                            listCustomerTable = new ArrayList<Customer>(masterListCustomer);
                            getListCustomerAutocomplete(listCustomerAutocomplete);
                            getListCustomerTable(listCustomerTable);
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
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                    }
                });
    }

    // method to show autocomplete item
    private void getListCustomerAutocomplete(final List<Customer> listItemCustomer){

        actvNamaPelanggan.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){

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
                        if(actvNamaPelanggan.length() <= 0){
                            getListCustomerTable(listCustomerTable);
                        }
                    }
                });
            }

            actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        String keyword = actvNamaPelanggan.getText().toString().trim();
                        List<Customer> itemCustomer = new ArrayList<Customer>();
                        for(Customer customer: masterListCustomer){
                            String customerName = customer.getNamaCustomer().toString();
                            if(customerName.toUpperCase().contains(keyword.toUpperCase())){
                                String kdCustomer = customer.getKodeCustomer();
                                String lastCustomerName = customer.getNamaCustomer().toString();
                                String customerAddress = customer.getAlamat();
                                String customerCity = customer.getKota();
                                String customerPiutang = iv.parseNullString(customer.getTotalPiutang());
                                String customerMaxPiutang = customer.getMaxPiutang();
                                itemCustomer.add(new Customer(kdCustomer,lastCustomerName,customerAddress, customerCity, customerPiutang, customerMaxPiutang));
                            }
                        }
                        getListCustomerTable(itemCustomer);
                        iv.hideSoftKey(context);
                        return  true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table customer
    private void getListCustomerTable(List<Customer> listItemCustomer){

        lvListPelanggan.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){
            ListCustomerLimitAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListCustomerLimitAdapter((Activity) context,listItemCustomer.size(),listItemCustomer);

            //set adapter to autocomplete
            lvListPelanggan.setAdapter(arrayAdapterString);

            lvListPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Customer customer = (Customer) adapterView.getItemAtPosition(i);
                    getDialog(customer);
                }
            });
        }
    }

    private void getDialog(final Customer customer){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_customer_limit, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final EditText edtNama = (EditText) viewDialog.findViewById(R.id.edt_nama);
        final EditText edtLimit = (EditText) viewDialog.findViewById(R.id.edt_limit);
        final LinearLayout llTolak = (LinearLayout) viewDialog.findViewById(R.id.ll_tolak);
        final LinearLayout llSetujui = (LinearLayout) viewDialog.findViewById(R.id.ll_setujui);
        final ImageView ivClose = (ImageView) viewDialog.findViewById(R.id.iv_close);

        edtLimit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtLimit.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtLimit.setText(formatted);
                    edtLimit.setSelection(formatted.length());
                    edtLimit.addTextChangedListener(this);
                }
            }
        });

        edtNama.setText(customer.getNamaCustomer());

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(alert != null){
                    try {
                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        llSetujui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validasi
                if(edtLimit.getText().toString().isEmpty()){

                    edtLimit.setError("Jumlah harap diisi");
                    edtLimit.requestFocus();
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin memproses limit untuk "+customer.getNamaCustomer() + " ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(alert != null){
                                    try {
                                        alert.dismiss();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                saveApprove(customer.getKodeCustomer(), edtLimit.getText().toString().replaceAll("[,.]", ""));
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        llTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null){
                    try {
                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveApprove(String kdcus, String jumlah) {

        dialogBox = new DialogBox(context);
        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdcus", kdcus);
            jBody.put("jumlah", jumlah);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(context, jBody, "POST", ServerURL.sendCustomerLimit, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        setListCustomerAutocomplete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        actvNamaPelanggan.setText("");
        setListCustomerAutocomplete();
    }
}
