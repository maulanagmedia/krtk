package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityBonus.Bonus;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.HeaderSetoranAdapter;
import gmedia.net.id.kartikaelektrik.activitySetoran.CustomerSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.DetailFormSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.MutasiSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.RekapMutasi;
import gmedia.net.id.kartikaelektrik.activitySetoran.RincianSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.TambahSetoran;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaSetoran extends Fragment {

    private View layout;
    private static Context context;
    private LinearLayout llTambahSetoran, llMutasiSetoran, llRekapSetoran;

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

        llTambahSetoran = (LinearLayout) layout.findViewById(R.id.ll_tambah_setoran);
        llMutasiSetoran = (LinearLayout) layout.findViewById(R.id.ll_mutasi_setoran);
        llRekapSetoran = (LinearLayout) layout.findViewById(R.id.ll_rekap_setoran);
        initEvent();
    }

    private void initEvent() {

        llTambahSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TambahSetoran.class);
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
