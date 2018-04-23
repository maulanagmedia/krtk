package gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Barang;

import java.util.List;

/**
 * Created by indra on 29/12/2016.
 */

public class ListBarangTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<Barang> items;
    private int rowPerTableItem;
    private View viewInflater;

    public ListBarangTableAdapter(Activity context, int resource, List items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
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
        private TextView tvMenuName;
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

        final Barang barang = items.get(position);
        holder.tvMenuName.setText(barang.getNamaBarang());

        return convertView;

    }

}
