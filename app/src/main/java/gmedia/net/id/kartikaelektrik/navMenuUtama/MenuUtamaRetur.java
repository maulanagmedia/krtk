package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturCustomer;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturDatePicker;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturSelisihNota;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturTelahDiproses;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityBonus.Bonus;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaRetur extends Fragment {

    private View layout;
    private Context context;
    private LinearLayout llReturCustomer, llTelahDiproses, llSelisihNota;
    public static final String returCustomer = "RC";
    public static final String returTelahDiproses = "TD";
    public static final String returSelisihNota = "SN";

    public MenuUtamaRetur(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_retur, container, false);
        getActivity().setTitle("Retur");
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

        llReturCustomer = (LinearLayout) layout.findViewById(R.id.ll_retur_customer);
        llTelahDiproses = (LinearLayout) layout.findViewById(R.id.ll_telah_diproses);
        llSelisihNota = (LinearLayout) layout.findViewById(R.id.ll_selisih_nota);

        llReturCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReturDatePicker.class);
                intent.putExtra("jenis", returCustomer);
                context.startActivity(intent);
            }
        });

        llTelahDiproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReturDatePicker.class);
                intent.putExtra("jenis", returTelahDiproses);
                context.startActivity(intent);
            }
        });

        llSelisihNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReturDatePicker.class);
                intent.putExtra("jenis", returSelisihNota);
                context.startActivity(intent);
            }
        });
    }
}
