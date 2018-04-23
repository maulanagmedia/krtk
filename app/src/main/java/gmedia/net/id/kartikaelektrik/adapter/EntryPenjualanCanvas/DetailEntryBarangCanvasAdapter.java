package gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/19/2017.
 */

public class DetailEntryBarangCanvasAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<Barang> items;
    private int rowPerTableItem;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();

    public DetailEntryBarangCanvasAdapter(Activity context, int resource, List items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    public void AddListItemAdapter(List items){
        this.items.addAll(items);
        this.notifyDataSetChanged();
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
        private TextView tvNamaBarang, tvStok;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_entry_barang_canvas, null, true);
            holder.tvNamaBarang = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.tvStok = (TextView) convertView.findViewById(R.id.tv_item_2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Barang barang = items.get(position);
        holder.tvNamaBarang.setText(barang.getNamaBarang());
        holder.tvStok.setText(barang.getStok() + " " + barang.getSatuan());

        return convertView;
    }
}
