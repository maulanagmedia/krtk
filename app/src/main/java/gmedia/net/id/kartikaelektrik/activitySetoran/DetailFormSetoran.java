package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.ListDetailNotaAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailFormSetoran extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private static ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";
    private EditText edtSales;
    private EditText edtCustomer;
    private EditText edtTanggal, edtTanggalTransfer;
    private static EditText edtTotal;
    private RadioGroup rgCaraBayar;
    private RadioButton rbTunai, rbBank, rbGiro;
    private Spinner spBank;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    private String kdcus = "", namaCus = "";
    private String crBayar = "";
    private ProgressBar pbLoading;
    private List<OptionItem> listBank;
    private String currentString = "", currentStringDiskon = "";
    private String idSetoran = "";
    private boolean isEdit = false;
    private EditText edtDariBank, edtDariNorek, edtKeBank, edtKeNorek, edtNamaPemilik;
    private ListView lvNota;
    private List<OptionItem> listNota;
    private static EditText edtSisa;
    public static double sisaPiutang = 0;
    private static ListDetailNotaAdapter adapterPiutangSales;
    private TextInputLayout tilNamaPemilik, tilTanggalTransfer;
    private boolean isConfirm = true;
    private TextView tvTotalPiutang;
    private double totalPiutangCurrent = 0;
    private LinearLayout llDiskon;
    private EditText edtDiskon;
    private EditText edtHargaDiskon;
    private EditText edtTotalDibayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Setoran");
        context = this;
        session = new SessionManager(context);

        intiUI();
    }

    private void intiUI() {

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);

        tilNamaPemilik = (TextInputLayout) findViewById(R.id.til_nama_pemilik);
        tilTanggalTransfer = (TextInputLayout) findViewById(R.id.til_tanggal_transfer);
        edtSales = (EditText) findViewById(R.id.edt_sales);
        edtCustomer = (EditText) findViewById(R.id.edt_customer);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        edtTanggalTransfer = (EditText) findViewById(R.id.edt_tanggal_transfer);
        rgCaraBayar = (RadioGroup) findViewById(R.id.rg_crbayar);
        rbTunai = (RadioButton) findViewById(R.id.rb_tunai);
        rbBank = (RadioButton) findViewById(R.id.rb_bank);
        rbGiro = (RadioButton) findViewById(R.id.rb_giro);
        spBank = (Spinner) findViewById(R.id.sp_bank);
        edtDariBank = (EditText) findViewById(R.id.edt_dari_bank);
        edtNamaPemilik = (EditText) findViewById(R.id.edt_nama_pemilik);
        edtDariNorek = (EditText) findViewById(R.id.edt_dari_norek);
        edtKeBank = (EditText) findViewById(R.id.edt_ke_bank);
        edtKeNorek = (EditText) findViewById(R.id.edt_ke_norek);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        edtSisa = (EditText) findViewById(R.id.edt_sisa);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        lvNota = (ListView) findViewById(R.id.lv_nota);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setText("Simpan Setoran");
        tvTotalPiutang = (TextView) findViewById(R.id.tv_total_piutang);

        llDiskon = (LinearLayout) findViewById(R.id.ll_diskon);
        edtDiskon = (EditText) findViewById(R.id.edt_diskon);
        edtHargaDiskon = (EditText) findViewById(R.id.edt_harga_diskon);
        edtTotalDibayar = (EditText) findViewById(R.id.edt_total_dibayar);

        isConfirm = true;
        sisaPiutang = 0;
        isEdit = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idSetoran = bundle.getString("id", "");
            if(!idSetoran.equals("")){

                //llSaveContainer.setEnabled(false);
                isEdit = true;
                tvSave.setText("Hapus Setoran");
            }else{
                kdcus = bundle.getString("kdcus", "");
                namaCus = bundle.getString("namacus", "");
                edtCustomer.setText(namaCus);

                getPiutangSales();
            }

            edtSales.setText(session.getFullName());
            initEvent();
        }
    }

    private void getPiutangSales() {

        /*pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPiutangSales, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listNota = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jArray = response.getJSONArray("response");

                        for(int i = 0; i < jArray.length(); i++){

                            JSONObject jo = jArray.getJSONObject(i);
                            listNota.add(new OptionItem(
                                    jo.getString("nonota"),
                                    jo.getString("tgl"),
                                    jo.getString("sisa"),
                                    "0", // terbayar
                                    jo.getString("tanda"),
                                    false // checked
                            ));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    List<OptionItem> listItem = new ArrayList<>(listNota);
                    setSalesPiutang(listItem);

                } catch (JSONException e) {

                    setSalesPiutang(null);
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                setSalesPiutang(null);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
            }
        });*/

        totalPiutangCurrent = 0;
        for(OptionItem item : ListNotaPiutang.listNota){

            totalPiutangCurrent += iv.parseNullDouble(item.getAtt1());
        }

        tvTotalPiutang.setText("Total Piutang : "+ iv.ChangeToCurrencyFormat(iv.doubleToStringFull(totalPiutangCurrent)));
        setSalesPiutang(ListNotaPiutang.listNota);
    }

    private void setSalesPiutang(List<OptionItem> listItems){

        lvNota.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            if(listItems.size() == 1){
                llDiskon.setVisibility(View.VISIBLE);
            }else{
                llDiskon.setVisibility(View.GONE);
            }

            adapterPiutangSales = new ListDetailNotaAdapter((Activity) context, listItems);
            lvNota.setAdapter(adapterPiutangSales);
        }
    }

    private void getDetailSetoran() {

        pbLoading.setVisibility(View.VISIBLE);

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getDetailSetoran+idSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        edtCustomer.setText(jo.getString("nama_customer"));
                        crBayar = jo.getString("crbayar");

                        if(crBayar.equals("T")){
                            rbTunai.setChecked(true);
                        }else if(crBayar.equals("B")){
                            rbBank.setChecked(true);
                        }else{
                            rbGiro.setChecked(true);
                        }

                        int position = 0;
                        for(int i = 0; i < listBank.size();i++){
                            if(listBank.get(i).getValue().equals(jo.getString("kode_bank"))){
                                position = i;
                                break;
                            }
                        }

                        spBank.setSelection(position);
                        edtTanggal.setText(iv.ChangeFormatDateString(jo.getString("tanggal"), formatDate, formatDateDisplay));
                        edtTotal.setText(jo.getString("total"));

                        edtDiskon.setText(jo.getString("diskon"));
                        edtHargaDiskon.setText(jo.getString("totaldiskon"));
                        edtDariBank.setText(jo.getString("dari_bank"));
                        edtDariNorek.setText(jo.getString("dari_rekening"));
                        edtNamaPemilik.setText(jo.getString("daripemilik"));

                        edtKeBank.setText(jo.getString("bank"));
                        edtKeNorek.setText(jo.getString("norekening"));
                        edtTanggalTransfer.setText(iv.ChangeFormatDateString(jo.getString("tgltransfer"), formatDate, formatDateDisplay));

                        JSONArray jPiutang = jo.getJSONArray("piutang");
                        listNota = new ArrayList<>();
                        for(int j = 0; j < jPiutang.length(); j++){

                            JSONObject jdp = jPiutang.getJSONObject(j);
                            listNota.add(new OptionItem(
                                    jdp.getString("nonota"),
                                    jdp.getString("tanggal"),
                                    jdp.getString("sisa"),
                                    jdp.getString("jumlah"),
                                    jdp.getString("tanda"),
                                    true));
                        }

                        setSalesPiutang(listNota);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEvent() {

        edtTanggal.setText(iv.getCurrentDate(formatDateDisplay));
        edtTanggal.setKeyListener(null);

        edtTanggalTransfer.setText(iv.getCurrentDate(formatDateDisplay));
        edtTanggalTransfer.setKeyListener(null);
        iv.datePickerEvent(context,edtTanggalTransfer,"RIGHT",formatDateDisplay, iv.getCurrentDate(formatDateDisplay));

        rgCaraBayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                getDataBank();
            }
        });

        edtTotal.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtTotal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtTotal.setText(formatted);
                    edtTotal.setSelection(formatted.length());

                    if(adapterPiutangSales != null){

                        adapterPiutangSales.resetData();
                    }

                    if(isEdit){
                        sisaPiutang = 0;
                        edtSisa.setText(iv.ChangeToRupiahFormat(sisaPiutang));
                    }else{
                        sisaPiutang = iv.parseNullDouble(cleanString);
                        edtSisa.setText(iv.ChangeToRupiahFormat(sisaPiutang));
                    }

                    edtDiskon.setText("");
                    edtTotal.addTextChangedListener(this);
                }
            }
        });

        edtDiskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                calculateDiscount();
            }
        });

        edtHargaDiskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentStringDiskon)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtHargaDiskon.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentStringDiskon = formatted;
                    hitungTotalDibayar();
                    edtHargaDiskon.setText(formatted);
                    edtHargaDiskon.setSelection(formatted.length());
                    edtHargaDiskon.addTextChangedListener(this);
                }
            }
        });

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEdit){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.kartika_logo)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin menghapus data?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteData();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }else{

                    validateBeforeSave();
                }

            }
        });

        if(totalPiutangCurrent > 0) edtTotal.setText(iv.doubleToString(totalPiutangCurrent));
        getDataBank();
    }

    private void hitungTotalDibayar() {

        String totalBayar = edtTotal.getText().toString().replaceAll("[,.]", "");
        String totalHargaDiskon = edtHargaDiskon.getText().toString().replaceAll("[,.]", "");
        edtTotalDibayar.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(iv.parseNullDouble(totalBayar) - iv.parseNullDouble(totalHargaDiskon))));
    }

    private void calculateDiscount(){

        String changedString = edtDiskon.getText().toString().replaceAll("[+]", ",");

        List<String> diskonList = new ArrayList<String>(Arrays.asList(changedString.split(",")));
        List<Double> diskonListDouble = new ArrayList<Double>();
        for (String diskon: diskonList){

            try {
                Double x = iv.parseNullDouble(diskon);
                diskonListDouble.add(x);
            }catch (Exception e){
                Double df = Double.valueOf(0);
                diskonListDouble.add(df);
                e.printStackTrace();
            }
        }

        if(diskonListDouble.size()>0 && edtTotal.getText().length() > 0){

            String hargaClean = edtTotal.getText().toString().replaceAll("[,.]", "");
            double hargaAwal = iv.parseNullDouble(hargaClean);
            double newHargaNetto = 0;
            double totalDiskon = 0;
            Integer index = 1;

            if(diskonListDouble.size() > 0){
                for(Double i: diskonListDouble){
                    if(index == 1){

                        //double minDiskon = i / 100 * hargaAwal;
                        newHargaNetto = hargaAwal - (i / 100 * hargaAwal);
                        totalDiskon += (i / 100 * hargaAwal);
                    }else{

                        totalDiskon += (i / 100 * newHargaNetto);
                        newHargaNetto = newHargaNetto - ( i / 100 * newHargaNetto);
                    }
                    index++;
                }
            }else{
                newHargaNetto = hargaAwal;
            }
            edtHargaDiskon.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(totalDiskon)));

        }else{
            // clear field
            edtHargaDiskon.setText("0");
        }
    }

    public static void updateSisa(){

        edtSisa.setText(iv.ChangeToRupiahFormat(sisaPiutang));
    }

    private void getDataBank() {

        pbLoading.setVisibility(View.VISIBLE);

        tilNamaPemilik.setHint("Nama Sumber (Pemilik)");
        //edtNamaPemilik.setHint("Nama Pemilik");
        tilTanggalTransfer.setHint("Tanggal Transfer");
        //edtTanggalTransfer.setHint("Tanggal Transfer");

        if(rbTunai.isChecked()){
            crBayar = "T";
        }else if(rbBank.isChecked()){
            crBayar = "B";
        }else{
            crBayar = "G";

            tilNamaPemilik.setHint("Nomor Giro");
            tilTanggalTransfer.setHint("Tanggal Giro Jatuh Tempo");
            //edtNamaPemilik.setHint("Nomor Giro");
            //edtTanggalTransfer.setHint("Tanggal Giro");
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("cara_bayar", crBayar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getMasterBayar, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listBank = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBank.add(new OptionItem(
                                    jo.getString("kode"),
                                    jo.getString("nama"),
                                    jo.getString("norekening")));
                        }
                    }else{

                        if(!crBayar.equals("T"))
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    setBankAdapter(listBank);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setBankAdapter(null);
                }

                if(!idSetoran.equals("")) getDetailSetoran();
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setBankAdapter(null);
                if(!idSetoran.equals("")) getDetailSetoran();
            }
        });
    }

    private void validateBeforeSave(){

        if(spBank.getAdapter() == null){

            Toast.makeText(context, "Tunggu hingga data termuat",Toast.LENGTH_LONG).show();
            return;
        }

        if(edtTotal.getText().toString().isEmpty() || edtTotal.getText().toString().equals("0")){

            edtTotal.setError("Total harap diisi");
            edtTotal.requestFocus();

            //Toast.makeText(context, "Total masih kosong, periksa data anda", Toast.LENGTH_LONG).show();
            return;
        }else{
            edtTotal.setError(null);
        }

        if(sisaPiutang != 0){

            Toast.makeText(context, "Sisa harus habis atau Rp 0", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setIcon(R.mipmap.kartika_logo)
                .setMessage("Apakah anda yakin ingin menambahkan ke daftar setoran?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveData();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();

    }

    public void deleteData() {

        llSaveContainer.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), new JSONObject(), "GET", ServerURL.deleteSetoran+idSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                llSaveContainer.setEnabled(true);
                progressDialog.dismiss();
                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        onBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                llSaveContainer.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveData() {

        /*llSaveContainer.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();*/

        String namaBank = ((OptionItem) spBank.getSelectedItem()).getText();
        String kodeBank = ((OptionItem) spBank.getSelectedItem()).getValue();

        List<OptionItem> bufferItems = new ArrayList<>();

        if(adapterPiutangSales != null){

            List<OptionItem> listItems = adapterPiutangSales.getItems();
            int i = 0;
            for(OptionItem item: listItems){

                if(item.isSelected()){

                    JSONObject jOrder = new JSONObject();
                    try {

                        String total = (listItems.size() == 1
                                && !edtTotalDibayar.getText().toString().isEmpty()
                                && !edtTotalDibayar.getText().toString().equals("0")) ? edtTotalDibayar.getText().toString() : item.getAtt2();
                        total = total.replaceAll("[,.]", "");
                        jOrder.put("nonota", item.getValue());
                        jOrder.put("jumlah", total);
                        jOrder.put("crbayar", crBayar);
                        jOrder.put("kode_bank", kodeBank);
                        jOrder.put("bank", namaBank);
                        jOrder.put("kdcus", kdcus);
                        jOrder.put("total", edtTotal.getText().toString().replaceAll("[,.]", ""));
                        jOrder.put("diskon", edtDiskon.getText().toString());
                        jOrder.put("totaldiskon", edtHargaDiskon.getText().toString().replaceAll("[,.]", ""));
                        jOrder.put("dari_bank", edtDariBank.getText().toString());
                        jOrder.put("dari_pemilik", edtNamaPemilik.getText().toString());
                        jOrder.put("dari_rekening", edtDariNorek.getText().toString());
                        jOrder.put("norekening", edtDariNorek.getText().toString());
                        jOrder.put("tgltransfer", iv.ChangeFormatDateString(edtTanggalTransfer.getText().toString(), formatDateDisplay, formatDate));
                        ListNotaPiutang.jaSetoran.put(jOrder);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(item.getAtt1().equals(item.getAtt2())){

                        //ListNotaPiutang.listNota.remove(i);
                    }else{

                        OptionItem newItem = new OptionItem(
                                item.getValue(),
                                item.getText(),
                                iv.doubleToStringFull(iv.parseNullDouble(item.getAtt1()) - iv.parseNullDouble(item.getAtt2())),
                                "0",
                                item.getAtt3(),
                                false
                        );

                        bufferItems.add(newItem);
                    }
                }else{

                    bufferItems.add(item);
                }

                i++;
            }

            ListNotaPiutang.listNota = new ArrayList<>(bufferItems);
        }

        Intent intent = new Intent(context, DetailCheckoutSetoran.class);
        intent.putExtra("kdcus", kdcus);
        intent.putExtra("namacus", namaCus);
        startActivity(intent);
        finish();

        /*ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jsonBody, "POST", ServerURL.saveSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                llSaveContainer.setEnabled(true);
                progressDialog.dismiss();
                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        onBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                llSaveContainer.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });*/
    }

    private void setBankAdapter(List<OptionItem> listItem) {

        spBank.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spBank.setAdapter(adapter);

            spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OptionItem item = (OptionItem) parent.getItemAtPosition(position);
                    edtKeBank.setText(item.getText());
                    edtKeNorek.setText(item.getAtt1());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(isConfirm){

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Pembatalan")
                    .setMessage("Setoran belum terproses, anda yakin membatalkan proses setoran?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            isConfirm = false;
                            onBackPressed();
                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            isConfirm = true;
                        }
                    }).show();
        }else{

            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }
}
