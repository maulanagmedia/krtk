package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.ListNotaPiutangAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ListNotaPiutang extends AppCompatActivity {

    public static double totalHarga = 0;
    private Context context;
    private SessionManager session;
    private static ItemValidation iv = new ItemValidation();
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private ListView lvNota;
    private static TextView tvTotal;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    public static List<OptionItem> listSelectedNota;
    public static List<OptionItem> listNota;
    private String kdcus = "", namaCus = "";
    private EditText edtCustomer;
    public static ListNotaPiutangAdapter adapterPiutangSales;
    public static List<CustomListItem> itemSetoran;
    public static JSONArray jaSetoran;
    private boolean isKhusus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nota_piutang);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Pilih Nota Piutang");
        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        lvNota = (ListView) findViewById(R.id.lv_nota);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        edtCustomer = (EditText) findViewById(R.id.edt_customer);

        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setText("Lanjutkan Proses Setoran");

        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        totalHarga = 0;

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            namaCus = bundle.getString("namacus", "");
            isKhusus = bundle.getBoolean("khusus", false);
            edtCustomer.setText(namaCus);
        }

        getPiutangSales();

        initEvent();
    }

    private void initEvent() {

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validasi
                if(totalHarga <= 0){

                    Toast.makeText(context, "Harap pilih nota terlebih dahulu", Toast.LENGTH_LONG).show();
                    return;
                }

                listNota = new ArrayList<>();
                itemSetoran = new ArrayList<>();
                jaSetoran = new JSONArray();

                if(listSelectedNota != null){
                    for(OptionItem item: listSelectedNota){

                        if(item.isSelected()){

                            final OptionItem item1 = new OptionItem(
                                    item.getValue(),
                                    item.getText(),
                                    item.getAtt1(),
                                    item.getAtt2(),
                                    item.getAtt3(),
                                    false

                            );

                            listNota.add(item1);
                        }
                    }
                }

                Intent intent = new Intent(context, DetailFormSetoran.class);
                intent.putExtra("kdcus", kdcus);
                intent.putExtra("namacus", namaCus);
                intent.putExtra("khusus", isKhusus);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getPiutangSales() {

        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPiutangSales, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listSelectedNota = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jArray = response.getJSONArray("response");

                        for(int i = 0; i < jArray.length(); i++){

                            JSONObject jo = jArray.getJSONObject(i);
                            listSelectedNota.add(new OptionItem(
                                    jo.getString("nonota"),
                                    jo.getString("tgl"),
                                    jo.getString("sisa"),
                                    "0", // terbayar
                                    jo.getString("tanda"),
                                    false // checked
                            ));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    setSalesPiutang();

                } catch (JSONException e) {

                    setSalesPiutang();
                    e.printStackTrace();
                    iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                setSalesPiutang();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
            }
        });
    }
    private void setSalesPiutang(){

        lvNota.setAdapter(null);

        if(listSelectedNota != null && listSelectedNota.size() > 0){

            adapterPiutangSales = new ListNotaPiutangAdapter((Activity) context, listSelectedNota);
            lvNota.setAdapter(adapterPiutangSales);
        }
    }

    public static void updateHarga(){

        totalHarga = 0;
        if(listSelectedNota != null){
            for(OptionItem item: listSelectedNota){

                if(item.isSelected()){

                    totalHarga += iv.parseNullDouble(item.getAtt1());
                }
            }
        }

        tvTotal.setText(iv.ChangeToCurrencyFormat(iv.doubleToStringFull(totalHarga)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(adapterPiutangSales != null)
            adapterPiutangSales.getItems();
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
