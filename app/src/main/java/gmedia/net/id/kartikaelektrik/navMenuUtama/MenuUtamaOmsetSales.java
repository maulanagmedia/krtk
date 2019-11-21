package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaOmsetSales extends Fragment {

    private View layout;
    private Context context;
    private TextInputLayout tilFilter, tilTanggalAwal, tilTanggalAkhir;
    private EditText edtFilter, edTanggalAwal, edtTanggalAkhir;
    private LinearLayout llSave;
    private TextView tvSave;
    private LinearLayout llDateContainer;
    private ItemValidation iv = new ItemValidation();
    private String tanggalAwal, tanggalAkhir;
    private List<CustomListItem> filterList;
    private int filterSelected = 0;
    private String idFilter = "";

    public MenuUtamaOmsetSales(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_omset_sales, container, false);
        getActivity().setTitle("Omset Sales");
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

        tilFilter = (TextInputLayout) layout.findViewById(R.id.til_filter);
        tilTanggalAwal = (TextInputLayout) layout.findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) layout.findViewById(R.id.til_tanggal_akhir);
        llDateContainer = (LinearLayout) layout.findViewById(R.id.ll_date_container);
        edtFilter = (EditText) layout.findViewById(R.id.edt_filter);
        edTanggalAwal = (EditText) layout.findViewById(R.id.edt_tanggal_awal);
        edtTanggalAkhir = (EditText) layout.findViewById(R.id.edt_tanggal_akhir);
        llSave = (LinearLayout) layout.findViewById(R.id.ll_save_container);
        tvSave = (TextView) layout.findViewById(R.id.tv_save);

        tvSave.setText("Tampilkan Omset Sales");
        initValidation();
        getFilterList();

        llSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Mondatory
                if(!iv.mondatoryEdittext(tilFilter, edtFilter, "Filter mohon dipilih")){
                    return;
                }

                if(idFilter.equals("0")){

                    if(!iv.mondatoryEdittext(tilTanggalAwal, edTanggalAwal, "Tanggal Dari tidak boleh kosong")){
                        return;
                    }

                    if(!iv.mondatoryEdittext(tilTanggalAkhir, edtTanggalAkhir, "Tanggal Sampai tidak boleh kosong")){
                        return;
                    }

                    if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal,"yyyy-MM-dd")){
                        tilTanggalAkhir.setErrorEnabled(true);
                        tilTanggalAkhir.setError("Tanggal Akhir Tidak Dapat Sebelum Tanggal Awal");
                        edtTanggalAkhir.requestFocus();
                        return;
                    }else{
                        tilTanggalAkhir.setError(null);
                        tilTanggalAkhir.setErrorEnabled(false);
                    }
                }

                tanggalAwal = edTanggalAwal.getText().toString();
                tanggalAkhir = edtTanggalAkhir.getText().toString();

                /*Intent intent = new Intent(context, DetailKomisiDenda.class);
                intent.putExtra("tanggalawal",tanggalAwal);
                intent.putExtra("tanggalakhir",tanggalAkhir);
                context.startActivity(intent);*/
            }
        });
    }

    private void getFilterList(){

        filterList = new ArrayList<>();
        filterList.add(new CustomListItem("0", "Berdasarkan Tanggal"));
        filterList.add(new CustomListItem("1", "Semua Omset Sales"));

        getFilterEvent(filterList);
    }

    private void getFilterEvent(final List<CustomListItem> listItem){

        if(listItem != null && listItem.size() > 0){

            filterSelected = 0;
            edtFilter.setText(listItem.get(filterSelected).getListItem2());
            idFilter = listItem.get(filterSelected).getListItem1();
            llDateContainer.setVisibility(View.VISIBLE);
            edtFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tampilFilterChosen(listItem);
                }
            });
        }
    }

    private void tampilFilterChosen(final List<CustomListItem> listItem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog1);

        CharSequence[] choiceList = new CharSequence[listItem.size()];
        for(int x = 0; x < listItem.size();x++){
            choiceList[x] = listItem.get(x).getListItem2();
        }

        final CharSequence[] finalChoice = choiceList;

        final int selected = filterSelected; // select at 0
        final int[] lastSelected = {filterSelected}; // select at 0

        builder.setSingleChoiceItems(
                finalChoice,
                filterSelected,
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
                filterSelected = lastSelected[0];
                edtFilter.setText(listItem.get(filterSelected).getListItem2());
                idFilter = listItem.get(filterSelected).getListItem1();
                if(idFilter.equals("0")){
                    llDateContainer.setVisibility(View.VISIBLE);
                }else{
                    llDateContainer.setVisibility(View.GONE);
                }
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterSelected = selected;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initValidation() {

        iv.datePickerEvent(context, edTanggalAwal, "RIGHT", "yyyy-MM-dd");
        iv.datePickerEvent(context, edtTanggalAkhir, "RIGHT", "yyyy-MM-dd");
    }
}
