package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.MutasiSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.RekapMutasi;
import gmedia.net.id.kartikaelektrik.activitySetoran.TambahSetoran;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaSetoran extends Fragment {

    private View layout;
    private static Context context;
    private LinearLayout llSetoranKhusus;
    private SessionManager session;
    private LinearLayout llTambahSetoran, llMutasiSetoran, llRekapSetoran;
    private CardView cvTambahSetoran, cvMutasiSetoran;
    private TextView tvRekapSetoran;

    public MenuUtamaSetoran(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_setoran, container, false);
        getActivity().setTitle("Setoran");
        context = getActivity();
        initUI();
        return layout;
    }

    public void setView(Context context, View layout){

        this.context = context;
        this.layout = layout;
        initUI();
    }

    private void initUI() {

        session = new SessionManager(context);
        llTambahSetoran = (LinearLayout) layout.findViewById(R.id.ll_tambah_setoran);
        llSetoranKhusus = (LinearLayout) layout.findViewById(R.id.ll_setoran_khusus);
        llMutasiSetoran = (LinearLayout) layout.findViewById(R.id.ll_mutasi_setoran);
        llRekapSetoran = (LinearLayout) layout.findViewById(R.id.ll_rekap_setoran);

        cvTambahSetoran = (CardView) layout.findViewById(R.id.cv_tambah_setoran);
        cvMutasiSetoran = (CardView) layout.findViewById(R.id.cv_mutasi_setoran);
        tvRekapSetoran = (TextView) layout.findViewById(R.id.tv_rekap_setoran);

        if(session.getLevelJabatan().equals("6")){ // Supir

            cvTambahSetoran.setVisibility(View.GONE);
            cvMutasiSetoran.setVisibility(View.GONE);
            tvRekapSetoran.setText("Rekap Biaya");
        }
        initEvent();
    }

    private void initEvent() {

        llTambahSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TambahSetoran.class);
                intent.putExtra("khusus", false);
                ((Activity)context).startActivity(intent);
            }
        });

        llSetoranKhusus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TambahSetoran.class);
                intent.putExtra("khusus", true);
                ((Activity)context).startActivity(intent);
            }
        });

        llMutasiSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MutasiSetoran.class);
                ((Activity)context).startActivity(intent);
            }
        });

        llRekapSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RekapMutasi.class);
                ((Activity)context).startActivity(intent);
            }
        });
    }
}
