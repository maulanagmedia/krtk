package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
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
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/19/2017.
 */

public class DetailSetoranAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public DetailSetoranAdapter(Activity context, List items) {
        super(context, R.layout.adapter_setoran_detail, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private LinearLayout container;
        private TextView item1;
        private TextView item2;
        private TextView item3;
        private TextView item4;
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
            convertView = inflater.inflate(R.layout.adapter_setoran_detail, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);

        /*if(item.getListItem1().isEmpty()){

            holder.item1.setBackgroundColor(context.getResources().getColor(R.color.color_blue_bg));
            holder.item2.setBackgroundColor(context.getResources().getColor(R.color.color_blue_bg));
        }else{
            holder.item1.setBackgroundColor(context.getResources().getColor(R.color.color_red_bg));
            holder.item2.setBackgroundColor(context.getResources().getColor(R.color.color_red_bg));
        }*/

        holder.item1.setText(item.getListItem2());
        holder.item2.setText(item.getListItem3());
        holder.item3.setText(item.getListItem5());
        holder.item4.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getListItem4())));

        return convertView;
    }

}
