package gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.KategoriBarang;

import java.util.List;

/**
 * Created by indra on 28/12/2016.
 */

public class ListKategoriBarangTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<KategoriBarang> items;
    private int rowPerTableItem;
    private View viewInflater;

    public ListKategoriBarangTableAdapter(Activity context, int resource, List<KategoriBarang> listKategoriBarang) {
        super(context, resource, listKategoriBarang);
        this.context = context;
        this.resource = resource;
        this.items = listKategoriBarang;
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
            convertView = inflater.inflate(R.layout.adapter_kategori_barang, null);
            holder.tvMenuName = (TextView) convertView.findViewById(R.id.tv_menu_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final KategoriBarang kategoriBarang = items.get(position);
        holder.tvMenuName.setText(kategoriBarang.getNamaKategori());

        return convertView;

    }

}
