package gmedia.net.id.kartikaelektrik.activityPengajuanTempo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPengajuanTempo.Adapter.CustomerTempoAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ActCustomerPengajuanTempo extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private AutoCompleteTextView actvPelanggan;
    private ListView lvCustomer;
    private List<CustomListItem> listData;
    private String TAG = "Test.Customer";
    private CustomerTempoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_customer_pengajuan_tempo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);

        setTitle("Pilih Pelanggan");
        initUI();
    }

    private void initUI() {

        actvPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_customer);
        lvCustomer = (ListView) findViewById(R.id.lv_customer);

        listData = new ArrayList<>();
        adapter = new CustomerTempoAdapter((Activity) context, listData);
        lvCustomer.setAdapter(adapter);

        initData();
    }

    public void initData(){

        // Get All Customer
        JSONObject jsonBody = new JSONObject();
        new ApiVolley(context, jsonBody, "GET", ServerURL.getCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        String message = "Terjadi kesalahan saat memuat data, harap ulangi kembali";
                        listData.clear();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    listData.add(new CustomListItem(jo.getString("kdcus")
                                            ,jo.getString("nama")
                                            ,jo.getString("alamat")
                                            ,jo.getString("tempo")
                                            ,jo.getString("xdenda")
                                    ));

                                }
                            }


                        }catch (Exception e){
                            e.printStackTrace();

                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("Kesalahan")
                                    .setMessage("Terjadi kesalahan dalam parsing data, ulangi proses ?")
                                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            initData();
                                        }
                                    })
                                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Kesalahan")
                                .setMessage("Terjadi kesalahan dalam memuat data, ulangi proses ?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        initData();
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
