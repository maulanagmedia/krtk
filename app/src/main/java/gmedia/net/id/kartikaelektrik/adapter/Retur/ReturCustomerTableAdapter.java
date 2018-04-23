package gmedia.net.id.kartikaelektrik.adapter.Retur;

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

public class ReturCustomerTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;

    public ReturCustomerTableAdapter(Activity context, int resource, List items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    @Override
    public int getCount() {
        /*if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }*/
        return items.size();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7 ;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil = 0;
        final CustomListItem item = items.get(position);
        String title = item.getListItem1();
        if(title.equals("HEADER")){
            hasil = 0;
        }else if(title.equals("FOOTER")){
            hasil = 1;
        }else{
            hasil = 2;
        }

        return hasil;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        final CustomListItem item = items.get(position);
        int tipeViewList = getItemViewType(position);

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();

            if(tipeViewList == 0){
                convertView = inflater.inflate(R.layout.adapter_list_header, null);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            }else if(tipeViewList == 1){
                convertView = inflater.inflate(R.layout.adapter_list_footer, null);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            }else if(tipeViewList == 2){
                convertView = inflater.inflate(R.layout.adapter_retur_customer, null);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item_5);
                holder.tvItem6 = (TextView) convertView.findViewById(R.id.tv_item_6);
            }

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(tipeViewList == 0){
            holder.tvItem1.setText(item.getListItem2());
        }else if(tipeViewList == 1){
            holder.tvItem1.setText(item.getListItem2());
        }else if(tipeViewList == 2){
            holder.tvItem1.setText(item.getListItem1());
            holder.tvItem2.setText(item.getListItem3());
            holder.tvItem3.setText(item.getListItem4());
            holder.tvItem4.setText(item.getListItem5());
            holder.tvItem5.setText(item.getListItem6());
            holder.tvItem6.setText(item.getListItem7());
        }

        return convertView;
    }
}
