package gmedia.net.id.kartikaelektrik.activityOmsetPenjualan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import gmedia.net.id.kartikaelektrik.MainActivity;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.Adapter.ListSalesAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ListSalesActivity extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private Context context;
    private AutoCompleteTextView actvSales;
    private ListView lvSales;
    private LinearLayout llContainer;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private int start = 0, count = 20;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private List<CustomListItem> masterlist = new ArrayList<>(), listItem = new ArrayList<>(), moreItem;
    private boolean firstLoad = true;
    private ListSalesAdapter adapter;
    private String kode = "", flag = "";
    private String tglAwal = "", tglAkhir = "";
    private String uid = "", token = "", exp = "", level = "", laba = "", fullName = "", username = "",password = "";
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sales);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pilih Sales");

        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        actvSales = (AutoCompleteTextView) findViewById(R.id.actv_sales);
        lvSales = (ListView) findViewById(R.id.lv_sales);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            flag = bundle.getString("flag","");

            if(flag.equals("1")){ // dari Menu Admin
                // Masuk sebagai sales

            }else{

                kode = bundle.getString("kode", "");
                tglAwal = bundle.getString("tanggalawal", "");
                tglAkhir = bundle.getString("tanggalakhir", "");
            }


            listItem = new ArrayList<>();
            adapter = new ListSalesAdapter((Activity) context, listItem);
            lvSales.setAdapter(adapter);

            actvSales.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        String keyword = actvSales.getText().toString().toLowerCase();
                        List<CustomListItem> items = new ArrayList<>();
                        if(masterlist != null && masterlist.size() > 0){

                            for(CustomListItem item : masterlist){
                                if(item.getListItem2().toLowerCase().contains(keyword)){
                                    items.add(item);
                                }
                            }

                            listItem.clear();
                            listItem.addAll(items);
                            adapter.notifyDataSetChanged();
                        }

                        iv.hideSoftKey(context);
                        return true;
                    }
                    return false;
                }
            });

            lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);

                    if(flag.equals("1")){

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah anda yakin ingin masuk sebagai "+item.getListItem2()+" ?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        masukSales(item.getListItem1());
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                    }else{

                        Intent intent = new Intent();
                        switch(kode){
                            case "cus":
                                intent = new Intent(context, ListOmsetCustomer.class);
                                intent.putExtra("nik", item.getListItem1());
                                intent.putExtra("tanggalawal", tglAwal);
                                intent.putExtra("tanggalakhir", tglAkhir);
                                break;
                            case "brg":
                                intent = new Intent(context, ListOmsetBarang.class);
                                intent.putExtra("nik", item.getListItem1());
                                intent.putExtra("tanggalawal", tglAwal);
                                intent.putExtra("tanggalakhir", tglAkhir);
                                break;
                            default:
                                intent = new Intent(context, ListOmsetCustomer.class);
                                intent.putExtra("nik", item.getListItem1());
                                intent.putExtra("tanggalawal", tglAwal);
                                intent.putExtra("tanggalakhir", tglAkhir);
                                break;
                        }

                        startActivity(intent);
                    }

                }
            });

            getDataSales();
        }
    }

    private void getDataSales() {

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

        ApiVolley restService = new ApiVolley(context, jBody, "GET", ServerURL.getSalesAdmin, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
                        isLoading = false;
                        listItem.clear();
                        masterlist.clear();

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listItem.add(new CustomListItem(
                                            jo.getString("nik"),
                                            jo.getString("nama"),
                                            jo.getString("jabatan")));

                                    masterlist.add(new CustomListItem(
                                            jo.getString("nik"),
                                            jo.getString("nama"),
                                            jo.getString("jabatan")));
                                }
                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"ERROR");
                    }
                });
    }

    private void getListTable(List<CustomListItem> listItems){

        lvSales.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            adapter = new ListSalesAdapter((Activity) context, listItems);

            //set adapter to autocomplete
            lvSales.setAdapter(adapter);

            lvSales.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvSales.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvSales.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvSales.addFooterView(footerList);
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

            lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);

                    if(flag.equals("1")){

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah anda yakin ingin masuk sebagai "+item.getListItem2()+" ?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        masukSales(item.getListItem1());
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                    }else{

                        Intent intent = new Intent();
                        switch(kode){
                            case "cus":
                                intent = new Intent(context, ListOmsetCustomer.class);
                                intent.putExtra("nik", item.getListItem1());
                                intent.putExtra("tanggalawal", tglAwal);
                                intent.putExtra("tanggalakhir", tglAkhir);
                                break;
                            case "brg":
                                intent = new Intent(context, ListOmsetBarang.class);
                                intent.putExtra("nik", item.getListItem1());
                                intent.putExtra("tanggalawal", tglAwal);
                                intent.putExtra("tanggalakhir", tglAkhir);
                                break;
                            default:
                                intent = new Intent(context, ListOmsetCustomer.class);
                                intent.putExtra("nik", item.getListItem1());
                                intent.putExtra("tanggalawal", tglAwal);
                                intent.putExtra("tanggalakhir", tglAkhir);
                                break;
                        }

                        startActivity(intent);
                    }

                }
            });
        }
    }

    private void masukSales(final String nik) {

        dialogBox = new DialogBox(ListSalesActivity.this);
        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nik", nik);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.loginByNik, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        dialogBox.dismissDialog();

                        try {
                            JSONObject obj = new JSONObject(result);
                            String status = obj.getJSONObject("metadata").getString("status");
                            String message = obj.getJSONObject("metadata").getString("message");

                            if(iv.parseNullInteger(status) == 200){

                                uid = obj.getJSONObject("response").getString("id");
                                token = obj.getJSONObject("response").getString("token");
                                exp = obj.getJSONObject("response").getString("expired_at");
                                level = obj.getJSONObject("response").getString("level");
                                laba = obj.getJSONObject("response").getString("laba");
                                fullName = obj.getJSONObject("response").getString("nama");
                                username = obj.getJSONObject("response").getString("username");
                                password = obj.getJSONObject("response").getString("password");

                                if (token.isEmpty()) {

                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    return;
                                } else {

                                    session.createLoginSession(uid, nik, username, password, token, exp, level, "1", fullName);
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    onLoginSuccess();
                                }
                            }else{

                                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                                Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            return;
                        }
                    }

                    @Override
                    public void onError(String result) {

                        Log.d("Error", "onError: "+result);
                        dialogBox.dismissDialog();

                    }
                });
    }

    public void showSnackBar(Activity activity, String message, int duration){
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, duration).show();
    }

    public void onLoginSuccess() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
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

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getListSales, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        isLoading = false;
                        lvSales.removeFooterView(footerList);

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            moreItem = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    moreItem.add(new CustomListItem(jo.getString("nik"),
                                    jo.getString("nama"),
                                    jo.getString("alamat")));
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
                        lvSales.removeFooterView(footerList);
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
