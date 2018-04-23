package gmedia.net.id.kartikaelektrik.adapter.OmsetPenjualan;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

/**
 * Created by indra on 29/12/2016.
 */

public class OmsetPerBarangTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;
    private int typeView;

    public OmsetPerBarangTableAdapter(Activity context, int resource, List items, int type) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
        typeView = type;
    }

    @Override
    public int getCount() {
        /*if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }*/
        return items.size();
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        final CustomListItem item = items.get(position);

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();

            if(typeView == 1){ // Main

                convertView = inflater.inflate(R.layout.adapter_omset_perbarang, null);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            }else if(typeView == 2){ // Per Customer

                convertView = inflater.inflate(R.layout.adapter_omset_notacustomer, null);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item_5);
            }else if(typeView == 3){ // Per No Nota

                convertView = inflater.inflate(R.layout.adapter_omset_barangcustomer, null);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item_5);
                holder.tvItem6 = (TextView) convertView.findViewById(R.id.tv_item_6);
                holder.tvItem7 = (TextView) convertView.findViewById(R.id.tv_item_7);
            }

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(typeView == 1){

            holder.tvItem1.setText(item.getListItem2());
            holder.tvItem2.setText(item.getListItem3());
        }else if(typeView == 2){
            holder.tvItem1.setText(item.getListItem1());
            holder.tvItem2.setText(item.getListItem2());
            holder.tvItem3.setText(item.getListItem4());
            holder.tvItem4.setText(item.getListItem5());
            holder.tvItem5.setText(item.getListItem6());
        }else if(typeView == 3){
            holder.tvItem1.setText(item.getListItem3());
            holder.tvItem2.setText(item.getListItem1());
            holder.tvItem3.setText(item.getListItem2());
            holder.tvItem4.setText(item.getListItem6());
            holder.tvItem5.setText(item.getListItem4());
            holder.tvItem6.setText(item.getListItem5());
            holder.tvItem7.setText(item.getListItem7());
        }

        return convertView;
    }
}
