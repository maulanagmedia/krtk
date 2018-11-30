package gmedia.net.id.kartikaelektrik.ActivityMenuAdmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 2/2/2017.
 */

public class DetailSOApproveAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();

    public DetailSOApproveAdapter(Activity context, int resource, List<CustomListItem> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
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
        private TextView tvItem1, tvItem2, tvItem3, tvItem4;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CustomListItem list = items.get(position);
        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_detail_order_approve, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_subtitle_4);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvItem1.setText(list.getListItem1());
        holder.tvItem2.setText(list.getListItem2());
        holder.tvItem3.setText(list.getListItem5());
        holder.tvItem4.setText(list.getListItem3());

        return convertView;
    }
}
