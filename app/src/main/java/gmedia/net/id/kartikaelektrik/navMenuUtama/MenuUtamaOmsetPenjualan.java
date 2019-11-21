package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.DatePickerOmset;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaOmsetPenjualan extends Fragment {

    private View layout;
    private Context context;
    private LinearLayout llOmsetCustomer, llOmsetBarang;

    public MenuUtamaOmsetPenjualan(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_omset_penjualan, container, false);
        getActivity().setTitle("Omset Penjualan");
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

        llOmsetCustomer = (LinearLayout) layout.findViewById(R.id.ll_omset_customer);
        llOmsetBarang = (LinearLayout) layout.findViewById(R.id.ll_omset_barang);

        llOmsetCustomer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DatePickerOmset.class);
                intent.putExtra("kode", "cus");
                context.startActivity(intent);
            }
        });

        llOmsetBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DatePickerOmset.class);
                intent.putExtra("kode", "brg");
                context.startActivity(intent);
            }
        });
    }

}
