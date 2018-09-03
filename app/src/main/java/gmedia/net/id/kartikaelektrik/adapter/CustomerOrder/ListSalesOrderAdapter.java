package gmedia.net.id.kartikaelektrik.adapter.CustomerOrder;

/**
 * Created by indra on 15/12/2016.
 * Description: Adapter for list on sales order
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.Global;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.activitySalesOrderDetail.DetailSalesOrder;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;

import org.json.JSONObject;

public class ListSalesOrderAdapter extends ArrayAdapter<String>  {

    private String[] nobukti;
    private String[] nama;
    private String[] alamat;
    private String[] tanggal;
    private String[] total;
    private String[] status;
    private String[] kirimanText;
    private String[] kiriman;
    private String[] namaSales;
    private Activity context;
    private View listViewItem;
    private ItemValidation iv = new ItemValidation();

    public ListSalesOrderAdapter(Activity context, String[] nobukti, String[] nama, String[] alamat, String[] tanggal, String[] total, String[] status, String[] kirimanText, String[] namaSales, String[] kiriman) {
        super(context, R.layout.adapter_retro_5_menu_with_delete, nobukti);
        this.context = context;
        this.nobukti = nobukti;
        this.nama  = nama;
        this.alamat = alamat;
        this.tanggal = tanggal;
        this.total = total;
        this.status = status;
        this.kirimanText = kirimanText;
        this.namaSales = namaSales;
        this.kiriman = kiriman;
    }

    @Override
    public int getCount() {
        int rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
        /*if(nobukti.length < rowPerTableItem){
            return nobukti.length;
        }else{
            return rowPerTableItem;
        }*/
        return nobukti.length;
    }

    private static class ViewHolder {
        private TextView item1, item2, item3, item4, item5, item6, item7, item8;
        private LinearLayout colorContainer, container, deleteContainer;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_retro_5_menu_with_delete, null);
            holder.colorContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.item5 = (TextView) convertView.findViewById(R.id.tv_item_5);
            holder.item6 = (TextView) convertView.findViewById(R.id.tv_item_6);
            holder.item7 = (TextView) convertView.findViewById(R.id.tv_item_7);
            holder.item8 = (TextView) convertView.findViewById(R.id.tv_item_8);
            holder.deleteContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item1.setText(nobukti[position]);
        holder.item2.setText(nama[position]);
        holder.item6.setText(kirimanText[position]);
        holder.item7.setText(alamat[position]);
        holder.item8.setText(namaSales[position]);

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);
        holder.item3.setText(iv.ChangeFormatDateString(tanggal[position], formatDate, formatDateDisplay));

        String stringTotal = "";
        try {
            stringTotal = iv.ChangeToRupiahFormat(Float.parseFloat(total[position]));
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.item4.setText(stringTotal);
        String stringFlag = "Baru";
        final Global global = new Global();
        CustomListItem statusItem = global.getStatus(status[position]);
        holder.colorContainer.setBackgroundColor(context.getResources().getColor(statusItem.getListItemNumber1()));
        holder.item5.setText(statusItem.getListItem1());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                linearLayoutSO.setBackgroundColor(color);
                showSalesOrderDetail(nobukti[position], status[position]);
            }
        });
        //endregion

        // Delete Sales Order
        holder.deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomListItem statusItem = global.getStatus(status[position]);
                String peringatan = "Tidak Dapat Menghapus Order";

                if(iv.parseNullInteger(status[position]) != 2){
                    peringatan = "Tidak dapat menghapus Order berstatus " + statusItem.getListItem1();
                    Toast.makeText(context, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(context).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus " + nobukti[position] +" ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        CustomListItem statusItem = global.getStatus(status[position]);
                        String peringatan = "Tidak Dapat Menghapus Order";

                        if(iv.parseNullInteger(status[position]) != 2){

                            peringatan = "Tidak dapat menghapus Order berstatus " + statusItem.getListItem1();
                            Toast.makeText(context, peringatan, Toast.LENGTH_LONG).show();
                            return;
                        }

                        deleteData(nobukti[position]);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        return convertView;

    }

    private void showSalesOrderDetail(String nomorBukti, String status){
        Intent intent = new Intent(context, DetailSalesOrder.class);
        intent.putExtra("nosalesorder",nomorBukti);
        intent.putExtra("status",status);
        context.startActivity(intent);
//        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void deleteData(String noBuktiSalesOrder){

        String baseURL = context.getResources().getString(R.string.url_delete_so_by_id) + noBuktiSalesOrder;

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "DELETE", baseURL, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            int statusNumber = Integer.parseInt(statusI);
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            if(statusNumber == 200){
                                AutoCompleteTextView actNoSO = (AutoCompleteTextView) context.findViewById(R.id.autocomplete_SalesOrder);
                                actNoSO.setText("");
                            }
                            Toast.makeText(context, dialog, Toast.LENGTH_LONG).show(); // show message response
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }
}
