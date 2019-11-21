package gmedia.net.id.kartikaelektrik.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySalesOrderDetail.DetailSalesOrder;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/24/2017.
 */

public class DashboardAdapter extends ArrayAdapter<String> {

    private String[] nobukti;
    private String[] tempo;
    private String[] total;
    private String[] status;
    private Activity context;
    private View listViewItem;
    private ItemValidation iv = new ItemValidation();

    public DashboardAdapter(Activity context, String[] nobukti, String[] tempo, String[] total, String[] status) {
        super(context, R.layout.adapter_retro_menu, nobukti);
        this.context = context;
        this.nobukti = nobukti;
        this.tempo = tempo;
        this.total = total;
        this.status = status;
    }

    @Override
    public int getCount() {
        int rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
        if(nobukti.length < rowPerTableItem){
            return nobukti.length;
        }else{
            return rowPerTableItem;
        }
    }

    private static class ViewHolder {
        private TextView item1, item2, item3;
        private LinearLayout colorContainer, container;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_retro_menu, null, true);
            holder.colorContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_main_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item1.setText(nobukti[position]);
        String stringTotal = "";
        try {
            stringTotal = iv.ChangeToRupiahFormat(Float.parseFloat(total[position]));
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.item2.setText("Total: " + stringTotal);
        String stringFlag = "Baru";
        switch (iv.parseNullInteger(status[position])){
            case 1 :
                stringFlag = "Baru";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_1));
                break;
            case 2 :
                stringFlag = "Pending";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_2));
                break;
            case 3 :
                stringFlag = "Verified";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_3));
                break;
            case 4 :
                stringFlag = "Need Accounting Approval";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_4));
                break;
            case 5 :
                stringFlag = "Need Owner Approval";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_5));
                break;
            case 7 :
                stringFlag = "Post";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_7));
                break;
            case 9 :
                stringFlag = "Ditolak";
                holder.colorContainer.setBackgroundColor(context.getResources().getColor(R.color.status_9));
                break;
            default:
                stringFlag = "Baru";
                break;
        }

        holder.item3.setText(stringFlag);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                linearLayoutSO.setBackgroundColor(color);
                showSalesOrderDetail(nobukti[position], status[position]);
            }
        });
        //endregion

        return convertView;
    }

    private void showSalesOrderDetail(String nomorBukti, String status){
        Intent intent = new Intent(context, DetailSalesOrder.class);
        intent.putExtra("nosalesorder",nomorBukti);
        intent.putExtra("status",status);
        context.startActivity(intent);
//        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
