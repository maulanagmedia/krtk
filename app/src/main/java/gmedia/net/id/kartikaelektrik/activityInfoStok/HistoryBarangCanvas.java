package gmedia.net.id.kartikaelektrik.activityInfoStok;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityInfoStok.Adapter.HistoryBarangCanvasAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class HistoryBarangCanvas extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private EditText edtAwal, edtAkhir;
    private LinearLayout llShow, llContainer;
    private ListView lvHistory;
    private TextView tvTotal;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private String formatDate = "", formatDateDisplay = "", tanggalAwal = "", tanggalAkhir = "";
    private String kdbrg = "", nmbrg = "";
    private List<CustomListItem> listHistory = new ArrayList<>();
    private HistoryBarangCanvasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_barang_canvas);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("History Barang Canvas");
        context = this;
        session = new SessionManager(context);

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        edtAwal = (EditText) findViewById(R.id.edt_awal);
        edtAkhir = (EditText) findViewById(R.id.edt_akhir);
        llShow = (LinearLayout) findViewById(R.id.ll_show);
        lvHistory = (ListView) findViewById(R.id.lv_history);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        tanggalAwal = iv.sumDate(iv.getCurrentDate(formatDate), -7, formatDate);
        tanggalAkhir = iv.getCurrentDate(formatDate);

        edtAwal.setText(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
        edtAkhir.setText(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdbrg = bundle.getString("kdbrg", "");
            nmbrg = bundle.getString("namabrg", "");

            setTitle("Canvas "+ nmbrg);
        }

        listHistory = new ArrayList<>();
        adapter = new HistoryBarangCanvasAdapter((Activity) context, listHistory);
        lvHistory.setAdapter(adapter);
    }

    private void initEvent() {

        edtAwal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                    SimpleDateFormat sdf = new SimpleDateFormat(formatDateDisplay);
                    Date dateValue = null;
                    final Calendar customDate;

                    try {
                        dateValue = sdf.parse(iv.ChangeFormatDateString(tanggalAwal, formatDate, formatDateDisplay));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    customDate = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            customDate.set(Calendar.YEAR,year);
                            customDate.set(Calendar.MONTH,month);
                            customDate.set(Calendar.DATE,date);

                            SimpleDateFormat sdFormat = new SimpleDateFormat(formatDateDisplay, Locale.US);
                            tanggalAwal = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), formatDateDisplay, formatDate);
                            edtAwal.setText(sdFormat.format(customDate.getTime()));
                        }
                    };

                    SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                    new DatePickerDialog(context,date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
                    return true;
                }

                return false;
            }
        });

        edtAkhir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    SimpleDateFormat sdf = new SimpleDateFormat(formatDateDisplay);
                    Date dateValue = null;
                    final Calendar customDate;

                    try {
                        dateValue = sdf.parse(iv.ChangeFormatDateString(tanggalAkhir, formatDate, formatDateDisplay));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    customDate = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            customDate.set(Calendar.YEAR,year);
                            customDate.set(Calendar.MONTH,month);
                            customDate.set(Calendar.DATE,date);

                            SimpleDateFormat sdFormat = new SimpleDateFormat(formatDateDisplay, Locale.US);
                            tanggalAkhir = iv.ChangeFormatDateString(sdFormat.format(customDate.getTime()), formatDateDisplay, formatDate);
                            edtAkhir.setText(sdFormat.format(customDate.getTime()));
                        }
                    };


                    SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                    new DatePickerDialog(context, date, iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
                    return true;
                }

                return false;
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initData();
            }
        });

        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initData();
            }
        });
    }

    private void initData() {

        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"SHOW");
        final JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("datestart", tanggalAwal);
            jsonBody.put("dateend", tanggalAkhir);
            jsonBody.put("kodebrg", kdbrg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(this, jsonBody, "POST", ServerURL.getHistoryBarangCanvas, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
                        listHistory.clear();

                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){

                                JSONArray ja = responseAPI.getJSONArray("response");
                                for(int i = 0; i < ja.length(); i++){

                                    JSONObject jo = ja.getJSONObject(i);
                                    if(i == 0) getSupportActionBar().setSubtitle(jo.getString("sales"));
                                    listHistory.add(new CustomListItem(
                                            jo.getString("tgl")
                                            ,jo.getString("keterangan")
                                            ,jo.getString("stok")
                                            ,jo.getString("satuan")
                                    ));
                                }

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"ERROR");
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        iv.ProgressbarEvent(llContainer,pbLoading,btnRefresh,"GONE");
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
