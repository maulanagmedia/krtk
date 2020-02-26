package gmedia.net.id.kartikaelektrik.activitySOKhusus.Adapter;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySOKhusus.OrderDetailKhusus;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class ListOrderKhususAdapter extends ArrayAdapter {

    private Activity context;
    private List<SalesOrderDetail> items;
    private View listViewItem;
    private String urlDeleteDetailSO;
    private ItemValidation iv = new ItemValidation();
    private HashMap<String, String> stringList;
    private String status;

    public ListOrderKhususAdapter(Activity context, List<SalesOrderDetail> items, HashMap<String, String> stringList, String status) {
        super(context, R.layout.adapter_order_menu_with_delete, items);
        this.context = context;
        this.items = items;
        this.stringList = stringList;
        urlDeleteDetailSO = ServerURL.deleteSODetailById;
        this.status = status;
    }

    private static class ViewHolder {
        private TextView item1, item2, item3, item4, item5, item6, item7;
        private LinearLayout container, deleteContainer, llPaket, llContainer, llTitle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_order_menu_with_delete, null);
            holder.llTitle = (LinearLayout) convertView.findViewById(R.id.ll_title);
            holder.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.llPaket = (LinearLayout) convertView.findViewById(R.id.ll_paket);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_subtitle_4);
            holder.item5 = (TextView) convertView.findViewById(R.id.tv_subtitle_5);
            holder.item6 = (TextView) convertView.findViewById(R.id.tv_subtitle_6);
            holder.item7 = (TextView) convertView.findViewById(R.id.tv_subtitle_7);
            holder.deleteContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final SalesOrderDetail selectedSOD = items.get(position);
        holder.item1.setText(selectedSOD.getNamaBarang());
        String satuan = "";
        if(selectedSOD.getSatuan() != null && !selectedSOD.getSatuan().trim().toLowerCase().equals("null")) satuan = selectedSOD.getSatuan();
        holder.item2.setText(selectedSOD.getJumlah() + " " + satuan);
        holder.item3.setText(iv.ChangeToRupiahFormat(Float.parseFloat(selectedSOD.getHargaTotal())));
        boolean paket = false;
        if(selectedSOD.getKdPaket() != null && selectedSOD.getKdPaket().length() > 0){
            holder.llPaket.setVisibility(View.VISIBLE);
            holder.item4.setText(selectedSOD.getNamaPaket() +": "+ selectedSOD.getJensiPaket());
            paket = true;
            urlDeleteDetailSO = context.getResources().getString(R.string.url_delete_so_paket_by_id);
        }
        holder.item7.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(selectedSOD.getHarga())));

        if(selectedSOD.getTerkirim() != null){

            holder.llContainer.setVisibility(View.VISIBLE);
            holder.item5.setText(selectedSOD.getTerkirim());
            holder.item6.setText(selectedSOD.getSisa());

            if(!selectedSOD.getSisa().equals("0")){
                holder.llTitle.setBackgroundColor(context.getResources().getColor(R.color.color_btn_white_2));
            }else{
                holder.llTitle.setBackgroundColor(context.getResources().getColor(R.color.color_grey1));
            }
        }else{

            holder.llContainer.setVisibility(View.GONE);
        }

        //region click event whend tap on order list
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailKhusus.class);
                intent.putExtra("noSalesOrder", stringList.get("noSalesOrder"));
                intent.putExtra("idOrderDetail", selectedSOD.getSoDetailID());
                intent.putExtra("namaPelanggan", stringList.get("namaPelanggan"));
                intent.putExtra("kdCus", stringList.get("kdCus"));
                intent.putExtra("tempo", stringList.get("tempo"));
                intent.putExtra("idTempo", stringList.get("idTempo"));
                intent.putExtra("jumlah", selectedSOD.getJumlah());
                intent.putExtra("satuan", selectedSOD.getSatuan());
                intent.putExtra("kodeBarang", selectedSOD.getIdBarang());
                intent.putExtra("namaBarang", selectedSOD.getNamaBarang());
                intent.putExtra("harga", selectedSOD.getHarga());
                intent.putExtra("hargapcs", selectedSOD.getHargapcs());
                intent.putExtra("harganetto", selectedSOD.getHargaNetto());
                intent.putExtra("diskon", selectedSOD.getDiskon());
                intent.putExtra("total", selectedSOD.getHargaTotal());
                intent.putExtra("status", selectedSOD.getStatus());
                intent.putExtra("statusSO", status);
                context.startActivity(intent);
            }
        });
        //endregion

        // Delete Sales Order
        final boolean statusPaket = (selectedSOD.getKdPaket() != null && selectedSOD.getKdPaket().length() > 0) ? true : false;
        holder.deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus " + selectedSOD.getNamaBarang() +" dari Order ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(iv.parseNullInteger(status) == 3 || iv.parseNullInteger(status) == 7 || iv.parseNullInteger(status) == 9){
                            String peringatan = "Tidak Dapat Menghapus Order";
                            switch (iv.parseNullInteger(status)){
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

                        deleteData(selectedSOD.getSoDetailID(), statusPaket);
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

    private void deleteData(String idSODetail, boolean statusPaket){

        if(statusPaket){
            urlDeleteDetailSO = context.getResources().getString(R.string.url_delete_so_paket_by_id);
        }else{
            urlDeleteDetailSO = ServerURL.deleteSODetailById;
        }

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "DELETE", urlDeleteDetailSO + idSODetail, "", "", 0,
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
                                AutoCompleteTextView actvNamaBarang = (AutoCompleteTextView) context.findViewById(R.id.actv_list_nama_barang);
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
