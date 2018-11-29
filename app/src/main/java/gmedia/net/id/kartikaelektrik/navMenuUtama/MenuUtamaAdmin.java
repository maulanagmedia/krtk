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
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.HistoryLimitCustomer;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.JatuhTempoGiro;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.LabaRugiOmsetJual;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.LabaRugiOmsetSetoran;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.VerifikasiLimitCustomer;
import gmedia.net.id.kartikaelektrik.MenuAdminPengaturanHeader.DetailPengaturanHeader;
import gmedia.net.id.kartikaelektrik.MenuAdminPerjalananSales.ListSalesPerjalanan;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.ListSalesActivity;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaAdmin extends Fragment {

    private View layout;
    private Context context;
    private LinearLayout llLabaRugiOmsetJual, llApproveHargaSO, llLabaRugiOmsetSetoran, llJatuhTempoGiro, llVerifikasiLimitCustomer, llMasukSebagaiSales;
    private LinearLayout llHistoryLimit;
    private LinearLayout llPerjalananSales;
    private LinearLayout llPengaturanHeader;

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

        llLabaRugiOmsetJual = (LinearLayout) layout.findViewById(R.id.ll_laba_rugi_omset_jual);
        llLabaRugiOmsetSetoran = (LinearLayout) layout.findViewById(R.id.ll_laba_rugi_omset_setoran);
        llApproveHargaSO = (LinearLayout) layout.findViewById(R.id.ll_approve_harga_so);
        llJatuhTempoGiro = (LinearLayout) layout.findViewById(R.id.ll_jatuh_tempo_giro);
        llVerifikasiLimitCustomer = (LinearLayout) layout.findViewById(R.id.ll_verifikasi_limit_customer);
        llHistoryLimit = (LinearLayout) layout.findViewById(R.id.ll_history_limit);
        llPerjalananSales = (LinearLayout) layout.findViewById(R.id.ll_perjalanan_sales);
        llPengaturanHeader = (LinearLayout) layout.findViewById(R.id.ll_pengaturan_header);
        llMasukSebagaiSales = (LinearLayout) layout.findViewById(R.id.ll_masuk_sebagai_sales);

        initEvent();
    }

    private void initEvent(){

        llLabaRugiOmsetJual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LabaRugiOmsetJual.class);
                context.startActivity(intent);
            }
        });

        llLabaRugiOmsetSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LabaRugiOmsetSetoran.class);
                context.startActivity(intent);
            }
        });

        llApproveHargaSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ApprovalHargaSO.class);
                context.startActivity(intent);
            }
        });

        llVerifikasiLimitCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, VerifikasiLimitCustomer.class);
                context.startActivity(intent);
            }
        });

        llJatuhTempoGiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, JatuhTempoGiro.class);
                context.startActivity(intent);
            }
        });

        llMasukSebagaiSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ListSalesActivity.class);
                intent.putExtra("flag", "1");
                context.startActivity(intent);
            }
        });

        llPerjalananSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ListSalesPerjalanan.class);
                intent.putExtra("flag", "1");
                context.startActivity(intent);
            }
        });

        llPengaturanHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailPengaturanHeader.class);
                context.startActivity(intent);
            }
        });

        llHistoryLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, HistoryLimitCustomer.class);
                context.startActivity(intent);
            }
        });
    }
}
