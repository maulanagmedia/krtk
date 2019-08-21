package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityKomisi.DetailKomisiDenda;
import gmedia.net.id.kartikaelektrik.activityPengeluaran.DetailPengeluaran;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter.ListPengeluaranAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

/**
 * Created by Maul on 2/1/2017.
 */

public class MenuUtamaPengeluaran extends Fragment {

    private Spinner spJenisTanggal;
    private View layout;
    private Context context;
    private TextInputLayout tilTanggalAwal;
    private TextInputLayout tilTanggalAkhir;
    private EditText edtTanggalAwal;
    private EditText edtTanggalAkhir;
    private String formatDate = "", formatDateDisplay = "";
    private ItemValidation iv = new ItemValidation();
    private String tanggalAwal, tanggalAkhir;
    private String idJenisPembayaran = "0";
    private List<CustomListItem> jenisPembayaranList;
    private int jenisPembayaranSelected = 0;
    private Button btnTampilkan;
    private String TAG = "Pengeluaran";
    private EditText edtKeyword;
    private String keyword = "";
    private ListView lvPengeluaran;
    private List<CustomListItem> listData = new ArrayList<>();
    private ListPengeluaranAdapter adapter;
    private Button btnAdd;

    public MenuUtamaPengeluaran(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_pengeluaran, container, false);
        context = getActivity();

        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tilTanggalAwal = (TextInputLayout) layout.findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) layout.findViewById(R.id.til_tanggal_akhir);
        edtTanggalAwal = (EditText) layout.findViewById(R.id.edt_tanggal_awal);
        edtTanggalAwal.setText(iv.getToday(formatDateDisplay));
        edtTanggalAkhir = (EditText) layout.findViewById(R.id.edt_tanggal_akhir);
        edtTanggalAkhir.setText(iv.getToday(formatDateDisplay));
        spJenisTanggal = (Spinner) layout.findViewById(R.id.sp_jenis_tanggal);
        btnTampilkan = (Button) layout.findViewById(R.id.btn_tampilkan);
        edtKeyword = (EditText) layout.findViewById(R.id.edt_keyword);
        btnAdd = (Button) layout.findViewById(R.id.btn_add);
        lvPengeluaran = (ListView) layout.findViewById(R.id.lv_pengeluaran);
        keyword = "";

        listData = new ArrayList<>();
        adapter = new ListPengeluaranAdapter((Activity) context, listData);
        lvPengeluaran.setAdapter(adapter);

        initValidation();

        lvPengeluaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailPengeluaran.class);
                intent.putExtra("id", item.getListItem1());
                ((Activity) context).startActivity(intent);
            }
        });

    }

    private void initEvent() {

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                initData();
            }
        });

        edtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                keyword = editable.toString();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailPengeluaran.class);
                ((Activity)context).startActivity(intent);
            }
        });
    }

    private void initData() {

        /*listData.add(new CustomListItem("1", "2019-08-09", "Testingfghfghfgf fhfgfhgfhfgfhfghfhfhfhfghf fhhgf hfhfhfgfhfghfh", "200000"));
        listData.add(new CustomListItem("2", "2019-08-09", "Testing 1", "300000"));
        listData.add(new CustomListItem("3", "2019-08-09", "Testing 2", "400000"));
        listData.add(new CustomListItem("4", "2019-08-09", "Testing 3", "500000"));

        adapter.notifyDataSetChanged();*/

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("datestart", iv.ChangeFormatDateString(edtTanggalAwal.getText().toString(), formatDateDisplay, formatDate));
            jBody.put("dateend", iv.ChangeFormatDateString(edtTanggalAkhir.getText().toString(), formatDateDisplay, formatDate));
            jBody.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPengeluaran,"", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                listData.clear();
                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");

                        for(int i = 0; i < ja.length();i++){

                            JSONObject jo = ja.getJSONObject(i);
                            listData.add(new CustomListItem(
                                    jo.getString("id")
                                    ,jo.getString("tgl")
                                    ,jo.getString("keterangan")
                                    ,jo.getString("nominal")
                            ));

                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    Toast.makeText(context, "Terjadi kesalahan dalam parsing data", Toast.LENGTH_LONG).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                Toast.makeText(context, "Terjadi kesalahan, harap ulangi proses", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initValidation() {

        iv.datePickerEvent(context, edtTanggalAwal, "RIGHT", formatDateDisplay);
        iv.datePickerEvent(context, edtTanggalAkhir, "RIGHT", formatDateDisplay);
    }
}
