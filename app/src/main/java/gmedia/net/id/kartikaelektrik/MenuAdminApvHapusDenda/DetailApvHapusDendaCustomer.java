package gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda.Adapter.ListPiutangDendaCustomerdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailApvHapusDendaCustomer extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private ListPiutangDendaCustomerdapter adapter;
    private List<OptionItem> masterList = new ArrayList<>();
    private ItemValidation iv = new ItemValidation();
    private ListView lvDenda;
    private TextView tvTotalNota, tvTotalDenda;
    private String kdcus = "", nama = "", nik = "";
    private LinearLayout llTerima, llTolak;
    double totalNota = 0, totalDenda = 0;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_apv_hapus_denda_customer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Verifikasi Hapus Denda");

        context = this;
        session = new SessionManager(context);
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        lvDenda = (ListView) findViewById(R.id.lv_denda);
        tvTotalNota = (TextView) findViewById(R.id.tv_total_nota);
        tvTotalDenda = (TextView) findViewById(R.id.tv_total_denda);
        llTolak = (LinearLayout) findViewById(R.id.ll_tolak);
        llTerima = (LinearLayout) findViewById(R.id.ll_terima);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        adapter = new ListPiutangDendaCustomerdapter((Activity) context, masterList);
        lvDenda.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            nama = bundle.getString("nama", "");
            nik = bundle.getString("nik", "");

            getSupportActionBar().setSubtitle(nama);
        }
    }

    private void initEvent() {

        llTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(session.getLevelJabatan().equals("1") // Owner
                        || session.getLevelJabatan().equals("5")) { // Finance


                }else{

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Peringatan")
                            .setMessage("Maaf anda tidak dapat mengubah data ini.")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            })
                            .show();

                    return;
                }

                if(totalNota == 0){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Peringatan")
                            .setMessage("Harap pilih minimal satu nota")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyetujui penghapusan denda?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                saveData("2");
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

        llTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(session.getLevelJabatan().equals("1") // Owner
                        || session.getLevelJabatan().equals("5")) { // Finance


                }else{

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Peringatan")
                            .setMessage("Maaf anda tidak dapat mengubah data ini.")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            })
                            .show();

                    return;
                }

                if(totalNota == 0){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Peringatan")
                            .setMessage("Harap pilih minimal satu nota")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menolak penghapusan denda?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                saveData("0");
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

    public void saveData(String flag) {

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        JSONArray jArray = new JSONArray();

        for(OptionItem item : masterList){

            if(item.isSelected()){

                jArray.put(item.getValue());
            }
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nonota", jArray);
            jBody.put("flag", flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jBody, "POST", ServerURL.saveApvHapusDenda, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                progressDialog.dismiss();
                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        onBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nik", nik);
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getDetailPengajuanHapusDendaCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        String message = "Terjadi kesalahan saat memuat data";
                        masterList.clear();
                        pbLoading.setVisibility(View.GONE);

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            message = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    masterList.add(new OptionItem(
                                            jo.getString("nonota"),
                                            jo.getString("nonota"),
                                            jo.getString("tgl"),
                                            jo.getString("piutang"),
                                            jo.getString("bayar"),
                                            jo.getString("sisa"),
                                            false));
                                }

                                int total = masterList.size();
                            }

                        }catch (Exception e){

                            e.printStackTrace();
                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("Informasi")
                                    .setMessage(message + ", ulangi proses?")
                                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            initData();
                                        }
                                    })
                                    .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
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

                        pbLoading.setVisibility(View.GONE);
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Informasi")
                                .setMessage("Terjadi kesalahan saat memuat data, ulangi proses?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        initData();
                                    }
                                })
                                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                });
    }

    public void updateTotal(){

        try {

            totalNota = 0; totalDenda = 0;

            for(OptionItem item : masterList){

                if(item.isSelected()){

                    totalDenda += iv.parseNullDouble(item.getAtt4());
                    totalNota++;
                }
            }

            tvTotalNota.setText(iv.ChangeToCurrencyFormat(totalNota));
            tvTotalDenda.setText(iv.ChangeToCurrencyFormat(totalDenda));
        }catch (Exception e) {e.printStackTrace();}

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
