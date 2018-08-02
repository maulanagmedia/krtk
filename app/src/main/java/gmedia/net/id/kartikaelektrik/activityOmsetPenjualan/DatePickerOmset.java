package gmedia.net.id.kartikaelektrik.activityOmsetPenjualan;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturCustomer;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturDatePicker;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturSelisihNota;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturTelahDiproses;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaRetur;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DatePickerOmset extends AppCompatActivity {

    private String formatDate;
    private String formatDateDisplay;
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edTanggalAwal;
    private EditText edtTanggalAkhir;
    private Button btnTampilkan;
    private String flag = "";
    private ItemValidation iv = new ItemValidation();
    private String tanggalAwal = "", tanggalAkhir ="";
    private String tanggalAwalH = "";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_omset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        session = new SessionManager(this);
        initUI();
    }

    private void initUI() {

        setTitle("Tenggat Waktu");
        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        tilTanggalAwal = (TextInputLayout) findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        btnTampilkan = (Button) findViewById(R.id.btn_tampilkan);

        tanggalAwalH = iv.sumDate(iv.getToday(formatDateDisplay),-30,formatDateDisplay);
        edTanggalAwal.setText(tanggalAwalH);
        edtTanggalAkhir.setText(iv.getToday(formatDateDisplay));

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            flag = bundle.getString("kode");
        }

        initValidation();
    }

    private void initValidation() {

        iv.datePickerEvent(DatePickerOmset.this, edTanggalAwal, "RIGHT", formatDateDisplay, tanggalAwalH);
        iv.datePickerEvent(DatePickerOmset.this, edtTanggalAkhir, "RIGHT", formatDateDisplay);

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTampilkan();
            }
        });
    }

    private void validateTampilkan() {

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

        Intent intent = new Intent();

        if(!session.getLaba().equals("1")){
            switch(flag){
                case "cus":
                    intent = new Intent(DatePickerOmset.this, ListOmsetCustomer.class);
                    intent.putExtra("tanggalawal", tanggalAwal);
                    intent.putExtra("tanggalakhir", tanggalAkhir);
                    break;
                case "brg":
                    intent = new Intent(DatePickerOmset.this, ListOmsetBarang.class);
                    intent.putExtra("tanggalawal", tanggalAwal);
                    intent.putExtra("tanggalakhir", tanggalAkhir);
                    break;
                default:
                    intent = new Intent(DatePickerOmset.this, ListOmsetCustomer.class);
                    intent.putExtra("tanggalawal", tanggalAwal);
                    intent.putExtra("tanggalakhir", tanggalAkhir);
                    break;
            }
        }else {

            intent = new Intent(DatePickerOmset.this, ListSalesActivity.class);
            intent.putExtra("kode", flag);
            intent.putExtra("tanggalawal", tanggalAwal);
            intent.putExtra("tanggalakhir", tanggalAkhir);
        }


        startActivity(intent);

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
