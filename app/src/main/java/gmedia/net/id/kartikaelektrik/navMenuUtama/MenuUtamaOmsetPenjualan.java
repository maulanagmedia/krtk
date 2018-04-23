package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityRetur.ReturSelisihNota;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.DatePickerOmset;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.DetailOmsetCustomer;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.ListOmsetBarang;
import gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.ListOmsetCustomer;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.OmsetPenjualan.OmsetPerCustomerTableAdapter;
import gmedia.net.id.kartikaelektrik.adapter.Retur.ReturCustomerTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

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
