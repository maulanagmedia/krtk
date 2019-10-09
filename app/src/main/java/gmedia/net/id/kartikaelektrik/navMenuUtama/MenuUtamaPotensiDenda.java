package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPengajuanTempo.ActCustomerPengajuanTempo;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter.PengajuanTempoAdapter;
import gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter.PotensiDendaAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaPotensiDenda extends Fragment {

    private View layout;
    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private List<Customer> masterListCustomer , listCustomerAutocomplete, listCustomerTable;
    private EditText edtKeyword;
    private ListView lvPotensi;
    private List<CustomListItem> listData;
    private PotensiDendaAdapter adapter;
    private boolean isLoading = false;
    private View footerList;
    private int start = 0, count = 10;
    private String keyword = "";

    public MenuUtamaPotensiDenda(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_potensi_denda, container, false);
        getActivity().setTitle("Potensi Denda");
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

        session = new SessionManager(context);
        edtKeyword = (EditText) layout.findViewById(R.id.edt_keyword);
        lvPotensi = (ListView) layout.findViewById(R.id.lv_potensi);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        initEvent();
        initData();
    }

    private void initEvent() {

        listData = new ArrayList<>();
        //set adapter for autocomplete
        adapter = new PotensiDendaAdapter((Activity) context, listData);

        //set adapter to autocomplete
        lvPotensi.setAdapter(adapter);

        isLoading = false;

        lvPotensi.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvPotensi.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvPotensi.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        lvPotensi.addFooterView(footerList);
                        start += count;
                        initData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        edtKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtKeyword.getText().toString();
                    start = 0;
                    initData();
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

    }

    private void initData() {

        isLoading = true;
        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", edtKeyword.getText().toString());
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("nik", session.getNikAsli());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getPotensiDenda, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        lvPotensi.removeFooterView(footerList);
                        isLoading = false;
                        if(start == 0){

                            listData.clear();
                        }

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    listData.add(new CustomListItem(
                                            jo.getString("id")
                                            ,jo.getString("nonota")
                                            ,jo.getString("nama_customer")
                                            ,jo.getString("tgl")
                                            ,jo.getString("tgltempo")
                                            ,jo.getString("piutang")
                                            ,jo.getString("jumlah_denda")
                                    ));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        lvPotensi.removeFooterView(footerList);
                        isLoading = false;
                    }
                });
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
    }
}
