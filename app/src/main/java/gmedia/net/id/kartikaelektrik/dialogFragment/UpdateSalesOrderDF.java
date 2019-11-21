package gmedia.net.id.kartikaelektrik.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Indra on 1/6/2017.
 */

public class UpdateSalesOrderDF extends DialogFragment {

    private EditText edNoSalesOrder, edNamaPelanggan, edTanggal, edTanggalTempo;
    private String noSalesOrder, kdCustomer, namaPelanggan, tanggal, tanggalTempo;
    private TextInputLayout tilTanggalTempo;
    private LinearLayout llSave;
    private View layout;
    private String urlUpdateSOHeader;
    private ItemValidation iv = new ItemValidation();
    public UpdateSalesOrderDF(){}

    public static UpdateSalesOrderDF newInstance(String noSalesOrder, String kdPelanggan, String namaPelanggan, String tanggal, String tanggalTempo){

        UpdateSalesOrderDF frag = new UpdateSalesOrderDF();
        Bundle args = new Bundle();
        args.putString("nosalesorder", noSalesOrder);
        args.putString("kodepelanggan", kdPelanggan);
        args.putString("namapelanggan", namaPelanggan);
        args.putString("tanggal", tanggal);
        args.putString("tanggaltempo", tanggalTempo);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_update_sales_order, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*// Get field from view
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();*/
        /*getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);*/
        layout = view;
        initUI(view);
    }

    private void initUI(View layoutView) {

        urlUpdateSOHeader = ServerURL.updateSO;

        edNoSalesOrder = (EditText) layoutView.findViewById(R.id.edt_no_sales_order);
        edNamaPelanggan = (EditText) layoutView.findViewById(R.id.edt_nama_pelanggan);
        edTanggal = (EditText) layoutView.findViewById(R.id.edt_tanggal);
        tilTanggalTempo = (TextInputLayout) layoutView.findViewById(R.id.til_tanggal_tempo);
        edTanggalTempo = (EditText) layoutView.findViewById(R.id.edt_tanggal_tempo);
        llSave = (LinearLayout) layoutView.findViewById(R.id.ll_save_container);

        kdCustomer = getArguments().getString("kodepelanggan");
        noSalesOrder = getArguments().getString("nosalesorder");
        namaPelanggan = getArguments().getString("namapelanggan");
        tanggal = getArguments().getString("tanggal");
        tanggalTempo = getArguments().getString("tanggaltempo");

        edNoSalesOrder.setText(noSalesOrder);
        edNamaPelanggan.setText(namaPelanggan);
        edTanggal.setText(tanggal);
        edTanggalTempo.setText(tanggalTempo);

        iv.PreValidateCustomDate(edTanggalTempo,tilTanggalTempo,"yyyy-MM-dd");
        iv.CustomDateFormatCorrection(edTanggalTempo);
        iv.datePickerEvent(layoutView.getContext(),edTanggalTempo,"RIGHT","yyyy-MM-dd");

        initEvent();
    }

    private void initEvent(){
        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tanggalTempo = edTanggalTempo.getText().toString();
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("kdcus",kdCustomer);
                    jsonBody.put("tgl",tanggal);
                    jsonBody.put("tgltempo",tanggalTempo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ApiVolley restService = new ApiVolley(getContext(), jsonBody, "PUT", urlUpdateSOHeader + noSalesOrder, "", "", 0,
                        new ApiVolley.VolleyCallback(){
                            @Override
                            public void onSuccess(String result){

                                JSONObject responseAPI = new JSONObject();
                                try {
                                    responseAPI = new JSONObject(result);
                                    JSONObject jo = responseAPI.getJSONObject("response");
                                    String status = responseAPI.getJSONObject("metadata").getString("status");
                                    String message = responseAPI.getJSONObject("metadata").getString("message");

                                    if(Integer.parseInt(status) == 200){
                                        dismiss();
                                    }

                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
}
