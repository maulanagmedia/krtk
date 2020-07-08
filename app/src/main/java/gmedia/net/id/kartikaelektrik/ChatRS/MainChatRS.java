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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ChatRS.Adapter.ListMainChatAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class MainChatRS extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private EditText edtSearch;
    private ListView lvChat;
    private DialogBox dialogBox;
    private FloatingActionButton fabChat;
    private ItemValidation iv = new ItemValidation();
    private String keyword = "";
    private List<CustomListItem> listGroup = new ArrayList<>();
    private boolean isLoading = false;
    private int start = 0, count = 10;
    private View footerList;
    private ListMainChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_r_s);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Daftar Pesan");
        }

        context = this;
        session = new SessionManager(context);

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        edtSearch = (EditText) findViewById(R.id.edt_search);
        lvChat = (ListView) findViewById(R.id.lv_chat);
        fabChat = (FloatingActionButton) findViewById(R.id.fab_chat);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(context);

        start = 0;
        keyword  = "";
        isLoading = false;

        lvChat.addFooterView(footerList);
        adapter = new ListMainChatAdapter((Activity) context, listGroup);
        lvChat.removeFooterView(footerList);
        lvChat.setAdapter(adapter);

        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvChat.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvChat.getLastVisiblePosition() >= total - threshold && !isLoading) {

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

        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ListCustomerChat.class);
                startActivity(intent);
            }
        });

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailChat.class);
                intent.putExtra("kdcus", item.getListItem1());
                intent.putExtra("nama", item.getListItem2());
                startActivity(intent);
            }
        });
    }

    private void initEvent() {

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    listGroup.clear();
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

        /*
        listGroup.add(new CustomItem(
                "1"
                ,"Victor"
                ,"Aku sangar koyo fotoku"
                ,"https://static9.depositphotos.com/1594920/1088/i/950/depositphotos_10880072-stock-photo-mixed-breed-monkey-between-chimpanzee.jpg"
                ,"2019-08-26 14:05:10"
                ,"0890989999"
        ));*/

        adapter.notifyDataSetChanged();

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        lvChat.addFooterView(footerList);

        try {
            jBody.put("keyword", keyword);
            jBody.put("start", start);
            jBody.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getChatRoom, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvChat.removeFooterView(footerList);
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
                            listGroup.add(new CustomListItem(
                                    jo.getString("kdcus")
                                    ,jo.getString("nama")
                                    ,jo.getString("message")
                                    ,jo.getString("image")
                                    ,jo.getString("timestamp")
                            ));
                        }

                    }else{

                        if(start == 0) DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    dialogBox.showDialog(MainChatRS.this, 2, "Terjadi kesalahan, harap ulangi proses");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvChat.removeFooterView(footerList);
                isLoading = false;
                if(start == 0) dialogBox.dismissDialog();

                dialogBox.showDialog(MainChatRS.this, 2, "Terjadi kesalahan, harap ulangi proses");
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
