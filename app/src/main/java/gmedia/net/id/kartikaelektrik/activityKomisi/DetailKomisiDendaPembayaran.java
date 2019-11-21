package gmedia.net.id.kartikaelektrik.activityKomisi;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.model.KomisiDenda;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DetailKomisiDendaPembayaran extends AppCompatActivity {

    private List<KomisiDenda> masterList;
    private List<CustomListItem> autocompleteList, tableList;
    private String TAG = "DetailKomisiDenda";
    private ItemValidation iv = new ItemValidation();
    private AutoCompleteTextView actvNamaPelanggan;
    private ListView lvKomisi;
    private boolean komisiMode = false;
    private boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_komisi_pembayaran);

        initUI();
    }

    private void initUI() {

        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_pelanggan);
        lvKomisi = (ListView) findViewById(R.id.lv_list_komisi);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String list = bundle.getString("list");
            String jenis = bundle.getString("jenis");

            if(jenis.equals("KOMISI")){
                komisiMode = true;
                setTitle("Komisi");
            }else{
                setTitle("Denda");
            }

            Type tipeList = new TypeToken<List<KomisiDenda>>(){}.getType();
            Gson gson = new Gson();
            masterList = gson.fromJson(list,tipeList);

            autocompleteList = new ArrayList<>();
            tableList = new ArrayList<>();
            for(KomisiDenda komisiDenda: masterList){

                CustomListItem item = new CustomListItem(komisiDenda.getNoNota(),komisiDenda.getNamaPelanggan(), iv.ChangeToRupiahFormat(Float.parseFloat(komisiDenda.getNilaiKomisiDenda())));
                autocompleteList.add(item);
                tableList.add(item);
            }

            getAutocompleteList(autocompleteList);
            getTableList(tableList);

        }
    }

    private void getAutocompleteList(List<CustomListItem> listItems) {

        actvNamaPelanggan.setAdapter(null);

        if(listItems != null && listItems.size() > 0){
            CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(DetailKomisiDendaPembayaran.this,listItems.size(),listItems, "C");

            //set adapter to autocomplete
            actvNamaPelanggan.setAdapter(arrayAdapterString);

            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvNamaPelanggan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNamaPelanggan.getText().length() <= 0){
                        getTableList(tableList);
                    }
                }
            });
        }

        actvNamaPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                List<CustomListItem> items = new ArrayList<CustomListItem>();
                items.add(cli);
                getTableList(items);
            }
        });
    }

    private void getTableList(List<CustomListItem> listItems) {

        lvKomisi.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            CustomListItemTableAdapter arrayAdapter;
            arrayAdapter = new CustomListItemTableAdapter(DetailKomisiDendaPembayaran.this, listItems.size(), listItems, "");
            lvKomisi.setAdapter(arrayAdapter);

            lvKomisi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CustomListItem cli = (CustomListItem) adapterView.getItemAtPosition(i);
                    String key = cli.getListItem1();

                    KomisiDenda selectedKomisiDenda = new KomisiDenda();
                    for(KomisiDenda komisiDenda: masterList){
                        if(komisiDenda.getNoNota().equals(key)){
                            selectedKomisiDenda = komisiDenda;
                        }
                    }

                    Intent intent = new Intent(DetailKomisiDendaPembayaran.this, SubDetailKomisiDendaPembayaran.class);
                    Gson gson = new Gson();
                    intent.putExtra("list", gson.toJson(selectedKomisiDenda));
                    String jenis = "DENDA";
                    if(komisiMode) jenis = "KOMISI";
                    intent.putExtra("jenis", jenis);
                    startActivity(intent);
                }
            });

        }
    }
}
