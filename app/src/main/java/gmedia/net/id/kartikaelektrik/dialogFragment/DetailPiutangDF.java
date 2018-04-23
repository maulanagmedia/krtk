package gmedia.net.id.kartikaelektrik.dialogFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/17/2017.
 */

public class DetailPiutangDF extends DialogFragment {

    private View layout;
    private EditText edtJmlPiutang;
    private EditText edtNoNota;
    private EditText edtNamaPelanggan;
    private EditText edtTanggal;
    private EditText edtTanggalTempo;
    private ItemValidation iv = new ItemValidation();

    public static DetailPiutangDF newInstance(String noNota, String namaPelanggan, String tanggal, String tanggalTempo, String jmlPiutang){

        DetailPiutangDF frag = new DetailPiutangDF();
        Bundle args = new Bundle();
        args.putString("nonota", noNota);
        args.putString("namapelanggan", namaPelanggan);
        args.putString("tanggal", tanggal);
        args.putString("tanggaltempo", tanggalTempo);
        args.putString("jmlpiutang", jmlPiutang);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_detail_piutang, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = view;
        initUI(view);
    }

    private void initUI(View view) {

        edtNoNota = (EditText) view.findViewById(R.id.edt_no_nota);
        edtNamaPelanggan = (EditText) view.findViewById(R.id.edt_nama_pelanggan);
        edtTanggal = (EditText) view.findViewById(R.id.edt_tanggal);
        edtTanggalTempo = (EditText) view.findViewById(R.id.edt_tanggal_tempo);
        edtJmlPiutang = (EditText) view.findViewById(R.id.edt_jml_piutang);

        edtNoNota.setText(getArguments().getString("nonota"));
        edtNamaPelanggan.setText(getArguments().getString("namapelanggan"));
        edtTanggal.setText(getArguments().getString("tanggal"));
        edtTanggalTempo.setText(getArguments().getString("tanggaltempo"));
        String jmlPiutang =  iv.ChangeToRupiahFormat(Float.parseFloat(getArguments().getString("jmlpiutang")));
        edtJmlPiutang.setText(jmlPiutang);
    }
}
