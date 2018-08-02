package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.ApprovalHargaSO;
import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturDatePicker;
import gmedia.net.id.kartikaelektrik.R;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaAdmin extends Fragment {

    private View layout;
    private Context context;
    private LinearLayout llLabaRugi, llApproveHargaSO;

    public MenuUtamaAdmin(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_admin, container, false);
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

        llLabaRugi = (LinearLayout) layout.findViewById(R.id.ll_laba_rugi);
        llApproveHargaSO = (LinearLayout) layout.findViewById(R.id.ll_approve_harga_so);

        initEvent();
    }

    private void initEvent() {

        llLabaRugi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ReturDatePicker.class);
                intent.putExtra("jenis", returCustomer);
                context.startActivity(intent);*/
            }
        });

        llApproveHargaSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ApprovalHargaSO.class);
                context.startActivity(intent);
            }
        });
    }
}
