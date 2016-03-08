package org.oway_team.oway;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.oway_team.oway.json.NavigationItem;

import java.util.List;


public class SearchSuggestionsAdapter extends CursorAdapter {

    private List<NavigationItem> items;

    private TextView text;

    public SearchSuggestionsAdapter(Context context, Cursor cursor, List<NavigationItem> items) {
        super(context, cursor, false);
        this.items = items;

    }
    public NavigationItem getNavItem(int position) {
        return items.get(position);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text.setText(cursor.getString(cursor.getColumnIndex("SUGGEST_COLUMN_TEXT_1")));
        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_ID")));
        view.setTag(items.get(id));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d("Suggestion adapter", "Asking for new view");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        text = (TextView) view.findViewById(android.R.id.text1);

        return view;

    }

}