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

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityBonus.Bonus;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaBonus extends Fragment {

    private View layout;
    private Context context;
    public MenuUtamaBonus(){}
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edTanggalAwal;
    private EditText edtTanggalAkhir;
    private LinearLayout llSave;
    private TextView tvSave;
    private ItemValidation iv = new ItemValidation();
    private String tanggalAwal, tanggalAkhir;
    private String formatDate = "", formatDateDisplay = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_bonus, container, false);
        getActivity().setTitle("Bonus");
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
        llSave = (LinearLayout) layout.findViewById(R.id.ll_save_container);
        tvSave = (TextView) layout.findViewById(R.id.tv_save);

        tvSave.setText("Tampilkan Bonus");

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

                Intent intent = new Intent(context, Bonus.class);
                intent.putExtra("tanggalawal",tanggalAwal);
                intent.putExtra("tanggalakhir",tanggalAkhir);
                context.startActivity(intent);
            }
        });
    }

    private void initValidation() {

        iv.datePickerEvent(context, edTanggalAwal, "RIGHT", formatDateDisplay);
        iv.datePickerEvent(context, edtTanggalAkhir, "RIGHT", formatDateDisplay);
    }
}
