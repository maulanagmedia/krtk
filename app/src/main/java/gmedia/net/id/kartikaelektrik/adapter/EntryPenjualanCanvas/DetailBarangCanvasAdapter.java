package gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.DetailCheckOutCanvas;
import gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas.DetailOrderEntryCanvas;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class DetailBarangCanvasAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private ItemValidation iv = new ItemValidation();

    public DetailBarangCanvasAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_order_menu, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView item1, item2, item3;
        private LinearLayout container;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_order_menu, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem itemSelected = items.get(position);
        holder.item1.setText(itemSelected.getListItem2());
        holder.item2.setText(itemSelected.getListItem3());
        holder.item3.setText(itemSelected.getListItem4());
        return convertView;

    }
}
