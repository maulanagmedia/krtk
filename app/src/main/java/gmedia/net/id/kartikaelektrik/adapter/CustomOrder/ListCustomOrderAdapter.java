package gmedia.net.id.kartikaelektrik.adapter.CustomOrder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomerOrder.OrderDetail;
import gmedia.net.id.kartikaelektrik.activityOrderCustom.OrderCustomDetail;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListCustomOrderAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private String urlDeleteDetailSO;
    private ItemValidation iv = new ItemValidation();
    private String statusSO;
    private String nama = "", kdCus = "", tanggal = "", tanggalTempo = "", flag = "";

    public ListCustomOrderAdapter(Activity context, List<CustomListItem> items, String nama, String kdCus, String statusSO, String tanggal, String tanggalTempo, String flag) {
        super(context, R.layout.adapter_custom_order, items);
        this.context = context;
        this.items = items;
        urlDeleteDetailSO = context.getResources().getString(R.string.url_delete_oc_detail);
        this.nama = nama;
        this.kdCus = kdCus;
        this.statusSO = statusSO;
        this.tanggal = tanggal;
        this.tanggalTempo = tanggalTempo;
        this.flag = flag;
    }

    private static class ViewHolder {
        private TextView item1, item2, item3, item4;
        private LinearLayout container, deleteContainer;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_custom_order, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.deleteContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem selectedItem = items.get(position);
        holder.item2.setText(selectedItem.getListItem1());
        holder.item3.setText(selectedItem.getListItem3() +" "+ selectedItem.getListItem4());
        holder.item4.setText(iv.ChangeToRupiahFormat(Double.parseDouble(selectedItem.getListItem7())));

        //region click event whend tap on order list
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderCustomDetail.class);
                Gson gson = new Gson();
                intent.putExtra("nobukti", selectedItem.getListItem9());
                intent.putExtra("id", selectedItem.getListItem8());
                intent.putExtra("item", gson.toJson(selectedItem));
                intent.putExtra("kdbrg", selectedItem.getListItem10());
                intent.putExtra("namabrg", selectedItem.getListItem11());
                intent.putExtra("satuan", selectedItem.getListItem14());
                intent.putExtra("kdcus", kdCus);
                intent.putExtra("nama", nama);
                intent.putExtra("status", statusSO);
                intent.putExtra("tgl", tanggal);
                intent.putExtra("tgltempo", tanggalTempo);
                intent.putExtra("flag", flag);
                context.startActivity(intent);
            }
        });
        //endregion

        // Delete Sales Order
        holder.deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus barang dari Order ?").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteData(selectedItem.getListItem8());
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        return convertView;

    }

    private void deleteData(String idSODetail){

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "DELETE", urlDeleteDetailSO+idSODetail, "", "", 0,
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
                                AutoCompleteTextView actvNamaBarang = (AutoCompleteTextView) context.findViewById(R.id.actv_nama_barang);
                                actvNamaBarang.setText("");
                            }
                            Toast.makeText(context, dialog, Toast.LENGTH_LONG).show(); // show message response
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Log.d("adapter", "onError: "+result);
                    }
                });
    }
}
