package gmedia.net.id.kartikaelektrik.activityBonus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.model.DetailItemBonus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SubDetailBonus extends AppCompatActivity {

    private EditText edtNoBukti;
    private EditText edtNoProgram;
    private EditText edtMerk;
    private EditText edtNilaiBonus;
    private EditText edtBonus;
    private EditText edtKeterangan;
    private DetailItemBonus dib;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail_bonus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        edtNoBukti = (EditText) findViewById(R.id.edt_no_bukti);
        edtNoProgram = (EditText) findViewById(R.id.edt_no_program);
        edtMerk = (EditText) findViewById(R.id.edt_merk);
        edtNilaiBonus = (EditText) findViewById(R.id.edt_nilai_bonus);
        edtBonus = (EditText) findViewById(R.id.edt_bonus);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            String bonusString = bundle.getString("bonus");

            Type BonusType = new TypeToken<DetailItemBonus>(){}.getType();
            Gson gson = new Gson();
            dib = gson.fromJson(bonusString, BonusType);

            edtNoBukti.setText(dib.getNoBukti());
            edtNoProgram.setText(dib.getNoProgram());
            edtMerk.setText(dib.getMerk());
            String nilaiBonus = dib.getNilai();
            if(dib.getFlag().toUpperCase().equals("P")){
                nilaiBonus = nilaiBonus + " %";
            }else if(dib.getFlag().toUpperCase().equals("R")){
                nilaiBonus = iv.ChangeToRupiahFormat(Float.parseFloat(dib.getNilai()));
            }
            edtNilaiBonus.setText(nilaiBonus);
            edtBonus.setText(iv.ChangeToRupiahFormat(Float.parseFloat(dib.getBonus())));
            edtKeterangan.setText(dib.getKeterangan());
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
