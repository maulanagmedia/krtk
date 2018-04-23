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

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityOrderCustom.DetailOrderCustom;
import gmedia.net.id.kartikaelektrik.activityOrderCustom.OrderCustomDetail;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.Global;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListCustomTableAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomListItem> items;
    private String urlDeleteDetailSO;
    private ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";

    public ListCustomTableAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_list_custom, items);
        this.context = context;
        this.items = items;
        urlDeleteDetailSO = context.getResources().getString(R.string.url_delete_oc);
    }

    private static class ViewHolder {
        private TextView item1, item2, item3, item4, item5, item6;
        private LinearLayout container, deleteContainer;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_list_custom, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.item5 = (TextView) convertView.findViewById(R.id.tv_item_5);
            holder.item6 = (TextView) convertView.findViewById(R.id.tv_item_6);
            holder.deleteContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        final CustomListItem selectedItem = items.get(position);
        holder.item1.setText(selectedItem.getListItem1());
        holder.item2.setText(selectedItem.getListItem2());
        holder.item3.setText(iv.ChangeFormatDateString(selectedItem.getListItem4(), formatDate, formatDateDisplay));
        holder.item4.setText(iv.ChangeFormatDateString(selectedItem.getListItem5(), formatDate, formatDateDisplay));
        holder.item5.setText(iv.ChangeToRupiahFormat(Double.parseDouble(selectedItem.getListItem6())));
        Global global = new Global();
        CustomListItem statusItem = global.getStatus(selectedItem.getListItem7());
        holder.deleteContainer.setBackgroundColor(context.getResources().getColor(statusItem.getListItemNumber1()));
        holder.item6.setText(statusItem.getListItem1());

        //region click event whend tap on order list
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailOrderCustom.class);
                Gson gson = new Gson();
                intent.putExtra("nobukti", selectedItem.getListItem1());
                intent.putExtra("kdcus", selectedItem.getListItem3());
                intent.putExtra("nama", selectedItem.getListItem2());
                context.startActivity(intent);
            }
        });
        //endregion

        // Delete Sales Order
        holder.deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus Order " + selectedItem.getListItem1() + " ?").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(iv.parseNullInteger(selectedItem.getListItem7()) == 3 || iv.parseNullInteger(selectedItem.getListItem7()) == 7 || iv.parseNullInteger(selectedItem.getListItem7()) == 9){
                            String peringatan = "Tidak Dapat Menghapus Order";
                            switch (iv.parseNullInteger(selectedItem.getListItem7())){
                                case 3:
                                    peringatan = "Tidak dapat menghapus Order yang telah disetujui";
                                    break;
                                case 7:
                                    peringatan = "Tidak dapat menghapus Order yang telah di Posting";
                                    break;
                                case 9:
                                    peringatan = "Tidak dapat menghapus Order yang ditolak";
                                    break;
                            }
                            Toast.makeText(context, peringatan, Toast.LENGTH_LONG).show();
                            return;
                        }

                        deleteData(selectedItem.getListItem1());
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
                                AutoCompleteTextView actvNamaBarang = (AutoCompleteTextView) context.findViewById(R.id.autocomplete_SalesOrder);
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
