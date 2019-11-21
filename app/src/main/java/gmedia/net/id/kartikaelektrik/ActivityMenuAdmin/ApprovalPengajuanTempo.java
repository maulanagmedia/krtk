package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter.ApvPengajuanTempoAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ApprovalPengajuanTempo extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtKeyword;
    private ListView lvPengajuan;
    private View footerList;
    private List<CustomListItem> listData;
    private ApvPengajuanTempoAdapter adapter;
    private boolean isLoading = false;
    private int start = 0, count = 10;
    private String keyword = "";
    private String formatTimestamp = "", formatDisplay = "";
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pengajuan_tempo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Approval Tempo");
        context = this;
        session = new SessionManager(context);
        initUI();
    }

    private void initUI() {

        edtKeyword = (EditText) findViewById(R.id.edt_keyword);
        lvPengajuan = (ListView) findViewById(R.id.lv_pengajuan);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        formatTimestamp = context.getResources().getString(R.string.format_date1);
        formatDisplay = context.getResources().getString(R.string.format_date_display3);

        initEvent();
        initData();
    }

    private void initEvent() {

        listData = new ArrayList<>();
        //set adapter for autocomplete
        adapter = new ApvPengajuanTempoAdapter((Activity) context, listData);

        //set adapter to autocomplete
        lvPengajuan.setAdapter(adapter);

        isLoading = false;

        lvPengajuan.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvPengajuan.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvPengajuan.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        lvPengajuan.addFooterView(footerList);
                        start += count;
                        initData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        edtKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtKeyword.getText().toString();
                    start = 0;
                    initData();
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

        lvPengajuan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);

                getDialog(item);
            }
        });

    }

    private void getDialog(final CustomListItem item){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_pengajuan_tempo, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvTanggal = (TextView) viewDialog.findViewById(R.id.tv_tanggal);
        final TextView tvMerk = (TextView) viewDialog.findViewById(R.id.tv_merk);
        final TextView tvCustomer = (TextView) viewDialog.findViewById(R.id.tv_customer);
        final TextView tvPengaju = (TextView) viewDialog.findViewById(R.id.tv_pengaju);
        final TextView tvTempo = (TextView) viewDialog.findViewById(R.id.tv_tempo);
        final LinearLayout llTolak = (LinearLayout) viewDialog.findViewById(R.id.ll_tolak);
        final LinearLayout llSetujui = (LinearLayout) viewDialog.findViewById(R.id.ll_setujui);
        final ImageView ivClose = (ImageView) viewDialog.findViewById(R.id.iv_close);
        final TextView tvProses = (TextView) viewDialog.findViewById(R.id.tv_proses);

        tvTanggal.setText(iv.ChangeFormatDateString(item.getListItem2(), formatTimestamp, formatDisplay));
        tvMerk.setText(item.getListItem3());
        tvCustomer.setText(item.getListItem4());
        tvPengaju.setText(item.getListItem6());
        tvTempo.setText(item.getListItem7());

        llTolak.setVisibility(View.VISIBLE);
        tvProses.setText("Setujui");

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(alert != null){
                    try {
                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        llSetujui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validasi
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyetujui pengajuan ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(alert != null){
                                    try {
                                        alert.dismiss();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                saveApprove(item.getListItem1(), "1");
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        llTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menolak pengajuan ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(alert != null){
                                    try {
                                        alert.dismiss();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                saveApprove(item.getListItem1(), "2");
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveApprove(String id, String appStatus) {

        dialogBox = new DialogBox(ApprovalPengajuanTempo.this);
        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", id);
            jBody.put("approval", appStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(context, jBody, "POST", ServerURL.apvPengajuanTempo, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        start = 0;
                        initData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {

        isLoading = true;
        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", edtKeyword.getText().toString());
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", "");
            jBody.put("users_id", "");
            jBody.put("status", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(context, jBody, "POST", ServerURL.getPengajuanTempo, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        lvPengajuan.removeFooterView(footerList);
                        isLoading = false;
                        if(start == 0){

                            listData.clear();
                        }

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    listData.add(new CustomListItem(
                                            jo.getString("id")
                                            ,jo.getString("insert_at")
                                            ,jo.getString("merk") + " - " + jo.getString("jenis")
                                            ,jo.getString("customer")
                                            ,jo.getString("tempo")
                                            ,jo.getString("pengaju")
                                            ,jo.getString("nama_tempo")
                                    ));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        lvPengajuan.removeFooterView(footerList);
                        isLoading = false;
                    }
                });
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

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

}
