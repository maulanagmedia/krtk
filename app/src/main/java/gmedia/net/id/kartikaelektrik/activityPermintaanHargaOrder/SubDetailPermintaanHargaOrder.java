package gmedia.net.id.kartikaelektrik.activityPermintaanHargaOrder;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubDetailPermintaanHargaOrder extends AppCompatActivity {

    private EditText edNamaPelanggan;
    private EditText edNamaBarang;
    private EditText edTanggal;
    private TextInputLayout tilJumlah;
    private EditText edJumlah;
    private EditText edHarga;
    private TextInputLayout tilDiskon;
    private EditText edDiskon;
    private EditText edHargaWithDiskon;
    private TextInputLayout tilHargaTotal;
    private EditText edHargaTotal;
    private SalesOrderDetail sod;
    private String namaPelanggan, tanggal;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail_permintaan_harga_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            edNamaPelanggan = (EditText) findViewById(R.id.edt_nama_pelanggan);
            edNamaBarang = (EditText) findViewById(R.id.edt_nama_barang);
            edTanggal = (EditText) findViewById(R.id.edt_tanggal);
            tilJumlah = (TextInputLayout) findViewById(R.id.til_quantity);
            edJumlah = (EditText) findViewById(R.id.edt_quantity);
            edHarga = (EditText) findViewById(R.id.edt_harga);
            tilDiskon = (TextInputLayout) findViewById(R.id.til_diskon);
            edDiskon = (EditText) findViewById(R.id.edt_diskon);
            edHargaWithDiskon = (EditText) findViewById(R.id.edt_harga_with_dicsount);
            tilHargaTotal = (TextInputLayout) findViewById(R.id.til_harga_total);
            edHargaTotal = (EditText) findViewById(R.id.edt_harga_total);

            Type komisiDendaType = new TypeToken<SalesOrderDetail>(){}.getType();
            Gson gson = new Gson();
            namaPelanggan = bundle.getString("namapelanggan");
            sod = gson.fromJson(bundle.getString("sod"), komisiDendaType);
            tanggal = bundle.getString("tanggal");

            edNamaPelanggan.setText(namaPelanggan);
            edNamaBarang.setText(sod.getNamaBarang());

            String formatDateDisplay = getResources().getString(R.string.format_date_display);
            edTanggal.setText(iv.ChangeFormatDateString(tanggal, getResources().getString(R.string.format_date),formatDateDisplay));
            edJumlah.setText(sod.getJumlah() + " " + sod.getSatuan());
            edHarga.setText(iv.ChangeToRupiahFormat(iv.parseNullFloat(sod.getHarga())));
            edDiskon.setText(sod.getDiskon());

            if(edDiskon.getText().length()>0 && !edDiskon.getText().toString().trim().equals("0")){

                String changedString = edDiskon.getText().toString().replaceAll("[+]", ",");

                // Calculate harga netto * total Diskon
                List<String> diskonList = new ArrayList<String>(Arrays.asList(changedString.split(",")));
                List<Float> diskonListFloat = new ArrayList<Float>();
                for (String diskon: diskonList){

                    try {
                        float x = Float.parseFloat(diskon);
                        diskonListFloat.add(x);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if(diskonListFloat.size()>0 && edHarga.getText().length() > 0){

                    float selectedIsiSatuan = iv.parseNullFloat(sod.getJumlahpcs()) / iv.parseNullFloat(sod.getJumlah());
                    float hargaAwal = iv.parseNullFloat(sod.getHarga()) / selectedIsiSatuan;
                    float newHargaNetto = 0;
                    Integer index = 1;

                    for(float i: diskonListFloat){
                        if(index == 1){
                            float minDiskon = (i / 100 * hargaAwal);
                            newHargaNetto = hargaAwal - (i / 100 * hargaAwal);
                        }else{
                            newHargaNetto = newHargaNetto - ( i / 100 * newHargaNetto);
                        }
                        index++;
                    }

                    edHargaWithDiskon.setText(iv.ChangeToRupiahFormat(newHargaNetto * selectedIsiSatuan));
                }else{
                    // clear field
                    edHargaWithDiskon.setText(iv.ChangeToRupiahFormat(Float.parseFloat("0")));
                }
            }else{
                edHargaWithDiskon.setText(iv.ChangeToRupiahFormat(Float.parseFloat("0")));
            }
            edHargaTotal.setText(iv.ChangeToRupiahFormat(iv.parseNullFloat(sod.getHargaTotal())));
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
