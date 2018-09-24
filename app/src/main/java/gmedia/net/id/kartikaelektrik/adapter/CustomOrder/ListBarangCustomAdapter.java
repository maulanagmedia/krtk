package gmedia.net.id.kartikaelektrik.adapter.CustomOrder;

import android.app.Activity;
import android.util.Log;
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

public class ListBarangCustomAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<Barang> items;
    private int rowPerTableItem;
    private View viewInflater;

    public ListBarangCustomAdapter(Activity context, List items) {
        super(context, R.layout.adapter_single_menu, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void addMoreData(List<Barang> addItems){

        items.addAll(addItems);
        notifyDataSetChanged();
    }

    private class ViewHolder {
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

        final Barang selectedItem = items.get(position);
        holder.tvMenuName.setText(selectedItem.getNamaBarang());

        if(convertView == null){
            Log.d("test", "getView: "+position);
        }
        return convertView;

    }

}
