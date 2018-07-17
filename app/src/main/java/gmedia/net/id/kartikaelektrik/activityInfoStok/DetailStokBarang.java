package gmedia.net.id.kartikaelektrik.activityInfoStok;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailStokBarang extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtNamaBarang, edtGudangBesar, edtGudangKecil, edtGudangCanvas, edtRetur;
    private String kodeBrg = "", namaBrg = "";
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stok_barang);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Detail Stok Barang");

        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        edtNamaBarang = (EditText) findViewById(R.id.edt_nama_barang);
        edtGudangBesar = (EditText) findViewById(R.id.edt_gudang_besar);
        edtGudangKecil = (EditText) findViewById(R.id.edt_gudang_kecil);
        edtGudangCanvas = (EditText) findViewById(R.id.edt_gudang_canvas);
        edtRetur = (EditText) findViewById(R.id.edt_retur);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kodeBrg = bundle.getString("id", "");
            namaBrg = bundle.getString("nama", "");

            edtNamaBarang.setText(namaBrg);

            getDetailStokBarang();
        }
    }

    private void getDetailStokBarang() {

        pbLoading.setVisibility(View.VISIBLE);
        ApiVolley restService = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getDetailStokBarang+kodeBrg, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        pbLoading.setVisibility(View.GONE);
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                JSONObject jo = responseAPI.getJSONObject("response");
                                edtGudangBesar.setText(jo.getString("gudangbesar"));
                                edtGudangKecil.setText(jo.getString("gudangkecil"));
                                edtGudangCanvas.setText(jo.getString("canvas"));
                                edtRetur.setText(jo.getString("retur"));
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
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
