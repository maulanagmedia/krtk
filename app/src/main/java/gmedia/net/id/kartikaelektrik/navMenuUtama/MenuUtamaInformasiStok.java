package gmedia.net.id.kartikaelektrik.navMenuUtama;

/**
 * Created by indra on 20/12/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityInfoStok.InfoStokBarang;
import gmedia.net.id.kartikaelektrik.activityInfoStok.InfoStokCanvas;

public class MenuUtamaInformasiStok extends android.app.Fragment {

    private View layout;
    private Context context;
    private LinearLayout llStokBarang, llStokCanvas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_informasi_stok, container, false);
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

        llStokBarang = (LinearLayout) layout.findViewById(R.id.ll_stok_barang);
        llStokCanvas = (LinearLayout) layout.findViewById(R.id.ll_stok_canvas);

        llStokBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, InfoStokBarang.class);
                context.startActivity(intent);
            }
        });

        llStokCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoStokCanvas.class);
                context.startActivity(intent);
            }
        });
    }
}
