package org.oway_team.oway;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.oway_team.oway.json.JSONNavigationItem;
import org.oway_team.oway.json.SuggesterProxy;
import org.oway_team.oway.json.SuggesterProxyListener;

import java.util.ArrayList;
import java.util.List;


public class AddItemsFragment extends Fragment implements SuggesterProxyListener {
    ListView mNavigationItemsListView;
    NavigationItemsAdapter mAdapter;
    AutoCompleteAdapter mAutocompleteAdapter;
    EditText mEditText;
    ListView mAutocompleteListView;
    String mIgnoreText;
    SuggesterProxy mSuggestionProxy;
    ImageView mCityLogo;
    private static final String TAG = "OWay-addItems";

    FloatingActionButton mLetsGoButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items_add_layout, container, false);
        mNavigationItemsListView = (ListView)view.findViewById(R.id.items_add_list_view);
        mLetsGoButton = (FloatingActionButton)view.findViewById(R.id.items_add_lets_go_button);
        mEditText = (EditText)view.findViewById(R.id.items_add_edit_text);
        mAutocompleteListView = (ListView)view.findViewById(R.id.navigation_items_autocomplete_listview);
        mCityLogo = (ImageView)view.findViewById(R.id.city_logo);
        mSuggestionProxy = new SuggesterProxy(this);
        initListeners();
        return view;
    }

    private void initListeners() {
        mLetsGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuildRoadButtonClicked();
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(mIgnoreText)) {
                    mIgnoreText = "";
                    return;
                } else if (s.length() >= 3 && !s.toString().equals(mIgnoreText)) {
                    Log.d(TAG, "On text change: " + s);
                    mSuggestionProxy.getAddr(s.toString());
                } else {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>)mAutocompleteListView.getAdapter();
                    if (adapter != null && adapter.getCount() > 0) {
                        Log.d(TAG, "Clearing adapter");
                        adapter.clear();

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAutocompleteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Items: " + mAutocompleteAdapter.getCount());
                addNavigationItemToList(mAutocompleteAdapter.getItem(position));
                mEditText.setText("");
//                ArrayAdapter<String> adapter = (ArrayAdapter<String>) mAutocompleteListView.getAdapter();
                mAutocompleteAdapter.clear();
            }
        });
    }
    private void onBuildRoadButtonClicked() {
        List<JSONNavigationItem> items = mAdapter.getItems();
        if (items.size() > 1) {

            ((MainActivity) getActivity()).onRouteRequested(items);
        }
    }
    private void addNavigationItemToList(JSONNavigationItem item) {
        if (mAdapter == null) {
            mAdapter = new NavigationItemsAdapter(getActivity(), R.layout.navigation_list_item, new ArrayList<JSONNavigationItem>());
            mNavigationItemsListView.setAdapter(mAdapter);
        }
        mCityLogo.setVisibility(View.INVISIBLE);
        mAdapter.add(item);
        if (mLetsGoButton.getVisibility() == View.INVISIBLE && mAdapter.getCount() > 1) {
            mLetsGoButton.setVisibility(View.VISIBLE);
        }
    }
    private void fillListViewWithFakeData() {
        ArrayList<JSONNavigationItem> items = new ArrayList<JSONNavigationItem>();
        for(int i = 0; i<10; i++) {
            items.add(new JSONNavigationItem());
        }
        mAdapter = new NavigationItemsAdapter(getActivity(), R.layout.navigation_list_item, items);
        mNavigationItemsListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter == null || mAdapter.getCount() < 2) {
            mLetsGoButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestFailed() {
        //Do nothing
    }

    @Override
    public void onJSONNavigationItemsReady(List<JSONNavigationItem> items) {
        Log.d(TAG, "Items recved: "+items.size());
        ArrayList<String> strings = new ArrayList<>();
        for (JSONNavigationItem item: items) {
            strings.add(item.title);
        }
        mAutocompleteAdapter
                = new AutoCompleteAdapter(getActivity(),android.R.layout.simple_list_item_1,items);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAutocompleteListView.setAdapter(mAutocompleteAdapter);
                mAutocompleteListView.bringToFront();
            }
        });
    }
}
