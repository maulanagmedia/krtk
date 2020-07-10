package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityOrderToko.OrderPerToko;
import gmedia.net.id.kartikaelektrik.ChatRS.MainChatRS;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter.ListOrderDariTokoAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class MenuUtamaOrderToko extends Fragment {

    private View layout;
    private Context context;
    private AutoCompleteTextView actvToko;
    private ListView lvToko;
    private String keyword = "";
    private ItemValidation iv = new ItemValidation();
    private int start = 0, count = 10;
    private boolean isLoading = false;
    private View footerList;
    private DialogBox dialogBox;
    private List<CustomListItem> listItems = new ArrayList<>();
    private ListOrderDariTokoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_menu_utama_order_toko, container, false);
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

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        actvToko = (AutoCompleteTextView) layout.findViewById(R.id.actv_toko);
        lvToko = (ListView) layout.findViewById(R.id.lv_toko);
        dialogBox = new DialogBox(context);

        actvToko.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvToko.getText().toString();
                    listItems.clear();
                    start = 0;
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });

        start = 0;
        count = 10;
        keyword = "";
        isLoading = false;

        lvToko.addFooterView(footerList);
        adapter = new ListOrderDariTokoAdapter((Activity) context, listItems);
        lvToko.removeFooterView(footerList);
        lvToko.setAdapter(adapter);

        lvToko.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvToko.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvToko.getLastVisiblePosition() >= total - threshold && !isLoading) {

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

        lvToko.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(context, OrderPerToko.class);
                intent.putExtra("kdcus", item.getListItem1());
                intent.putExtra("nama", item.getListItem2());
                ((Activity)context).startActivity(intent);
            }
        });

        initData();
    }

    private void initData() {

        if(!isLoading){

            isLoading = true;
            if(start == 0) dialogBox.showDialog(true);
            JSONObject jBody = new JSONObject();
            lvToko.addFooterView(footerList);

            try {
                jBody.put("keyword", keyword);
                jBody.put("start", start);
                jBody.put("count", count);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getListOrderToko, "", "", 0, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    lvToko.removeFooterView(footerList);
                    if(start == 0){
                        listItems.clear();
                        dialogBox.dismissDialog();
                    }
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
                                listItems.add(new CustomListItem(
                                        jo.getString("kdcus")
                                        ,jo.getString("nama")
                                        ,jo.getString("jml_nota")
                                        ,jo.getString("total")
                                        ,jo.getString("image")
                                ));
                            }

                        }else{

                            if(start == 0) DialogBox.showDialog(context, 3, message);
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                        dialogBox.showDialog(context, 2, "Terjadi kesalahan, harap ulangi proses");
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(String result) {

                    lvToko.removeFooterView(footerList);
                    isLoading = false;
                    if(start == 0) dialogBox.dismissDialog();

                    dialogBox.showDialog(context, 2, "Terjadi kesalahan, harap ulangi proses");
                }
            });
        }
    }
}