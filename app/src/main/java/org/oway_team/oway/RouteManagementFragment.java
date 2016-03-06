package org.oway_team.oway;


import android.content.Context;
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
import android.widget.Toast;

import org.oway_team.oway.api.API;
import org.oway_team.oway.api.APIListener;
import org.oway_team.oway.api.APILoadingError;
import org.oway_team.oway.api.APIManager;
import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.json.NavigationRoute;
import org.oway_team.oway.json.SuggesterProxy;
import org.oway_team.oway.json.SuggesterProxyListener;

import java.util.ArrayList;
import java.util.List;


public class RouteManagementFragment extends Fragment implements APIListener {
    ListView mNavigationItemsListView;
    NavigationItemsAdapter mAdapter;
    AutoCompleteAdapter mAutocompleteAdapter;
    EditText mEditText;
    ListView mAutocompleteListView;
    String mIgnoreText;
    ImageView mCityLogo;
    private static final String TAG = "OWay-RouteManagement";

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

        initListeners();
        APIManager.instance().addListener(this);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
//        APIManager.instance().removeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        APIManager.instance().addListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIManager.instance().removeListener(this);
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

                    APIManager.instance().loadSuggestions(s.toString());
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
        List<NavigationItem> items = mAdapter.getItems();
        APIManager.instance().buildRoute(items);
    }
    private void addNavigationItemToList(NavigationItem item) {
        if (mAdapter == null) {
            mAdapter = new NavigationItemsAdapter(getActivity(), R.layout.navigation_list_item, new ArrayList<NavigationItem>());
            mNavigationItemsListView.setAdapter(mAdapter);
        }
        if (mAdapter.getCount() >= 10) {
            Context context = getActivity();
            CharSequence text = getString(R.string.max_items_text);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        mCityLogo.setVisibility(View.INVISIBLE);
        mAdapter.add(item);
        if (mLetsGoButton.getVisibility() == View.INVISIBLE && mAdapter.getCount() > 1) {
            mLetsGoButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter == null || mAdapter.getCount() < 2) {
            mLetsGoButton.setVisibility(View.INVISIBLE);
        }
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public void onRouteLoadingStarted() {

    }

    @Override
    public void onRouteReady(NavigationRoute route) {

    }

    @Override
    public void onRouteLoadingFailed(APILoadingError error) {

    }

    @Override
    public void onSuggestionReady(List<NavigationItem> items) {
        Log.d(TAG, "Items recved: "+items.size());
        ArrayList<String> strings = new ArrayList<>();
        for (NavigationItem item: items) {
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

    @Override
    public void onSuggestionLoadingFailed(APILoadingError error) {

    }
}
