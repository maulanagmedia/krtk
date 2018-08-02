package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.HeaderSetoranAdapter;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.SetoranPernobuktiAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class TambahSetoran extends AppCompatActivity {

    private static Context context;
    private static ListView lvSetoran;
    private static ProgressBar pbLoading;
    private FloatingActionButton fabAdd;
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edTanggalAwal;
    private EditText edtTanggalAkhir;
    private LinearLayout llSave;
    private TextView tvSave;
    private static ItemValidation iv = new ItemValidation();
    private static String tanggalAwal;
    private static String tanggalAkhir;
    private String formatDate = "", formatDateDisplay = "";
    private static List<CustomListItem> listSetoran;
    private static TextView tvTotal;
    private static AutoCompleteTextView actvKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Tambah Setoran");
        context = this;

        initUI();
    }

    private void initUI() {

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        actvKeyword = (AutoCompleteTextView) findViewById(R.id.actv_keyword);
        tilTanggalAwal = (TextInputLayout) findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edTanggalAwal.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        edtTanggalAkhir.setText(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
        lvSetoran = (ListView) findViewById(R.id.lv_setoran);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        llSave = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        initValidation();

        llSave.setOnClickListener(new View.OnClickListener() {
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

        initEvent();
        getDataSetoran();
    }

    private void initEvent() {

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CustomerSetoran.class);
                startActivity(intent);
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
            jBody.put("keyword", actvKeyword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSetoranPerNobukti, "", "", 0, new ApiVolley.VolleyCallback() {
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

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listSetoran.add(new CustomListItem(
                                    jo.getString("nobukti"),
                                    jo.getString("nama"),
                                    jo.getString("tgl_input"),
                                    jo.getString("total")));

                            total += iv.parseNullDouble(jo.getString("total"));
                        }

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

            SetoranPernobuktiAdapter adapter = new SetoranPernobuktiAdapter((Activity) context, listItem);

            lvSetoran.setAdapter(adapter);

            lvSetoran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    Intent intent = new Intent(context, DetailSetoranPerNota.class);
                    intent.putExtra("nobukti", item.getListItem1());
                    intent.putExtra("namacus", item.getListItem2());
                    ((Activity) context).startActivity(intent);

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataSetoran();
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
