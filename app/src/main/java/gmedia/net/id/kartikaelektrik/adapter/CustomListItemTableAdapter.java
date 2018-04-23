package gmedia.net.id.kartikaelektrik.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

import java.util.List;

/**
 * Created by Shin on 2/2/2017.
 */

public class CustomListItemTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<CustomListItem> items;
    private int rowPerTableItem;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();
    private String styleLayout = "L";
    /*styleLayout
    * L = List
    * 4 = 4 Item
    * 5 = 5 Item
    */

    public CustomListItemTableAdapter(Activity context, int resource, List<CustomListItem> items, String styleLayout) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
        this.styleLayout = styleLayout;
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
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CustomListItem list = items.get(position);
        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            if(styleLayout.toUpperCase().equals("L")){
                convertView = inflater.inflate(R.layout.adapter_menu_list_3, null);
            }else if(styleLayout.equals("4")){
                convertView = inflater.inflate(R.layout.adapter_retro_menu_4, null);
                holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_subtitle_4);
                holder.tvItem4.setText(list.getListItem4());
            }else if(styleLayout.equals("5")){
                convertView = inflater.inflate(R.layout.adapter_retro_menu_5, null);
                holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_subtitle_4);
                holder.tvItem4.setText(list.getListItem4());
                holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_subtitle_5);
                holder.tvItem5.setText(list.getListItem5());
            }else{
                convertView = inflater.inflate(R.layout.adapter_retro_menu, null);
            }
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvItem1.setText(list.getListItem1());
        holder.tvItem2.setText(list.getListItem2());
        holder.tvItem3.setText(list.getListItem3());

        return convertView;
    }
}
