package org.oway_team.oway;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
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
    ImageView mCityLogo;

    SearchView mSearchView;
    private static final String TAG = "OWay-RouteManagement";

    FloatingActionButton mLetsGoButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items_add_layout, container, false);
        mNavigationItemsListView = (ListView)view.findViewById(R.id.items_add_list_view);
        mLetsGoButton = (FloatingActionButton)view.findViewById(R.id.items_add_lets_go_button);
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
    }
    public void onSearchViewReady(SearchView searchView) {
        Log.d(TAG,"Registering onClickListener");
        mSearchView = searchView;
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                NavigationItem item = ((SearchSuggestionsAdapter)mSearchView.
                        getSuggestionsAdapter()).getNavItem(position);
                addNavigationItemToList((NavigationItem) item);
                return true;
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

    }

    @Override
    public void onSuggestionLoadingFailed(APILoadingError error) {

    }
}
