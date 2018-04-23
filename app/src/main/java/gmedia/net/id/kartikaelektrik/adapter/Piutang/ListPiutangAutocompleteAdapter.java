package gmedia.net.id.kartikaelektrik.adapter.Piutang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Piutang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin on 1/16/2017.
 */

public class ListPiutangAutocompleteAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<Piutang> items, tempItems, suggestions;
    private int rowPerAutocompleteItem; // list 50 item only

    public ListPiutangAutocompleteAdapter(Context context, int resource, List<Piutang> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<Piutang>(items);
        suggestions = new ArrayList<Piutang>();
        rowPerAutocompleteItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_autocomplete));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Piutang piutang = items.get(position);
        if (piutang != null) {
            TextView lblNamaBarang = (TextView) view.findViewById(android.R.id.text1);
            if (lblNamaBarang != null)
                lblNamaBarang.setText(piutang.getNamaCustomer() +"( " + piutang.getNoNota()+" )");
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
            String str = ((Piutang) resultValue).getNamaCustomer();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                int x = 0;
                for (Piutang piutang : tempItems) {
                    if (piutang.getNamaCustomer().toLowerCase().contains(constraint.toString().toLowerCase()) || piutang.getNoNota().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(piutang);
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
            List<Piutang> filterList = (ArrayList<Piutang>) results.values;
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
