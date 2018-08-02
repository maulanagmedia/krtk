package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/19/2017.
 */

public class DetailSetoranNotaAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public DetailSetoranNotaAdapter(Activity context, List items) {
        super(context, R.layout.adapter_setoran_detail_nota, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private LinearLayout container;
        private TextView item1;
        private TextView item2;
        private TextView item3;
        private TextView item4;
        private TextView item5;
        private TextView tvLabel;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_setoran_detail_nota, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.item5 = (TextView) convertView.findViewById(R.id.tv_item_5);
            holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);

        String crBayar = item.getListItem3();
        if(item.getListItem3().equals("T")){
            crBayar = "Tunai";
        }else if(item.getListItem3().equals("B")){

            crBayar = "Bank";
        }else if(item.getListItem3().equals("G")){

            crBayar = "Giro";
            holder.tvLabel.setText("Tanggal Tempo");
        }

        holder.item1.setText(crBayar + " ("+ item.getListItem1()+")");
        holder.item2.setText(item.getListItem4());
        holder.item3.setText(item.getListItem5());
        holder.item4.setText(item.getListItem6());
        holder.item5.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getListItem7())));

        return convertView;
    }

}
