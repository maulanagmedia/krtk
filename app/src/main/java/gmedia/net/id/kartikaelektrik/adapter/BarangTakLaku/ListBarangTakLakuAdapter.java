package gmedia.net.id.kartikaelektrik.adapter.BarangTakLaku;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
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
 * Created by Shin on 1/19/2017.
 */

public class ListBarangTakLakuAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private final int backgroundcolor = Color.parseColor("#E0DFDB");
    private View listViewItem;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public ListBarangTakLakuAdapter(Activity context, List items) {
        super(context, R.layout.adapter_barang_taklaku, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
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
            convertView = inflater.inflate(R.layout.adapter_barang_taklaku, null);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item4);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);
        holder.item1.setText(item.getListItem2());
        holder.item2.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getListItem3())));
        holder.item3.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(item.getListItem4())));
        holder.item4.setText(item.getListItem5() +" "+ item.getListItem6());

        return convertView;
    }
}
