package org.oway_team.oway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.utils.Common;

import java.util.List;

public class NavigationItemsAdapter extends ArrayAdapter<NavigationItem> {
    Context mContext;
    List<NavigationItem> mItems;
    int mResId;

    private String mRouteId;

    public String getRouteId() {
        return mRouteId;
    }

    public void setRouteId(String routeId) {
        this.mRouteId = routeId;
    }

    class ViewHolder {
        ImageView categoryImageView;
        TextView textTv;
        LinearLayout ll;
    }
    public NavigationItemsAdapter(Context context, int resource, List<NavigationItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mItems = objects;
        mResId = resource;
        mRouteId = "";

    }
    public List<NavigationItem> getItems() {
        return mItems;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResId, parent, false);
            holder = new ViewHolder();
            holder.categoryImageView = (ImageView)rowView.findViewById(R.id.navigation_list_item_category_logo);
            holder.textTv = (TextView)rowView.findViewById(R.id.navigation_list_item_text);
            holder.ll = (LinearLayout)rowView.findViewById(R.id.bottom_wrapper);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder)rowView.getTag();
        }

        String type = mItems.get(position).type;
        if (type.equalsIgnoreCase("company")) {
            holder.categoryImageView.setImageDrawable(Common.getDrawable(getContext(),
                    R.drawable.item_org));
        } else {
            holder.categoryImageView.setImageDrawable(Common.getDrawable(getContext(),
                    R.drawable.item_location));
        }

        holder.textTv.setText(mItems.get(position).title);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(mItems.get(position));
            }
        });

        return rowView;
    }
}
