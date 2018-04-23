package gmedia.net.id.kartikaelektrik.adapter.EntryPaket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.SortedMap;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

/**
 * Created by Shin on 21/07/2017.
 */

public class BarangPaketExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private SortedMap<String,List<CustomListItem>> masterData;
    private ImageView ivState;
    private int tableMaxCount = 0;

    public BarangPaketExpandAdapter(Context context, List<String> listDataHeader, SortedMap<String, List<CustomListItem>> masterData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.masterData = masterData;
        tableMaxCount = context.getResources().getInteger(R.integer.max_table);
    }

    @Override
    public int getGroupCount() {

        if(listDataHeader.size() < tableMaxCount){
            return listDataHeader.size();
        }else{
            return tableMaxCount;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return masterData.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return masterData.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expand_adapter_barang_header,null);
        }

        TextView tvHeader = (TextView) convertView.findViewById(R.id.tv_item_1);
        ivState = (ImageView) convertView.findViewById(R.id.iv_state);
        if(isExpanded){
            ivState.setImageResource(R.mipmap.ic_collapse);
        }else{
            ivState.setImageResource(R.mipmap.ic_expand);
        }
        tvHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final CustomListItem item = (CustomListItem) getChild(groupPosition, childPosition);
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expand_adapter_barang_child, null);
        }

        TextView tvChild = (TextView) convertView.findViewById(R.id.tv_item_1);
        tvChild.setText(item.getListItem2());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }
}
