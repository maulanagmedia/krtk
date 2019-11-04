package gmedia.net.id.kartikaelektrik.activityPengajuanTempo.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityPengajuanTempo.ActBarangPengajuanTempo;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CustomerTempoAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();
    private List<OptionItem> listTempo = new ArrayList<>();
    private ArrayAdapter adapterTempo;
    private String selectedTempo = "";

    public CustomerTempoAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_double_menu, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView edCustomerName, edCustomerAddress;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_double_menu, null);
            holder.edCustomerName = (TextView) convertView.findViewById(R.id.tv_primary_menu_name);
            holder.edCustomerAddress = (TextView) convertView.findViewById(R.id.tv_secondary_menu_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        final CustomListItem itemSelected = items.get(position);
        holder.edCustomerName.setText(itemSelected.getListItem2());
        holder.edCustomerAddress.setText(itemSelected.getListItem3());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCategory(itemSelected.getListItem1(), itemSelected.getListItem2(),itemSelected.getListItem4());
            }
        });


        return convertView;

    }


    private void showCategory(String kdCus, String customerName, String tempo){

        showLabel(customerName, kdCus);

    /*Intent intent = new Intent(context, ListBarangSalesOrder.class);
    intent.putExtra("kdCus",kdCus);
    intent.putExtra("namaPelanggan",customerName);
    intent.putExtra("tempo",tempo);
    context.startActivity(intent);*/
    }

    private void getDataTempo() {

        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();

        ApiVolley restService = new ApiVolley(context, jBody, "GET", ServerURL.getTempoRumus, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        String message = "Data ";
                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listTempo.clear();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listTempo.add(
                                            new OptionItem(
                                                    jo.getString("tempo")
                                                    ,jo.getString("nama")
                                                    ,jo.getString("id")
                                            )
                                    );
                                }
                            }

                            adapterTempo.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLabel(final String customer, final String kdcus){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_tempo, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvLabel = (TextView) viewDialog.findViewById(R.id.tv_label);
        final Spinner spTempo = (Spinner) viewDialog.findViewById(R.id.sp_tempo);

        final TextView tvBatal = (TextView) viewDialog.findViewById(R.id.tv_batal);
        final TextView tvSimpan = (TextView) viewDialog.findViewById(R.id.tv_simpan);

        tvLabel.setText(customer);
        adapterTempo = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listTempo);

        spTempo.setAdapter(adapterTempo);

        spTempo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                OptionItem item = listTempo.get(position);
                selectedTempo = item.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getDataTempo();

        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        alert.getWindow().setGravity(Gravity.BOTTOM);

        final AlertDialog alertDialogs = alert;

        tvBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alertDialogs != null) {

                    try {
                        alertDialogs.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        tvSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alertDialogs != null) {

                    try {
                        alertDialogs.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                Intent intent = new Intent(context, ActBarangPengajuanTempo.class);
                intent.putExtra("kdCus", kdcus);
                intent.putExtra("namaPelanggan", customer);
                intent.putExtra("tempo", selectedTempo);
                context.startActivity(intent);
            }
        });

        try {

            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
