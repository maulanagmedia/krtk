package gmedia.net.id.kartikaelektrik.adapter.CustomerOrder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityTambahOrderSales.ListBarangSalesOrder;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by indra on 27/12/2016.
 */

public class ListTableCustomerOrderAdapter extends ArrayAdapter {

    private Activity context;
    private ItemValidation iv = new ItemValidation();
    private int resource;
    private List<Customer> items;
    private final int rowPerTableItem = 100;
    private final int backgroundcolor = Color.parseColor("#E0DFDB");
    private View viewInflater;
    private List<OptionItem> listTempo = new ArrayList<>();
    private ArrayAdapter adapterTempo;
    private String selectedTempo = "", selectedIdTempo = "";

    public ListTableCustomerOrderAdapter(Activity context, int resource, List<Customer> customerList) {
        super(context, resource, customerList);
        this.context = context;
        this.resource = resource;
        this.items = customerList;
    }

    @Override
    public int getCount() {
        if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }
    }

    private static class ViewHolder {
        private TextView edCustomerName, edCustomerAddress;
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

//        if(position % 2 != 0) convertView.setBackgroundColor(backgroundcolor);
        final Customer customer = items.get(position);
        holder.edCustomerName.setText(customer.getNamaCustomer());
        holder.edCustomerAddress.setText(customer.getAlamat());

        if(customer.getXdenda().equals("0")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x0));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x0));
        }else if(customer.getXdenda().equals("1")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x1));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x1));
        }else if(customer.getXdenda().equals("2")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x2));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x2));
        }else if(customer.getXdenda().equals("3")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x3));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x3));
        }else{
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x4));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x4));
        }

        //region click event when tap on order list
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategory(customer.getKodeCustomer(),customer.getNamaCustomer(),customer.getTempo());
            }
        });
        //endregion

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
                selectedIdTempo = item.getAtt1();
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

                Intent intent = new Intent(context, ListBarangSalesOrder.class);
                intent.putExtra("kdCus", kdcus);
                intent.putExtra("namaPelanggan", customer);
                intent.putExtra("tempo", selectedTempo);
                intent.putExtra("idTempo", selectedIdTempo);
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
