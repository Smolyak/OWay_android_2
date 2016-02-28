package org.oway_team.oway;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.oway_team.oway.json.JSONNavigationItem;
import org.oway_team.oway.utils.Common;

import java.util.List;

public class NavigationItemsAdapter extends ArrayAdapter<JSONNavigationItem> {
    Context mContext;
    List<JSONNavigationItem> mItems;
    int mResId;
    class ViewHolder {
        ImageView categoryImageView;
        TextView textTv;
    }
    public NavigationItemsAdapter(Context context, int resource, List<JSONNavigationItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mItems = objects;
        mResId = resource;
    }
    public List<JSONNavigationItem> getItems() {
        return mItems;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResId, parent, false);
            holder = new ViewHolder();
            holder.categoryImageView = (ImageView)rowView.findViewById(R.id.navigation_list_item_category_logo);
            holder.textTv = (TextView)rowView.findViewById(R.id.navigation_list_item_text);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder)rowView.getTag();
        }
        String type = mItems.get(position).type;
        Log.d("WOW", "Inflating item: " + type);
        if (type.equalsIgnoreCase("company")) {
            holder.categoryImageView.setImageDrawable(Common.getDrawable(getContext(),
                    R.drawable.item_org));
        } else {
            holder.categoryImageView.setImageDrawable(Common.getDrawable(getContext(),
                    R.drawable.item_location));
        }

        holder.textTv.setText(mItems.get(position).title);

        return rowView;
    }
}
