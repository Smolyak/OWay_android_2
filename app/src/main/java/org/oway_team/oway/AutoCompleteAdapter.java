package org.oway_team.oway;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.oway_team.oway.json.JSONNavigationItem;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<JSONNavigationItem>{
    Context mContext;
    List<JSONNavigationItem> mItems;
    int mResId;
    class ViewHolder {
        TextView textTv;
    }
    public AutoCompleteAdapter(Context context, int resource, List<JSONNavigationItem> objects) {
        super(context, resource, objects);
        Log.d("ADAPTER", "Building new adapter: " + objects.size());
        mContext = context;
        mItems = objects;
        mResId = resource;
    }

    @Override
    public JSONNavigationItem getItem(int position) {
        Log.d("ADAPTER","Asking for: "+position +" : " + mItems.size());
        return mItems.get(position);
//     return super.getItem(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
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
            holder.textTv = (TextView)rowView.findViewById(android.R.id.text1);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder)rowView.getTag();
        }
        holder.textTv.setText(mItems.get(position).title);

        return rowView;
    }
}
