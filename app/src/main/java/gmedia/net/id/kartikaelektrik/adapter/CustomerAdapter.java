package gmedia.net.id.kartikaelektrik.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.model.Customer;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;

/**
 * Created by indra on 23/12/2016.
 */

public class CustomerAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private List<Customer> items, tempItems, suggestions;
    private final int rowPerAutocompleteItem = 50; // list 50 item only

    public CustomerAdapter(Context context, int resource, List<Customer> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<Customer>(items); // this makes the difference.
        suggestions = new ArrayList<Customer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Customer customer = items.get(position);
        if (customer != null) {
            TextView lblNamaCustomer = (TextView) view.findViewById(android.R.id.text1);
            if (lblNamaCustomer != null)
                lblNamaCustomer.setText(customer.getNamaCustomer() +" ("+ customer.getAlamat() + ")");
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
            String str = ((Customer) resultValue).getNamaCustomer();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                int x = 0;
                for (Customer customer : tempItems) {
                    if (customer.getNamaCustomer().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
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

            List<Customer> filterList = (ArrayList<Customer>) results.values;

            if (results != null && results.count > 0) {
                clear();
                addAll(filterList);
            }else{
                notifyDataSetInvalidated();
            }
        }
    };
}
