package gmedia.net.id.kartikaelektrik.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin on 2/2/2017.
 */

public class CustomListItemAutocompleteAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<CustomListItem> items, tempItems, suggestions;
    private int rowPerAutocompleteItem; // list 50 item only
    private String flagDisplay = "C"; // custom

    public CustomListItemAutocompleteAdapter(Context context, int resource, List<CustomListItem> items, String flagDisplay) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<CustomListItem>(items);
        suggestions = new ArrayList<CustomListItem>();
        rowPerAutocompleteItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_autocomplete));
        this.flagDisplay = flagDisplay;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        CustomListItem list = items.get(position);
        if (list != null) {
            TextView autocompleteText = (TextView) view.findViewById(android.R.id.text1);
            if (autocompleteText != null)
                if(flagDisplay.toUpperCase().equals("C")){
                    autocompleteText.setText(list.getListItem1() +" ("+list.getListItem2()+")");
                }else{
                    autocompleteText.setText(list.getListItem1());
                }
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
            String str = "";
            if(!flagDisplay.toUpperCase().equals("C")){
                str = ((CustomListItem) resultValue).getListItem1();
            }else{
                str = ((CustomListItem) resultValue).getListItem1() +" ("+((CustomListItem) resultValue).getListItem2()+")";
            }
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                int x = 0;
                for (CustomListItem list : tempItems) {
                    if(flagDisplay.toUpperCase().equals("C")){
                        if (list.getListItem1().toLowerCase().contains(constraint.toString().toLowerCase()) || list.getListItem2().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(list);
                            x++;
                        }
                    }else{
                        if (list.getListItem1().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(list);
                            x++;
                        }
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
            List<CustomListItem> filterList = (ArrayList<CustomListItem>) results.values;
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
