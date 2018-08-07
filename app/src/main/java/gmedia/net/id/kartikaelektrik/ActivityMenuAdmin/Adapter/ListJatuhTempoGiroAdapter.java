package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter;

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

public class ListJatuhTempoGiroAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public ListJatuhTempoGiroAdapter(Activity context, int resource, List items) {
        super(context, R.layout.adapter_jatuh_tempo_giro, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private TextView item1, item2, item3, item4, item5, item6;
    }

    public void addMoreData(List<CustomListItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.adapter_jatuh_tempo_giro, null);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.item5 = (TextView) convertView.findViewById(R.id.tv_item_5);
            holder.item6 = (TextView) convertView.findViewById(R.id.tv_item_6);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display3);
        final CustomListItem item = items.get(position);

        holder.item1.setText(item.getListItem3());
        holder.item2.setText(iv.ChangeFormatDateString(item.getListItem2(), formatDate, formatDateDisplay));
        holder.item3.setText(iv.ChangeFormatDateString(item.getListItem6(), formatDate, formatDateDisplay));
        holder.item4.setText(item.getListItem4());
        holder.item5.setText(item.getListItem5());
        holder.item6.setText(item.getListItem7());

        return convertView;
    }

}
