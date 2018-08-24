package gmedia.net.id.kartikaelektrik.adapter.Piutang;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

/**
 * Created by indra on 29/12/2016.
 */

public class DetailCustomerPiutangTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;

    public DetailCustomerPiutangTableAdapter(Activity context, int resource, List items){
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    @Override
    public int getCount() {

        return items.size();
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_detail_piutang_customer, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item_5);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);
        holder.tvItem1.setText(item.getListItem1());
        holder.tvItem2.setText(item.getListItem2());
        holder.tvItem3.setText(item.getListItem5());
        holder.tvItem4.setText(item.getListItem3());
        holder.tvItem5.setText(item.getListItem4());
        return convertView;
    }
}
