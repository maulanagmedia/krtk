package gmedia.net.id.kartikaelektrik.ChatRS.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.FormatItem;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListCustomerChatAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListCustomerChatAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.item_group_chat, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private ImageView ivProfile;
        private TextView tvNama, tvDeskripsi, tvTime;
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
            convertView = inflater.inflate(R.layout.item_group_chat, null);
            holder.ivProfile = convertView.findViewById(R.id.iv_profile);
            holder.tvNama = convertView.findViewById(R.id.tv_nama);
            holder.tvDeskripsi = convertView.findViewById(R.id.tv_deskripsi);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem itemSelected = items.get(position);
        holder.tvNama.setText(itemSelected.getListItem2());
        holder.tvDeskripsi.setText(itemSelected.getListItem3());
        ImageUtils iu = new ImageUtils();
        iu.LoadProfileImage(context, itemSelected.getListItem4(), holder.ivProfile);
        holder.tvTime.setVisibility(View.GONE);

        return convertView;

    }
}
