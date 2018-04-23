package gmedia.net.id.kartikaelektrik.navMenuUtama;

/**
 * Created by indra on 20/12/2016.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPiutang.DetailListPiutangCustomer;
import gmedia.net.id.kartikaelektrik.activityPiutang.DetailPiutangJatuhTempo;
import gmedia.net.id.kartikaelektrik.activityPiutang.DetailPiutangPerNota;
import gmedia.net.id.kartikaelektrik.activityPiutang.ListPiutangPerCustomer;

public class MenuUtamaTagihanPiutang extends Fragment {

    private View layout;
    private Context context;
    private LinearLayout llRekapPerCustomer, llDetailPerNota, llJatuhTempo;
    public static String kodeJatuhTempo = "JT";

    public MenuUtamaTagihanPiutang(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_tagihan_piutang, container, false);
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

        llRekapPerCustomer = (LinearLayout) layout.findViewById(R.id.ll_recap_percustomer);
        llDetailPerNota = (LinearLayout) layout.findViewById(R.id.ll_detail_pernota);
        llJatuhTempo = (LinearLayout) layout.findViewById(R.id.ll_jatuh_tempo);
        initEvent();
    }

    private void initEvent() {

        llRekapPerCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ListPiutangPerCustomer.class);
                context.startActivity(intent);
            }
        });

        llDetailPerNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPiutangPerNota.class);
                context.startActivity(intent);
            }
        });

        llJatuhTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailPiutangJatuhTempo.class);
                intent.putExtra("kode", kodeJatuhTempo);
                context.startActivity(intent);
            }
        });
    }

}
