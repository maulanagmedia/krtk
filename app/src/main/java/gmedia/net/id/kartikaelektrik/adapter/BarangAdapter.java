package gmedia.net.id.kartikaelektrik.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Barang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indra on 24/12/2016.
 */

public class BarangAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<Barang> items, tempItems, suggestions;
    private int rowPerAutocompleteItem;

    public BarangAdapter(Context context, int resource, List<Barang> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<Barang>(items);
        suggestions = new ArrayList<Barang>();
        rowPerAutocompleteItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_autocomplete));
    }

    @Override
    public int getCount() {
        if (items.size() < rowPerAutocompleteItem){
            return items.size();
        }else{
            return rowPerAutocompleteItem;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Barang barang = items.get(position);
        if (barang != null) {
            TextView lblNamaBarang = (TextView) view.findViewById(android.R.id.text1);
            if (lblNamaBarang != null)
                lblNamaBarang.setText(barang.getNamaBarang());
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
            String str = ((Barang) resultValue).getNamaBarang();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                int x = 1;
                for (Barang barang : tempItems) {
                    if (barang.getNamaBarang().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(barang);
                        x++;
                    }
                    if(x == rowPerAutocompleteItem) break;
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

            List<Barang> filterList = (ArrayList<Barang>) results.values;

            if (results.count > 0) {
                clear();
                addAll(filterList);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    };
}
