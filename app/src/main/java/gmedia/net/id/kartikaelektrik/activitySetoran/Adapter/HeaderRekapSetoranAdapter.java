package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.RequiresApi;
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

public class HeaderRekapSetoranAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public HeaderRekapSetoranAdapter(Activity context, List items) {
        super(context, R.layout.adapter_rekap_setoran_header, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private LinearLayout container;
        private TextView item1, item2, item3, item4, item5, item6;
        private LinearLayout llDetail;
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
            convertView = inflater.inflate(R.layout.adapter_rekap_setoran_header, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.item5 = (TextView) convertView.findViewById(R.id.tv_item5);
            holder.item6 = (TextView) convertView.findViewById(R.id.tv_item6);
            holder.llDetail = (LinearLayout) convertView.findViewById(R.id.ll_detail);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);
        holder.item1.setText(item.getListItem2());
        holder.item2.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getListItem3())));

        if(item.getListItem6().trim().toUpperCase().equals("T")){

            holder.llDetail.setVisibility(View.VISIBLE);
        }else{

            holder.llDetail.setVisibility(View.GONE);
        }

        holder.item3.setText(iv.ChangeToCurrencyFormat(item.getListItem7()));
        holder.item4.setText(iv.ChangeToCurrencyFormat(item.getListItem8()));
        holder.item5.setText(iv.ChangeToCurrencyFormat(item.getListItem9()));
        holder.item6.setText(iv.ChangeToCurrencyFormat(iv.parseNullDouble(item.getListItem3()) + iv.parseNullDouble(item.getListItem9())));

        return convertView;
    }

}
