package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.ApprovalHargaSO;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.ApprovalPengajuanTempo;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.HistoryLimitCustomer;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.JatuhTempoGiro;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.LabaRugiOmsetJual;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.LabaRugiOmsetSetoran;
import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.VerifikasiLimitCustomer;
import gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda.DetailApvHapusDenda;
import gmedia.net.id.kartikaelektrik.MenuAdminPengaturanHeader.DetailPengaturanHeader;
import gmedia.net.id.kartikaelektrik.MenuAdminPerjalananSales.ListSalesPerjalanan;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.ListSalesActivity;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaAdmin extends Fragment {

    private View layout;
    private Context context;
    private CardView llLabaRugiOmsetJual, llApproveHargaSO, llLabaRugiOmsetSetoran, llJatuhTempoGiro
            ,llVerifikasiLimitCustomer, llMasukSebagaiSales, llHistoryLimit,
            llPerjalananSales, llPengaturanHeader, llApvHapusDenda;
    private ItemValidation iv  = new ItemValidation();
    private SessionManager session;
    private CardView llApprovalTempo;

    public MenuUtamaAdmin(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_admin, container, false);
        getActivity().setTitle("Menu Admin");
        context = getActivity();
        session = new SessionManager(context);
        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;
        session = new SessionManager(context);
        initUI();
    }

    private void initUI() {

        llLabaRugiOmsetJual = (CardView) layout.findViewById(R.id.ll_laba_rugi_omset_jual);
        llLabaRugiOmsetSetoran = (CardView) layout.findViewById(R.id.ll_laba_rugi_omset_setoran);
        llApproveHargaSO = (CardView) layout.findViewById(R.id.ll_approve_harga_so);
        llJatuhTempoGiro = (CardView) layout.findViewById(R.id.ll_jatuh_tempo_giro);
        llVerifikasiLimitCustomer = (CardView) layout.findViewById(R.id.ll_verifikasi_limit_customer);
        llHistoryLimit = (CardView) layout.findViewById(R.id.ll_history_limit);
        llPerjalananSales = (CardView) layout.findViewById(R.id.ll_perjalanan_sales);
        llPengaturanHeader = (CardView) layout.findViewById(R.id.ll_pengaturan_header);
        llApvHapusDenda = (CardView) layout.findViewById(R.id.ll_apv_hapus_denda);
        llMasukSebagaiSales = (CardView) layout.findViewById(R.id.ll_masuk_sebagai_sales);
        llApprovalTempo = (CardView) layout.findViewById(R.id.ll_apv_tempo);

        llMasukSebagaiSales.setVisibility(View.GONE);
        llLabaRugiOmsetJual.setVisibility(View.GONE);
        llLabaRugiOmsetSetoran.setVisibility(View.GONE);
        llApproveHargaSO.setVisibility(View.GONE);
        llJatuhTempoGiro.setVisibility(View.GONE);
        llVerifikasiLimitCustomer.setVisibility(View.GONE);
        llHistoryLimit.setVisibility(View.GONE);
        llApvHapusDenda.setVisibility(View.GONE);
        llPengaturanHeader.setVisibility(View.GONE);
        llPerjalananSales.setVisibility(View.GONE);
        llApprovalTempo.setVisibility(View.GONE);

        initData();
        initEvent();
    }

    private void initData() {

        ApiVolley restService = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getMenuAdmin, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    String menu = jo.getString("menu").trim().toLowerCase();
                                    if (menu.equals("masuk_sebagai_sales")){

                                        llMasukSebagaiSales.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("laba_rugi_omset_jual")){

                                        llLabaRugiOmsetJual.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("laba_rugi_omset_setoran")){

                                        llLabaRugiOmsetSetoran.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("approval_harga_so")){

                                        llApproveHargaSO.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("jatuh_tempo_giro")){

                                        llJatuhTempoGiro.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("verifikasi_limit_customer")){

                                        llVerifikasiLimitCustomer.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("history_limit_customer")){

                                        llHistoryLimit.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("verifikasi_hapus_denda")){

                                        llApvHapusDenda.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("pengaturan_header")){

                                        llPengaturanHeader.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("perjalanan_sales")){

                                        llPerjalananSales.setVisibility(View.VISIBLE);
                                    }else if(menu.equals("approval_tempo")){

                                        llApprovalTempo.setVisibility(View.VISIBLE);
                                    }
                                }
                            }else{

                                AlertDialog dialog = new AlertDialog.Builder(context)
                                        .setTitle("Kesalahan")
                                        .setMessage(message +", ulangi proses ?")
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                initData();
                                            }
                                        })
                                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            }

                        }catch (Exception e){

                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("Kesalahan")
                                    .setMessage("Terjadi kesalahan dalam parsing data, ulangi proses ?")
                                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            initData();
                                        }
                                    })
                                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Kesalahan")
                                .setMessage(result + ", ulangi proses ?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        initData();
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

        llApvHapusDenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailApvHapusDenda.class);
                context.startActivity(intent);
            }
        });

        llApprovalTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ApprovalPengajuanTempo.class);
                context.startActivity(intent);
            }
        });
    }
}
