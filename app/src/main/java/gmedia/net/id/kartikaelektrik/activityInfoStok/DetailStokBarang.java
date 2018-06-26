package gmedia.net.id.kartikaelektrik.activityInfoStok;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailStokBarang extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtNamaBarang, edtGudangBesar, edtGudangKecil, edtGudangCanvas, edtRetur;
    private String kodeBrg = "", namaBrg = "";

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

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kodeBrg = bundle.getString("id", "");
            namaBrg = bundle.getString("nama", "");

            edtNamaBarang.setText(namaBrg);
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
