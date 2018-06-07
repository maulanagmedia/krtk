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
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaSetoran extends Fragment {

    private View layout;
    private Context context;
    private ListView lvSetoran;
    private ProgressBar pbLoading;
    private FloatingActionButton fabAdd;

    public MenuUtamaSetoran(){}
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edTanggalAwal;
    private EditText edtTanggalAkhir;
    private LinearLayout llSave;
    private TextView tvSave;
    private ItemValidation iv = new ItemValidation();
    private String tanggalAwal, tanggalAkhir;
    private String formatDate = "", formatDateDisplay = "";
    private List<CustomListItem> listSetoran;

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

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tilTanggalAwal = (TextInputLayout) layout.findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) layout.findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) layout.findViewById(R.id.edt_tanggal_awal);
        edTanggalAwal.setText(iv.getToday(formatDateDisplay));
        edtTanggalAkhir = (EditText) layout.findViewById(R.id.edt_tanggal_akhir);
        edtTanggalAkhir.setText(iv.getToday(formatDateDisplay));
        lvSetoran = (ListView) layout.findViewById(R.id.lv_setoran);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);
        fabAdd = (FloatingActionButton) layout.findViewById(R.id.fab_add);
        llSave = (LinearLayout) layout.findViewById(R.id.ll_save_container);
        tvSave = (TextView) layout.findViewById(R.id.tv_save);

        initValidation();

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Mondatory
                if(!iv.mondatoryEdittext(tilTanggalAwal, edTanggalAwal, "Tanggal Awal Tidak Boleh Kosong")){
                    return;
                }

                if(!iv.mondatoryEdittext(tilTanggalAkhir, edtTanggalAkhir, "Tanggal Akhir Tidak Boleh Kosong")){
                    return;
                }

                if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal,formatDateDisplay)){
                    tilTanggalAkhir.setErrorEnabled(true);
                    tilTanggalAkhir.setError("Tanggal Akhir Tidak Dapat Sebelum Tanggal Awal");
                    edtTanggalAkhir.requestFocus();
                    return;
                }else{
                    tilTanggalAkhir.setError(null);
                    tilTanggalAkhir.setErrorEnabled(false);
                }

                tanggalAwal = iv.ChangeFormatDateString(edTanggalAwal.getText().toString(), formatDateDisplay, formatDate);
                tanggalAkhir = iv.ChangeFormatDateString(edtTanggalAkhir.getText().toString(), formatDateDisplay, formatDate);


                getDataSetoran();
            }
        });

        initEvent();
        getDataSetoran();
    }

    private void initEvent() {

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CustomerSetoran.class);
                ((Activity)context).startActivity(intent);
            }
        });

    }

    private void initValidation() {

        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        iv.datePickerEvent(context, edTanggalAwal, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        iv.datePickerEvent(context, edtTanggalAkhir, "RIGHT", formatDateDisplay, iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
    }

    private void getDataSetoran() {

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_awal", tanggalAwal);
            jBody.put("tgl_akhir", tanggalAkhir);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSetoranHeader, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listSetoran = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listSetoran.add(new CustomListItem(jo.getString("kode_bank"),
                                    jo.getString("bank"),
                                    jo.getString("total")));
                        }

                    }
                    setAdapter(listSetoran);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setAdapter(null);

                }

            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setAdapter(null);
            }
        });
    }

    private void setAdapter(List<CustomListItem> listItem) {

        lvSetoran.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            HeaderSetoranAdapter adapter = new HeaderSetoranAdapter((Activity) context, listItem);

            lvSetoran.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getDataSetoran();
    }
}
