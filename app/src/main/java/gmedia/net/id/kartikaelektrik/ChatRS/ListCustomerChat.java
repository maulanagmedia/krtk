package gmedia.net.id.kartikaelektrik.ChatRS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ChatRS.Adapter.ListCustomerChatAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class ListCustomerChat extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private Activity activity;
    private EditText edtCustomer;
    private ListView lvCustomer;

    private List<CustomListItem> listItem = new ArrayList<>();
    private ListCustomerChatAdapter adapter;
    private View footerList;
    private DialogBox dialogBox;
    private String keyword = "";
    private int start = 0, count = 10;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_customer_chat);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pilih Customer");
        }

        activity = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(activity);
        edtCustomer = (EditText) findViewById(R.id.edt_customer);
        lvCustomer = (ListView) findViewById(R.id.lv_customer);

        start = 0;
        count = 10;
        keyword  = "";
        isLoading = false;

        lvCustomer.addFooterView(footerList);
        adapter = new ListCustomerChatAdapter(activity, listItem);
        lvCustomer.removeFooterView(footerList);
        lvCustomer.setAdapter(adapter);

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

        lvCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                if(item != null){
                    Intent intent = new Intent(activity, DetailChat.class);
                    intent.putExtra("nama", item.getListItem2());
                    intent.putExtra("kdcus", item.getListItem1());
                    startActivity(intent);
                }else{
                    DialogBox.showDialog(activity, 3,"Data tidak termuat dengan benar, harap ulangi proses");
                }
            }
        });
    }

    private void initEvent() {

        edtCustomer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtCustomer.getText().toString();
                    start = 0;
                    listItem.clear();
                    initData();

                    iv.hideSoftKey(activity);
                    return true;
                }

                return false;
            }
        });
    }

    private void initData() {

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        lvCustomer.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(activity, jBody, "POST", ServerURL.getChatToko, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvCustomer.removeFooterView(footerList);
                if(start == 0) dialogBox.dismissDialog();
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
                            listItem.add(new CustomListItem(
                                    jo.getString("kdcus")
                                    ,jo.getString("nama")
                                    ,jo.getString("alamat")
                                    ,jo.getString("image")
                            ));
                        }

                    }else{

                        if(start == 0) DialogBox.showDialog(activity, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    dialogBox.showDialog(activity, 2, "Terjadi kesalahan, harap ulangi proses");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvCustomer.removeFooterView(footerList);
                isLoading = false;
                if(start == 0) dialogBox.dismissDialog();

                dialogBox.showDialog(activity, 2, "Terjadi kesalahan, harap ulangi proses");
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