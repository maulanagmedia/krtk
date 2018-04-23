package gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.KategoriBarang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indra on 28/12/2016.
 */

public class ListKategoriBarangAutocompleteAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<KategoriBarang> items, tempItems, suggestions;
    private int rowPerAutocompleteItem; // list 50 item only

    public ListKategoriBarangAutocompleteAdapter(Context context, int resource, List<KategoriBarang> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<KategoriBarang>(items);
        suggestions = new ArrayList<KategoriBarang>();
        rowPerAutocompleteItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_autocomplete));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        KategoriBarang kategoriBarang = items.get(position);
        if (kategoriBarang != null) {
            TextView lblNamaBarang = (TextView) view.findViewById(android.R.id.text1);
            if (lblNamaBarang != null)
                lblNamaBarang.setText(kategoriBarang.getNamaKategori());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((KategoriBarang) resultValue).getNamaKategori();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                int x = 0;
                for (KategoriBarang kategoriBarang : tempItems) {
                    if (kategoriBarang.getNamaKategori().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(kategoriBarang);
                        x++;
                    }
                    if(x >= rowPerAutocompleteItem) break;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<KategoriBarang> filterList = (ArrayList<KategoriBarang>) results.values;
            if (results != null && results.count > 0) {
                clear();
                addAll(filterList);
                notifyDataSetChanged();
            }else{
                notifyDataSetInvalidated();
            }
        }
    };
}
