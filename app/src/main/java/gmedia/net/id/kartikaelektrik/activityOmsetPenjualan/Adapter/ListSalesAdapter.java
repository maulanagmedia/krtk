package gmedia.net.id.kartikaelektrik.activityOmsetPenjualan.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

/**
 * Created by indra on 29/12/2016.
 */

public class ListSalesAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;

    public ListSalesAdapter(Activity context, int resource, List items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    private static class ViewHolder {
        private TextView tvMenuName;
    }

    public void addMoreData(List<CustomListItem> addItems){

        items.addAll(addItems);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_single_menu, null);
            holder.tvMenuName = (TextView) convertView.findViewById(R.id.tv_menu_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);
        holder.tvMenuName.setText(item.getListItem2());

        return convertView;

    }

}
