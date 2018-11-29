package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter.ListCustomerHistoryLimitAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class HistoryLimitCustomer extends AppCompatActivity {

    private List<CustomListItem> listReller = new ArrayList<>();
    private ListView lvCustomer;
    private ListCustomerHistoryLimitAdapter adapterReseller;
    private View footerList;
    private Context context;
    private ItemValidation iv = new ItemValidation();
    private String keyword = "";
    private int start = 0, count = 10;
    private boolean isLoading = false;
    private AutoCompleteTextView avtvCustomer;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_limit_customer);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Daftar Customer");
        }

        context = this;

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);
        lvCustomer = (ListView) findViewById(R.id.lv_customer);
        avtvCustomer = (AutoCompleteTextView) findViewById(R.id.actv_customer);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvCustomer.addFooterView(footerList);
        adapterReseller = new ListCustomerHistoryLimitAdapter((Activity) context, listReller);
        lvCustomer.removeFooterView(footerList);
        lvCustomer.setAdapter(adapterReseller);

        lvCustomer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvCustomer.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvCustomer.getLastVisiblePosition() >= total - threshold && !isLoading) {

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
    }

    private void initEvent() {

        avtvCustomer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = avtvCustomer.getText().toString();
                    start = 0;
                    listReller.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        lvCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(view.getContext(), HistoryLimitCustomerDetail.class);
                intent.putExtra("kdcus", item.getListItem1());
                intent.putExtra("nama", item.getListItem2());
                view.getContext().startActivity(intent);
            }
        });
    }

    private void initData() {

        isLoading = true;
        if(start == 0) pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        lvCustomer.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getCustomerHistoryLimit,"", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvCustomer.removeFooterView(footerList);
                if(start == 0) pbLoading.setVisibility(View.GONE);
                String message = "";
                isLoading = false;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listReller.add(new CustomListItem(
                                    jo.getString("kdcus")
                                    ,jo.getString("nama")
                                    ,jo.getString("alamat")
                            ));
                        }

                    }else{

                        if(start == 0) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan, harap ulangi proses", Toast.LENGTH_LONG).show();
                }

                adapterReseller.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvCustomer.removeFooterView(footerList);
                isLoading = false;
                if(start == 0) pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan, harap ulangi proses", Toast.LENGTH_LONG).show();
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
}
