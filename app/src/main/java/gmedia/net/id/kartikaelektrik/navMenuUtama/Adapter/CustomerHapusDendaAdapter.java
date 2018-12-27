package gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter;

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
 * Created by Shin on 1/8/2017.
 */

public class CustomerHapusDendaAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();

    public CustomerHapusDendaAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_customer_denda, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
    }

    public void addMoreData(List<CustomListItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_customer_denda, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getListItem2());
        holder.tvItem2.setText(itemSelected.getListItem3());
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem4()));

        return convertView;
    }
}
