package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityHistoryPenjualanBarang.BarangPalingLaku;
import gmedia.net.id.kartikaelektrik.activityHistoryPenjualanBarang.BarangTidakLaku;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuHistoryPenjualanBarang extends Fragment {

    private View layout;
    private Context context;
    private LinearLayout llBarangTidakLaku, llBarangPalingLaku;

    public MenuHistoryPenjualanBarang(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_history_penjualan_barang, container, false);
        getActivity().setTitle("Menu Admin");
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

        llBarangTidakLaku = (LinearLayout) layout.findViewById(R.id.ll_barang_tidak_laku);
        llBarangPalingLaku = (LinearLayout) layout.findViewById(R.id.ll_barang_paling_laku);

        initEvent();
    }

    private void initEvent() {


        llBarangTidakLaku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, BarangTidakLaku.class);
                ((Activity) context).startActivity(intent);
            }
        });

        llBarangPalingLaku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, BarangPalingLaku.class);
                ((Activity) context).startActivity(intent);
            }
        });
    }
}
