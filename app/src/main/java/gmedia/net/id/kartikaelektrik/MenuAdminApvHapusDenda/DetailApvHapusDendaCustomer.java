package gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda;

import android.app.Activity;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda.Adapter.ListPiutangDendaCustomerdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailApvHapusDendaCustomer extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private ListPiutangDendaCustomerdapter adapter;
    private List<OptionItem> masterList = new ArrayList<>();
    private ItemValidation iv = new ItemValidation();
    private ListView lvDenda;
    private TextView tvTotalNota, tvTotalDenda;
    private String kdcus = "", nama = "";
    private LinearLayout llTerima, llTolak;
    double totalNota = 0, totalDenda = 0;

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

        adapter = new ListPiutangDendaCustomerdapter((Activity) context, masterList);
        lvDenda.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            nama = bundle.getString("nama", "");

            getSupportActionBar().setSubtitle(nama);
        }
    }

    private void initEvent() {

        llTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(totalNota == 0){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Pengirangan")
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
                        .setMessage("Apakah anda yakin ingin memproses penghapusan denda?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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

                if(totalNota == 0){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Pengirangan")
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
                        .setMessage("Apakah anda yakin ingin memproses penghapusan denda?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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

    private void initData() {

        masterList.clear();
        masterList.add(new OptionItem(
                "1"
                ,"2012341"
                , "2018-12-10"
                , "20000"
                , "0"
                , "20000"
                , false
        ));

        masterList.add(new OptionItem(
                "1"
                ,"2012341"
                , "2018-12-10"
                , "20000"
                , "0"
                , "20000"
                , false
        ));

        masterList.add(new OptionItem(
                "1"
                ,"2012341"
                , "2018-12-10"
                , "20000"
                , "0"
                , "20000"
                , false
        ));

        adapter.notifyDataSetChanged();
    }

    public void updateCCID(){

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
