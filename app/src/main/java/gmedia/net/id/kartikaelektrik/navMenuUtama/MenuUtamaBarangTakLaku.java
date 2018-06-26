package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.HeaderSetoranAdapter;
import gmedia.net.id.kartikaelektrik.activitySetoran.CustomerSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.RincianSetoran;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaBarangTakLaku extends Fragment {

    private View layout;
    private static Context context;
    private ItemValidation iv = new ItemValidation();
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvBarang;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_barang_tak_laku, container, false);
        getActivity().setTitle("Setoran");
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

        actvNamaBarang = (AutoCompleteTextView) layout.findViewById(R.id.actv_nama_barang);
        lvBarang = (ListView) layout.findViewById(R.id.lv_barang);
        llLoad = (LinearLayout) layout.findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) layout.findViewById(R.id.pb_load);
        btnRefres = (Button) layout.findViewById(R.id.btn_refresh);
    }


}
