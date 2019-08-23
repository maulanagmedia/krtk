package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
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

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter.ListCustomerLimitAppAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class VerifikasiLimitCustomer extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private int start = 0, count = 20;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private AutoCompleteTextView actvNama;
    private LinearLayout llContainer;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private ListView lvVerifikasi;
    private List<CustomListItem> masterList, moreItem;
    private boolean firstLoad = true;
    private ListCustomerLimitAppAdapter adapter;
    private String currentString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_limit_customer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Verifikasi Limit Order");
        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);
        actvNama = (AutoCompleteTextView) findViewById(R.id.actv_nama);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        lvVerifikasi = (ListView) findViewById(R.id.lv_verifikasi);
        firstLoad = true;

        getData();
    }

    private void getData() {

        start = 0;
        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"SHOW");
        isLoading = true;

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getCustomerLimitApprove, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
                        isLoading = false;
                        
                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(
                                            jo.getString("kdcus"),
                                            jo.getString("nama_customer"),
                                            jo.getString("jumlah"),
                                            jo.getString("tgl"),
                                            jo.getString("usertgl"),
                                            jo.getString("id")));
                                }
                            }
                            
                            getListAutocomplete(masterList);
                            getListTable(masterList);

                        }catch (Exception e){
                            e.printStackTrace();
                            getListAutocomplete(null);
                            getListTable(null);
                        }
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        getListAutocomplete(null);
                        getListTable(null);
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"ERROR");
                    }
                });
    }

    private void getListAutocomplete(List<CustomListItem> listItems) {

        actvNama.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvNama.getText().toString();
                    getData();
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

        if(firstLoad){
            firstLoad = false;
            actvNama.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(editable.toString().length() <= 0) {

                        keyword = "";
                        getData();
                    }
                }
            });
        }
    }

    private void getListTable(List<CustomListItem> listItems) {

        lvVerifikasi.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            adapter = new ListCustomerLimitAppAdapter((Activity) context, listItems.size(), listItems);

            //set adapter to autocomplete
            lvVerifikasi.setAdapter(adapter);

            lvVerifikasi.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvVerifikasi.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvVerifikasi.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvVerifikasi.addFooterView(footerList);
                            start += count;
                            getMoreData();
                            //Log.i(TAG, "onScroll: last ");
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });

            lvVerifikasi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);

                    if(session.getLevelJabatan().equals("1") // Owner
                            || session.getLevelJabatan().equals("5")) { // Finance

                        getDialog(item.getListItem6(), item.getListItem2(), item.getListItem3());
                    }else{

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Peringatan")
                                .setMessage("Maaf anda tidak dapat mengubah data ini.")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                    }
                                })
                                .show();
                    }

                }
            });
        }
    }

    private void getDialog(final String id, final String nama, String total){

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
        final TextView tvProses = (TextView) viewDialog.findViewById(R.id.tv_proses);

        llTolak.setVisibility(View.VISIBLE);
        tvProses.setText("Setujui");

        edtLimit.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(total)));
        edtLimit.setEnabled(false);
        edtLimit.setClickable(false);

        edtNama.setText(nama);

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
                        .setMessage("Apakah anda yakin ingin menyetujui limit untuk "+ nama + " ?")
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

                                saveApprove(id, "2");
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

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menolak limit untuk "+ nama + " ?")
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

                                saveApprove(id, "9");
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

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveApprove(String id, String appStatus) {

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", id);
            jBody.put("status", appStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(context, jBody, "POST", ServerURL.saveStatusCustomerLimit, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                progressDialog.dismiss();
                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        getData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMoreData() {

        isLoading = true;
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getCustomerLimitApprove, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        isLoading = false;
                        lvVerifikasi.removeFooterView(footerList);

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            moreItem = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    moreItem.add(new CustomListItem(
                                            jo.getString("kdcus"),
                                            jo.getString("nama_customer"),
                                            jo.getString("jumlah"),
                                            jo.getString("tgl"),
                                            jo.getString("usertgl"),
                                            jo.getString("id")));
                                }
                            }

                            if(adapter != null) adapter.addMoreData(moreItem);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        lvVerifikasi.removeFooterView(footerList);
                    }
                });
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
