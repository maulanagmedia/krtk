package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import gmedia.net.id.kartikaelektrik.ActivityHapusDenda.DetailDendaCustomerActivity;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter.CustomerHapusDendaAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaHapusDenda extends Fragment {

    private View layout;
    private Context context;
    private SessionManager session;
    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvListPelanggan;
    private ItemValidation iv = new ItemValidation();
    private CustomerHapusDendaAdapter adapter;
    private LinearLayout llLoadCusxtomer;
    private ProgressBar pbLoadCustomer;
    private Button btnRefresh;
    private List<CustomListItem> masterList = new ArrayList<>();
    private boolean firstLoad = true;
    private String currentString = "";
    private View footerList;
    private int start = 0, count = 10;
    private String keyword = "";
    private boolean isLoading = false;
    private TextView tvCustomer, tvTotalDenda, tvTotalHapusDenda;

    public MenuUtamaHapusDenda(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_hapus_denda, container, false);
        getActivity().setTitle("Hapus Denda");
        context = getActivity();
        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;

        initUI();
        initEvent();
        initData();
        //getSummaryHapusDenda();
    }

    private void initUI() {

        session = new SessionManager(context);
        actvNamaPelanggan = (AutoCompleteTextView) layout.findViewById(R.id.actv_nama_pelanggan);
        lvListPelanggan = (ListView) layout.findViewById(R.id.lv_list_pelanggan);
        llLoadCusxtomer = (LinearLayout) layout.findViewById(R.id.ll_load_customer);
        pbLoadCustomer = (ProgressBar) layout.findViewById(R.id.pb_load_customer);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);
        tvCustomer = (TextView) layout.findViewById(R.id.tv_customer);
        tvTotalDenda = (TextView) layout.findViewById(R.id.tv_total_denda);
        tvTotalHapusDenda = (TextView) layout.findViewById(R.id.tv_total_hapus_denda);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvListPelanggan.addFooterView(footerList);
        adapter = new CustomerHapusDendaAdapter((Activity) context, masterList);
        lvListPelanggan.removeFooterView(footerList);
        lvListPelanggan.setAdapter(adapter);

        lvListPelanggan.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvListPelanggan.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvListPelanggan.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                masterList.clear();
                start = 0;
                initData();
            }
        });
    }

    private void initEvent() {

        actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvNamaPelanggan.getText().toString();
                    start = 0;
                    masterList.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        lvListPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailDendaCustomerActivity.class);
                intent.putExtra("kdcus", item.getListItem1());
                intent.putExtra("nama", item.getListItem2());
                ((Activity)context).startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    public void initData(){

        // Get All Customer
        isLoading = true;
        if(start == 0) iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"SHOW");
        JSONObject jBody = new JSONObject();
        lvListPelanggan.addFooterView(footerList);

        try {
            jBody.put("nik", session.getNik());
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(layout.getContext(), jBody, "POST", ServerURL.getCustomerDenda, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        lvListPelanggan.removeFooterView(footerList);
                        if(start == 0) iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"GONE");
                        String message = "";
                        isLoading = false;

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(
                                            jo.getString("kdcus"),
                                            jo.getString("customer"),
                                            jo.getString("alamat"),
                                            jo.getString("denda")));
                                }

                                int total = masterList.size();

                            }

                        }catch (Exception e){
                            iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();

                        if(start == 0) getSummaryHapusDenda();
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoadCusxtomer,pbLoadCustomer,btnRefresh,"ERROR");
                    }
                });
    }

    public void getSummaryHapusDenda(){

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nik", session.getNik());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(layout.getContext(), jBody, "POST", ServerURL.getSummaryHapusDenda, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        String message = "";

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray ja = responseAPI.getJSONArray("response");
                                if(ja.length() > 0) {

                                    JSONObject jo = ja.getJSONObject(0);

                                    tvCustomer.setText(iv.ChangeToCurrencyFormat(jo.getString("jml_customer")));
                                    tvTotalDenda.setText(iv.ChangeToCurrencyFormat(jo.getString("jml_denda")));
                                    tvTotalHapusDenda.setText(iv.ChangeToCurrencyFormat(jo.getString("total")));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, "Terjadi kesalahan saat memuat data, harap coba kembali.", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi kesalahan saat memuat data, harap coba kembali", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
    }
}
