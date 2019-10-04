package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import gmedia.net.id.kartikaelektrik.activityCustomerLimit.ListCustomerLimitAdapter;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaPengajuanTempo extends Fragment {

    private View layout;
    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private List<Customer> masterListCustomer , listCustomerAutocomplete, listCustomerTable;
    private EditText edtKeyword;
    private ListView lvPengajuan;
    private Button btnAdd;

    public MenuUtamaPengajuanTempo(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_pengajuan_tempo, container, false);
        getActivity().setTitle("Customer Limit");
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

        session = new SessionManager(context);
        edtKeyword = (EditText) layout.findViewById(R.id.edt_keyword);
        lvPengajuan = (ListView) layout.findViewById(R.id.lv_pengajuan);
        btnAdd = (Button) layout.findViewById(R.id.btn_add);
    }





    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
    }
}
