package gmedia.net.id.kartikaelektrik.adapter.SalesOrder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListBarangDetailSOAutocompleteAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<SalesOrderDetail> items, tempItems, suggestions;
    private int rowPerAutocompleteItem;

    public ListBarangDetailSOAutocompleteAdapter(Context context, int resource, List<SalesOrderDetail> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<SalesOrderDetail>(items);
        suggestions = new ArrayList<SalesOrderDetail>();
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
        SalesOrderDetail soDetail = items.get(position);
        if (soDetail != null) {
            TextView lblNamaBarang = (TextView) view.findViewById(android.R.id.text1);
            if (lblNamaBarang != null)
                lblNamaBarang.setText(soDetail.getNamaBarang());
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
            String str = ((SalesOrderDetail) resultValue).getNamaBarang();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                int x = 1;
                for (SalesOrderDetail soDetail : tempItems) {
                    if (soDetail.getNamaBarang().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(soDetail);
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

            List<SalesOrderDetail> filterList = (ArrayList<SalesOrderDetail>) results.values;

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
