package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/19/2017.
 */

public class ListSummarySetoranAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private final int backgroundcolor = Color.parseColor("#E0DFDB");
    private View listViewItem;
    private ItemValidation iv = new ItemValidation();

    public ListSummarySetoranAdapter(Activity context, List items) {
        super(context, R.layout.adapter_summary_setoran, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private LinearLayout container;
        private TextView item1, item2, item3;
    }

    @Override
    public int getCount() {

        return items.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_summary_setoran, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);
        holder.item1.setText(item.getListItem2());
        holder.item2.setText(item.getListItem3());
        holder.item3.setText(iv.ChangeToCurrencyFormat(item.getListItem4()));

        return convertView;
    }

}
