package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.ListSummarySetoranAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailCheckoutSetoran extends AppCompatActivity {

    private Context context;
    private LinearLayout llAdd;
    private String kdcus = "", namaCus;
    private SessionManager session;
    private LinearLayout llProcess;
    private TextView tvSetoran;
    private ListView lvSetoran;
    private boolean isConfirm = true;
    private List<CustomListItem> listSetoran;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_checkout_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Proses Setoran");
        context = this;
        session = new SessionManager(context);
        isConfirm = true;

        initUI();
    }

    private void initUI() {

        llAdd = (LinearLayout) findViewById(R.id.ll_add);
        llProcess = (LinearLayout) findViewById(R.id.ll_process);
        tvSetoran = (TextView) findViewById(R.id.tv_total);
        lvSetoran = (ListView) findViewById(R.id.lv_setoran);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            namaCus = bundle.getString("namacus", "");
        }

        initEvent();

        getDataSetoran();
    }

    private void getDataSetoran(){

        listSetoran = new ArrayList<>();
        double total = 0;
        for(int i = 0; i < ListNotaPiutang.jaSetoran.length(); i++){

            try {

                JSONObject jo = ListNotaPiutang.jaSetoran.getJSONObject(i);
                String crBayar = jo.getString("crbayar");
                if(jo.getString("crbayar").equals("T")){
                    crBayar = "Tunai";
                }else if(jo.getString("crbayar").equals("B")){
                    crBayar = "Bank";
                }else if(jo.getString("crbayar").equals("G")){
                    crBayar = "Giro";
                }

                listSetoran.add(new CustomListItem(
                        String.valueOf(i)
                        ,jo.getString("nonota")
                        ,crBayar + (jo.getString("bank").length() > 0 ? "( "+jo.getString("bank") + ")" : "")
                        ,jo.getString("jumlah")
                        ,jo.getString("totaldiskon")
                ));

                total += iv.parseNullDouble(jo.getString("jumlah"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        tvSetoran.setText(iv.ChangeToRupiahFormat(total));
        if(listSetoran != null && listSetoran.size() > 0){

            ListSummarySetoranAdapter adapter = new ListSummarySetoranAdapter((Activity) context, listSetoran);
            lvSetoran.setAdapter(adapter);
        }
    }

    private void initEvent() {

        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailFormSetoran.class);
                intent.putExtra("kdcus", kdcus);
                intent.putExtra("namacus", namaCus);
                startActivity(intent);
                finish();
            }
        });

        llProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validasi

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin memproses setoran ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                saveData();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            }
        });
    }

    public void saveData() {

        llProcess.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        JSONArray jSetoran = new JSONArray();
        for(int i = 0; i < ListNotaPiutang.jaSetoran.length(); i++){

            try {

                JSONObject jo = ListNotaPiutang.jaSetoran.getJSONObject(i);

                JSONObject jData = new JSONObject();
                jData.put("crbayar", jo.getString("crbayar"));
                jData.put("daribank", jo.getString("dari_bank"));
                jData.put("daripemilik", jo.getString("dari_pemilik"));
                jData.put("kebank", jo.getString("kode_bank"));
                jData.put("kenamabank", jo.getString("bank"));
                jData.put("tgltransfer", jo.getString("tgltransfer"));
                jData.put("nonota", jo.getString("nonota"));
                jData.put("diskon", jo.getString("diskon"));
                jData.put("totaldiskon", jo.getString("totaldiskon"));
                jData.put("jumlah", jo.getString("jumlah"));

                jSetoran.put(jData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdcus", kdcus);
            jBody.put("setoran", jSetoran);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jBody, "POST", ServerURL.saveSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                llProcess.setEnabled(true);
                progressDialog.dismiss();
                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        isConfirm = false;
                        CustomerSetoran.isSaved = true;
                        onBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                llProcess.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
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

        if(isConfirm){

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Pembatalan")
                    .setMessage("Setoran belum terproses, anda yakin membatalkan proses setoran?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            isConfirm = false;
                            onBackPressed();
                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            isConfirm = true;
                        }
                    }).show();
        }else{

            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }
}
