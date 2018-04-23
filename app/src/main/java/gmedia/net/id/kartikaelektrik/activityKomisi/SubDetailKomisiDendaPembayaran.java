package gmedia.net.id.kartikaelektrik.activityKomisi;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPiutang.SubDetailPiutangJatuhTempo;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.model.KomisiDenda;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SubDetailKomisiDendaPembayaran extends AppCompatActivity {

    private EditText edtNoNota;
    private EditText edtNamaPelanggan;
    private EditText edtTanggal;
    private EditText edtPiutang;
    private EditText edtPotongan;
    private EditText edtJumlah;
    private EditText edtTanggalBayar;
    private EditText edtBayar;
    private EditText edtSelisihHari;
    private EditText edtPersenKomisi;
    private EditText edtKomisi;
    private EditText edtJenisPembayaran;
    private KomisiDenda komisi;
    private Boolean komisiMode = true;
    private String TAG = "SubDetail";
    private ItemValidation iv = new ItemValidation();
    private TextInputLayout tilPersenKomisi;
    private TextInputLayout tilKomisi;
    private Button btnTampilkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail_komisi_pembayaran);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        initUI();
    }

    private void initUI() {

        edtNoNota = (EditText) findViewById(R.id.edt_no_nota);
        edtNamaPelanggan = (EditText) findViewById(R.id.edt_nama_pelanggan);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        edtPiutang = (EditText) findViewById(R.id.edt_piutang);
        edtPotongan = (EditText) findViewById(R.id.edt_potongan);
        edtJumlah = (EditText) findViewById(R.id.edt_jumlah);
        edtTanggalBayar = (EditText) findViewById(R.id.edt_tanggal_bayar);
        edtBayar = (EditText) findViewById(R.id.edt_bayar);
        edtSelisihHari = (EditText) findViewById(R.id.edt_selisih_hari);
        tilPersenKomisi = (TextInputLayout) findViewById(R.id.til_persen_komisi);
        edtPersenKomisi = (EditText) findViewById(R.id.edt_persen_komisi);
        tilKomisi = (TextInputLayout) findViewById(R.id.til_komisi);
        edtKomisi = (EditText) findViewById(R.id.edt_komisi);
        edtJenisPembayaran = (EditText) findViewById(R.id.edt_jenis_pembayaran);
        btnTampilkan = (Button) findViewById(R.id.btn_tampilkan);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String komisiDendaString = bundle.getString("list");
            String jenis = bundle.getString("jenis");

            if(jenis.equals("KOMISI")){
                komisiMode = true;
                setTitle("Detail Komisi");
                tilPersenKomisi.setHint("Persen Komisi");
                edtPersenKomisi.setHint("Persen Komisi");
                tilKomisi.setHint("Komisi");
                edtKomisi.setHint("Komisi");
            }else{
                setTitle("Detail Denda");
                tilPersenKomisi.setHint("Persen Denda");
                edtPersenKomisi.setHint("Persen Denda");
                tilKomisi.setHint("Denda");
                edtKomisi.setHint("Denda");
            }

            Type komisiDendaType = new TypeToken<KomisiDenda>(){}.getType();
            Gson gson = new Gson();
            komisi = gson.fromJson(komisiDendaString, komisiDendaType);

            edtNoNota.setText(komisi.getNoNota());
            edtNamaPelanggan.setText(komisi.getNamaPelanggan());
            String formatDate = getResources().getString(R.string.format_date);
            String formatDate1 = getResources().getString(R.string.format_date1);
            String formatDateDisplay = getResources().getString(R.string.format_date_display);
            edtTanggal.setText(iv.ChangeFormatDateString(komisi.getTanggal(),formatDate, formatDateDisplay));
            edtPiutang.setText(iv.ChangeToRupiahFormat(Float.parseFloat(komisi.getPiutang())));
            edtPotongan.setText(iv.ChangeToRupiahFormat(Float.parseFloat(komisi.getPotongan())));
            edtJumlah.setText(iv.ChangeToRupiahFormat(Float.parseFloat(komisi.getJumlah())));
            edtTanggalBayar.setText(iv.ChangeFormatDateString(komisi.getTanggalBayar(), formatDate1, formatDateDisplay));
            edtBayar.setText(komisi.getBayar());
            edtSelisihHari.setText(komisi.getSelisihHari());
            edtPersenKomisi.setText(komisi.getPersenKomisiDenda() + " %");
            edtKomisi.setText(iv.ChangeToRupiahFormat(Float.parseFloat(komisi.getNilaiKomisiDenda())));
            edtJenisPembayaran.setText(komisi.getPembayaran());

            btnTampilkan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(SubDetailKomisiDendaPembayaran.this, SubDetailPiutangJatuhTempo.class);
                    intent.putExtra("nobukti", komisi.getNoNota());
                    intent.putExtra("namacus", komisi.getNamaPelanggan());
                    intent.putExtra("total", iv.ChangeToRupiahFormat(iv.parseNullDouble(komisi.getPiutang())));
                    startActivity(intent);
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
