package gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.DetailCheckOutCanvas;
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.DetailOrderEntryCanvas;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListBarangDetailCOAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private ItemValidation iv = new ItemValidation();
    private HashMap<String, String> stringList;
    private String status;

    public ListBarangDetailCOAdapter(Activity context, List<CustomListItem> items, HashMap<String, String> stringList) {
        super(context, R.layout.adapter_order_menu_with_delete, items);
        this.context = context;
        this.items = items;
        this.stringList = stringList;
        this.status = status;
    }

    private static class ViewHolder {
        private TextView item1, item2, item3;
        private LinearLayout container, deleteContainer;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_order_menu_with_delete, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            holder.deleteContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem itemSelected = items.get(position);
        holder.item1.setText(itemSelected.getListItem2());
        holder.item2.setText(itemSelected.getListItem3() + " " + itemSelected.getListItem4());
        holder.item3.setText(iv.ChangeToRupiahFormat(Double.parseDouble(itemSelected.getListItem12())));

        //region click event whend tap on order list
        holder.container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailOrderEntryCanvas.class);
                intent.putExtra("idorder", itemSelected.getListItem1());
                intent.putExtra("nobukti", stringList.get("nobukti"));
                intent.putExtra("kdcus", stringList.get("kdcus"));
                intent.putExtra("nama", stringList.get("namacus"));
                intent.putExtra("jumlah", itemSelected.getListItem3());
                intent.putExtra("stok", itemSelected.getListItem13());
                intent.putExtra("satuan", itemSelected.getListItem4());
                intent.putExtra("kodebarang", itemSelected.getListItem1());
                intent.putExtra("namabarang", itemSelected.getListItem2());
                intent.putExtra("nokonsinyasi", itemSelected.getListItem2());
                intent.putExtra("selectedbarang", stringList.get("selectedbarang"));

                //Untuk perhitungan ulang harga
                intent.putExtra("harga", itemSelected.getListItem6());
                intent.putExtra("hargapcs", itemSelected.getListItem9());
                intent.putExtra("diskon", itemSelected.getListItem10());
                intent.putExtra("harganetto", itemSelected.getListItem11());
                intent.putExtra("total", itemSelected.getListItem12());

                context.startActivity(intent);

            }
        });
        //endregion

        // Delete Sales Order
        holder.deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus " + itemSelected.getListItem3() +" dari Order ?").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int index = 0;
                        for(CustomListItem getItem: items){

                            if(itemSelected.getListItem1().equals(getItem.getListItem1())){
                                break;
                            }
                            index++;
                        }
                        DetailCheckOutCanvas.deleteSelectedBarang(context, index);
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
}
