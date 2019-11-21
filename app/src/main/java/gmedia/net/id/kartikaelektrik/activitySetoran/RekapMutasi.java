package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.HeaderRekapSetoranAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.notificationService.InitFirebaseSetting;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class RekapMutasi extends AppCompatActivity {

    private static Context context;
    private SessionManager session;
    private static ListView lvSetoran;
    private static ProgressBar pbLoading;
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edTanggalAwal;
    private EditText edtTanggalAkhir;
    private LinearLayout llShow;
    private TextView tvShow;
    private static ItemValidation iv = new ItemValidation();
    private static String tanggalAwal;
    private static String tanggalAkhir;
    private String formatDate = "", formatDateDisplay = "";
    private static List<CustomListItem> listSetoran;
    private static TextView tvTotal, tvTotalCustomer, tvTotalNota;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_mutasi);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Rekap Setoran");
        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        tilTanggalAwal = (TextInputLayout) findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edTanggalAwal.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        edtTanggalAkhir.setText(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
        lvSetoran = (ListView) findViewById(R.id.lv_setoran);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        llShow = (LinearLayout) findViewById(R.id.ll_show);
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvTotalCustomer = (TextView) findViewById(R.id.tv_total_customer);
        tvTotalNota = (TextView) findViewById(R.id.tv_total_nota);

        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setText("Kunci Setoran");

        if(session.getLevelJabatan().equals("1") || session.getLevelJabatan().equals("5")){ // hanya owner / finance yang bisa
            llSaveContainer.setVisibility(View.VISIBLE);
        }else{
            llSaveContainer.setVisibility(View.GONE);
        }

        if(session.getLevelJabatan().equals("6")){ // Supir

            setTitle("Rekap Biaya");
        }

        initValidation();

        getDataSetoran();

        initEvent();
    }

    private void initEvent() {

        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Mondatory
                if(!iv.mondatoryEdittext(tilTanggalAwal, edTanggalAwal, "Tanggal Awal Tidak Boleh Kosong")){
                    return;
                }

                if(!iv.mondatoryEdittext(tilTanggalAkhir, edtTanggalAkhir, "Tanggal Akhir Tidak Boleh Kosong")){
                    return;
                }

                if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal,formatDateDisplay)){
                    tilTanggalAkhir.setErrorEnabled(true);
                    tilTanggalAkhir.setError("Tanggal Akhir Tidak Dapat Sebelum Tanggal Awal");
                    edtTanggalAkhir.requestFocus();
                    return;
                }else{
                    tilTanggalAkhir.setError(null);
                    tilTanggalAkhir.setErrorEnabled(false);
                }

                tanggalAwal = iv.ChangeFormatDateString(edTanggalAwal.getText().toString(), formatDateDisplay, formatDate);
                tanggalAkhir = iv.ChangeFormatDateString(edtTanggalAkhir.getText().toString(), formatDateDisplay, formatDate);


                getDataSetoran();
            }
        });

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
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

                if(listSetoran != null && listSetoran.size() > 0){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Konfirmasi")
                            .setIcon(R.mipmap.logo_kartika)
                            .setMessage("Apakah anda yakin ingin mengunci setoran pada periode ini?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //saveKunci();
                                    checkLogin();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }else{
                    Toast.makeText(context, "List Masih Kosong", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void checkLogin() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);


        final EditText edtUsername = (EditText) viewDialog.findViewById(R.id.edt_username);
        final EditText edtPassword = (EditText) viewDialog.findViewById(R.id.edt_password);
        final Button btnLogin = (Button) viewDialog.findViewById(R.id.btn_login);

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                String refreshToken = "";
                InitFirebaseSetting.getFirebaseSetting(RekapMutasi.this);
                refreshToken = FirebaseInstanceId.getInstance().getToken();

                if(edtUsername.getText().toString().isEmpty()){

                    edtUsername.setError("Harap diisi");
                    edtUsername.requestFocus();
                    return;
                }else{

                    edtUsername.setError(null);
                }

                if(edtPassword.getText().toString().isEmpty()){

                    edtPassword.setError("Harap diisi");
                    edtPassword.requestFocus();
                    return;
                }else{

                    edtPassword.setError(null);
                }

                dialogBox = new DialogBox(RekapMutasi.this);
                dialogBox.showDialog(false);

                JSONObject jBody = new JSONObject();
                try {
                    jBody.put("username", edtUsername.getText());
                    jBody.put("password", edtPassword.getText());
                    jBody.put("fcm_id", refreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = ServerURL.doLogin;

                ApiVolley request = new ApiVolley(context, jBody, "POST", url, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        if(alert != null) {

                            try {
                                alert.dismiss();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        dialogBox.dismissDialog();

                        try {

                            JSONObject response = new JSONObject(result);
                            String status = response.getJSONObject("metadata").getString("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            message = "Authentikasi berhasil";
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            if(status.equals("200")){

                                String laba = response.getJSONObject("response").getString("laba");
                                if(laba.equals("1")){
                                    saveKunci();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                            setAdapter(null);

                        }

                    }

                    @Override
                    public void onError(String result) {

                        dialogBox.dismissDialog();
                        Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                        setAdapter(null);
                    }
                });

                /*if(alert != null) {

                    try {
                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }*/
            }
        });

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveKunci() {

        dialogBox = new DialogBox(RekapMutasi.this);
        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_awal", tanggalAwal);
            jBody.put("tgl_akhir", tanggalAkhir);
            jBody.put("khusus", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.kunciSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if(status.equals("200")){


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setAdapter(null);

                }

            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setAdapter(null);
            }
        });
    }

    private void initValidation() {

        iv.datePickerEvent(context, edTanggalAwal, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        iv.datePickerEvent(context, edtTanggalAkhir, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
    }

    public static void getDataSetoran() {

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_awal", tanggalAwal);
            jBody.put("tgl_akhir", tanggalAkhir);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSetoranHeader, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listSetoran = new ArrayList<>();
                    double total = 0;

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONObject("response").getJSONArray("setoran");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listSetoran.add(new CustomListItem(
                                    jo.getString("kode_bank"),
                                    jo.getString("bank")
                                    ,jo.getString("total")
                                    ,tanggalAwal
                                    ,tanggalAkhir
                                    ,jo.getString("crbayar")
                                    ,jo.getString("saldo")
                                    ,jo.getString("pengeluaran")
                                    ,jo.getString("sisa")
                                    ));

                            total += (iv.parseNullDouble(jo.getString("total")) + iv.parseNullDouble(jo.getString("sisa")));
                        }

                        JSONObject jumlahRes = response.getJSONObject("response").getJSONObject("jumlah");
                        String totalCustomer = jumlahRes.getString("jumlah_customer");
                        String totalNota = jumlahRes.getString("jumlah_nota");
                        tvTotalCustomer.setText(iv.ChangeToCurrencyFormat(totalCustomer));
                        tvTotalNota.setText(iv.ChangeToCurrencyFormat(totalNota));

                    }

                    tvTotal.setText(iv.ChangeToRupiahFormat(total));
                    setAdapter(listSetoran);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setAdapter(null);

                }

            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setAdapter(null);
            }
        });
    }

    private static void setAdapter(List<CustomListItem> listItem) {

        lvSetoran.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            HeaderRekapSetoranAdapter adapter = new HeaderRekapSetoranAdapter((Activity) context, listItem);

            lvSetoran.setAdapter(adapter);

            lvSetoran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    Intent intent = new Intent(context, RincianSetoran.class);
                    intent.putExtra("kode_bank", item.getListItem1());
                    intent.putExtra("tgl_awal", item.getListItem4());
                    intent.putExtra("tgl_akhir", item.getListItem5());
                    ((Activity) context).startActivity(intent);
                }
            });
        }
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
