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
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 2/2/2017.
 */

public class PiutangPerNotaTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();

    public PiutangPerNotaTableAdapter(Activity context, int resource, List<CustomListItem> items) {
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

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CustomListItem list = items.get(position);
        ViewHolder holder = new ViewHolder();
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
                convertView = inflater.inflate(R.layout.adapter_detail_piutang_pernota, null);

                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
                holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
                holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
                holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_subtitle_4);
                holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_subtitle_5);
            }

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(tipeViewList == 0){
            holder.tvItem1.setText(list.getListItem2());
        }else if(tipeViewList == 1){
            holder.tvItem1.setText(list.getListItem2());
        }else if(tipeViewList == 2){
            holder.tvItem1.setText(list.getListItem1());
            holder.tvItem2.setText(list.getListItem3());
            holder.tvItem3.setText(list.getListItem5());
            holder.tvItem4.setText(list.getListItem6());
            holder.tvItem5.setText(list.getListItem4());
        }

        return convertView;
    }
}
