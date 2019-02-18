package gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomer.CustomerDetail;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/19/2017.
 */

public class ListCanvasCustomerTableAdapter extends ArrayAdapter {

    private Activity context;
    private List<Customer> items;
    private final int backgroundcolor = Color.parseColor("#E0DFDB");
    private View listViewItem;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public ListCanvasCustomerTableAdapter(Activity context, int resource, List items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private TextView item1, item2;
    }

    @Override
    public int getCount() {
        if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_single_menu, null);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_menu_name);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_menu_desc);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Customer customer = items.get(position);
        holder.item1.setText(customer.getNamaCustomer());
        holder.item2.setText(customer.getAlamat() +", "+customer.getKota());

        try {

            if(customer.getXdenda() != null){

                if(customer.getXdenda().equals("0")){
                    holder.item1.setTextColor(context.getResources().getColor(R.color.color_x0));
                    holder.item2.setTextColor(context.getResources().getColor(R.color.color_x0));
                }else if(customer.getXdenda().equals("1")){
                    holder.item1.setTextColor(context.getResources().getColor(R.color.color_x1));
                    holder.item2.setTextColor(context.getResources().getColor(R.color.color_x1));
                }else if(customer.getXdenda().equals("2")){
                    holder.item1.setTextColor(context.getResources().getColor(R.color.color_x2));
                    holder.item2.setTextColor(context.getResources().getColor(R.color.color_x2));
                }else if(customer.getXdenda().equals("3")){
                    holder.item1.setTextColor(context.getResources().getColor(R.color.color_x3));
                    holder.item2.setTextColor(context.getResources().getColor(R.color.color_x3));
                }else{
                    holder.item1.setTextColor(context.getResources().getColor(R.color.color_x4));
                    holder.item2.setTextColor(context.getResources().getColor(R.color.color_x4));
                }
            }
        }catch (Exception e){e.printStackTrace();}

        return convertView;
    }
}
