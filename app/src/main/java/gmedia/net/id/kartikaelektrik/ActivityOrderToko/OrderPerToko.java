package gmedia.net.id.kartikaelektrik.ActivityOrderToko;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityOrderToko.Adapter.ListOrderPerTokoAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class OrderPerToko extends AppCompatActivity {

    private Activity activity;
    private AutoCompleteTextView actvNama;
    private TextView tvTotal;
    private ListView lvData;
    private String keyword = "";
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private List<CustomListItem> listItems = new ArrayList<>();
    private String kdcus, namaCustomer;
    private ListOrderPerTokoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_per_toko);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            namaCustomer = bundle.getString("nama", "");
        }

        setTitle("Order " + namaCustomer);
        dialogBox = new DialogBox(activity);

        initUI();
        initData();
    }

    private void initUI() {

        actvNama = (AutoCompleteTextView) findViewById(R.id.actv_nama);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        lvData = (ListView) findViewById(R.id.lv_data);

        actvNama.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvNama.getText().toString();
                    listItems.clear();
                    initData();

                    iv.hideSoftKey(activity);
                    return true;
                }

                return false;
            }
        });

        adapter = new ListOrderPerTokoAdapter(activity, listItems);
        lvData.setAdapter(adapter);
    }

    private void initData() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", kdcus);
            jBody.put("keyword", keyword);
        } catch (JSONException e) {


        }
        ApiVolley request = new ApiVolley(activity, jBody, "POST", ServerURL.getListOrderToko, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                listItems.clear();
                dialogBox.dismissDialog();
                String message = "";
                double total = 0;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listItems.add(new CustomListItem(
                                    jo.getString("nobukti")
                                    ,jo.getString("tgl")
                                    ,jo.getString("tgltempo")
                                    ,jo.getString("tempo")
                                    ,jo.getString("total")
                                    ,jo.getString("jml_item")
                                    ,jo.getString("promo")
                            ));

                            total += iv.parseNullDouble(jo.getString("total"));
                        }

                    }else{

                        DialogBox.showDialog(activity, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    dialogBox.showDialog(activity, 2, "Terjadi kesalahan, harap ulangi proses");
                }

                tvTotal.setText(iv.ChangeToCurrencyFormat(total));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}