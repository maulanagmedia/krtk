package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityDenda.DendaPembayaran;
import gmedia.net.id.kartikaelektrik.activityKomisi.DetailKomisiDenda;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Maul on 2/1/2017.
 */

public class MenuUtamaDenda extends Fragment {

    private Spinner spJenisTanggal;
    private TextInputLayout tilJenisPembayaran;
    private EditText edtJenisPembayaran;
    private View layout;
    private Context context;
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edTanggalAwal;
    private EditText edtTanggalAkhir;
    private String formatDate = "", formatDateDisplay = "";
    private ItemValidation iv = new ItemValidation();
    private String tanggalAwal, tanggalAkhir;
    private String idJenisPembayaran = "0";
    private List<CustomListItem> jenisPembayaranList;
    private int jenisPembayaranSelected = 0;
    private Button btnTampilkan;

    public MenuUtamaDenda(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_denda, container, false);
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

        tilJenisPembayaran = (TextInputLayout) layout.findViewById(R.id.til_jenis_pembayaran);
        tilTanggalAwal = (TextInputLayout) layout.findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) layout.findViewById(R.id.til_tanggal_akhir);
        edtJenisPembayaran = (EditText) layout.findViewById(R.id.edt_jenis_pembayaran);
        edTanggalAwal = (EditText) layout.findViewById(R.id.edt_tanggal_awal);
        edTanggalAwal.setText(iv.getToday(formatDateDisplay));
        edtTanggalAkhir = (EditText) layout.findViewById(R.id.edt_tanggal_akhir);
        edtTanggalAkhir.setText(iv.getToday(formatDateDisplay));
        spJenisTanggal = (Spinner) layout.findViewById(R.id.sp_jenis_tanggal);
        btnTampilkan = (Button) layout.findViewById(R.id.btn_tampilkan);

        initValidation();
        getJenisPembayaran();

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Mondatory

                /*if(!iv.mondatoryEdittext(tilJenisPembayaran, edtJenisPembayaran, "Jenis Pembayaran kosong")){
                    return;
                }*/

                if(!iv.mondatoryEdittext(tilTanggalAwal, edTanggalAwal, "Tanggal Awal Tidak Boleh Kosong")){
                    return;
                }

                if(!iv.mondatoryEdittext(tilTanggalAkhir, edtTanggalAkhir, "Tanggal Akhir Tidak Boleh Kosong")){
                    return;
                }

                if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal, formatDateDisplay)){
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

                Intent intent = new Intent(context, DetailKomisiDenda.class);
                intent.putExtra("jenistanggal", String.valueOf(spJenisTanggal.getSelectedItemPosition()));
                intent.putExtra("jenisPembayaranId", idJenisPembayaran);
                intent.putExtra("jenisPembayaran", edtJenisPembayaran.getText().toString());
                intent.putExtra("tanggalawal",tanggalAwal);
                intent.putExtra("tanggalakhir",tanggalAkhir);
                intent.putExtra("jenis", "DENDA");
                context.startActivity(intent);
            }
        });
    }

    //region getJenisTabungan()
    private void getJenisPembayaran() {

        JSONObject jBody = new JSONObject();

        jenisPembayaranList = new ArrayList<>();
        //TODO: Dummy Komisi
        jenisPembayaranList.add(new CustomListItem("1001","Pembayaran Ketat Biasa"));
        jenisPembayaranList.add(new CustomListItem("1002","Pembayaran Ketat Khusus"));
        jenisPembayaranList.add(new CustomListItem("1003","Pembayaran Ketat Diskon Khusus"));
        jenisPembayaranList.add(new CustomListItem("1004","Pembayaran Ketat Kontan"));
        jenisPembayaranList.add(new CustomListItem("1005","Pembayaran Produk Sekai"));
        jenisPembayaranList.add(new CustomListItem("1006","Pembayaran Biasa (Listrik dan Elektronik)"));
        jenisPembayaranList.add(new CustomListItem("1008","Pembayaran Biasa Tempo Khusus (90)"));
        jenisPembayaranList.add(new CustomListItem("1009","Pembayaran Toko Tempo Khusus (75)"));

        getJenisPembayaranEvent(jenisPembayaranList);
        /*ApiVolley request = new ApiVolley(getContext(), jBody, "GET", "URLe", "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");


                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                        }
                    }

                    //getJenisPembayaranEvent(jenisTabunganList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });*/
    }

    private void getJenisPembayaranEvent(final List<CustomListItem> listItem){

        if(listItem != null && listItem.size() > 0){

            jenisPembayaranSelected = 0;
            edtJenisPembayaran.setText(listItem.get(jenisPembayaranSelected).getListItem2());
            idJenisPembayaran = listItem.get(jenisPembayaranSelected).getListItem1();
            edtJenisPembayaran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tampilJenisPembayaranChosen(listItem);
                }
            });
        }
    }

    private void tampilJenisPembayaranChosen(final List<CustomListItem> listItem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog1);

        CharSequence[] choiceList = new CharSequence[listItem.size()];
        for(int x = 0; x < listItem.size();x++){
            choiceList[x] = listItem.get(x).getListItem2();
        }

        final CharSequence[] finalChoice = choiceList;

        final int selected = jenisPembayaranSelected; // select at 0
        final int[] lastSelected = {jenisPembayaranSelected}; // select at 0

        builder.setSingleChoiceItems(
                finalChoice,
                jenisPembayaranSelected,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {

                        lastSelected[0] = which;
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jenisPembayaranSelected = lastSelected[0];
                edtJenisPembayaran.setText(listItem.get(jenisPembayaranSelected).getListItem2());
                idJenisPembayaran = listItem.get(jenisPembayaranSelected).getListItem1();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jenisPembayaranSelected = selected;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initValidation() {

        iv.datePickerEvent(context, edTanggalAwal, "RIGHT", formatDateDisplay);
        iv.datePickerEvent(context, edtTanggalAkhir, "RIGHT", formatDateDisplay);
    }
}
