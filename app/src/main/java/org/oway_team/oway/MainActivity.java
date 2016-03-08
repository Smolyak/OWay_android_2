package org.oway_team.oway;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LogWriter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.oway_team.oway.api.APIListener;
import org.oway_team.oway.api.APILoadingError;
import org.oway_team.oway.api.APIManager;
import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.json.NavigationRoute;
import org.oway_team.oway.utils.Common;

import java.io.PrintWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        APIListener {
    RouteManagementFragment mRouteManagementFragment;
    MapsFragment mMapsFragment;
    SearchView mSearchView;


    private static final String TAG = "OWay-Main";
    private static final int MSG_PARSE_INTENT_DATA = 1;
    static MainHandler mHandler = new MainHandler(Looper.getMainLooper());

    @Override
    public void onRouteLoadingStarted() {

    }

    @Override
    public void onRouteReady(NavigationRoute route) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                getSupportFragmentManager().dump("", null,
                        new PrintWriter(System.out, true), null);
                ft.hide(mRouteManagementFragment);
                ft.show(mMapsFragment);
                ft.addToBackStack(null);
                ft.commit();
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

    }

    @Override
    public void onRouteLoadingFailed(APILoadingError error) {

    }

    @Override
    public void onSuggestionReady(final List<NavigationItem> items) {
        final Cursor cursor = Common.buildSuggestionsCursor(items);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

                mSearchView.setSuggestionsAdapter(
                        new SearchSuggestionsAdapter(getApplicationContext(), cursor, items));
                Log.d(TAG, "Notify with items cnt: " + items.size());
                mSearchView.getSuggestionsAdapter().notifyDataSetChanged();
                mSearchView.showContextMenu();
            }
        });

    }

    @Override
    public void onSuggestionLoadingFailed(APILoadingError error) {

    }

    static class MainHandler extends Handler {
        public MainHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PARSE_INTENT_DATA:
                    Intent intent = (Intent)msg.obj;
                    if (intent.getData() == null) {
                        Log.e(TAG, "Nothing to parse");
                        return;
                    }
                    String routeId = intent.getData().getQueryParameter("id");
                    if (routeId != null) {
                        APIManager.instance().loadRoute(routeId);
                    }
                    break;
                default:
                    Log.w(TAG,"Unknown Handler msg"+msg.what);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRouteManagementFragment = new RouteManagementFragment();
        mMapsFragment = new MapsFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_fragment_holder, mMapsFragment);
        fragmentTransaction.add(R.id.main_fragment_holder, mRouteManagementFragment);
        fragmentTransaction.hide(mMapsFragment);
        fragmentTransaction.commit();
        APIManager.instance().addListener(this);
        Intent intent = getIntent();
        if (intent.getData() != null) {
            Message msg = mHandler.obtainMessage(MSG_PARSE_INTENT_DATA);
            msg.obj = intent;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        APIManager.instance().removeListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setQueryRefinementEnabled(true);
        //HACK: Without this suggestor won't work correctly
        final Cursor cursor = Common.buildSuggestionsCursor(null);
        mSearchView.setSuggestionsAdapter
                (new SearchSuggestionsAdapter(this, cursor, null));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                APIManager.instance().loadSuggestions(query);
                return false;
            }
        });
        mRouteManagementFragment.onSearchViewReady(mSearchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void showMap() {
        if(!mMapsFragment.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(mMapsFragment);
            ft.hide(mRouteManagementFragment);
            ft.commit();
        }
    }

    public void showTaskList() {
        if(!mRouteManagementFragment.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(mRouteManagementFragment);
            ft.hide(mMapsFragment);
            ft.commit();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean shouldClose = true;
        switch (id) {
            case R.id.nav_map:
                showMap();

                break;
            case R.id.nav_task_list:
                showTaskList();
                break;
            default:
                Log.w(TAG,"This item is not implemented yet");
                CharSequence text = getString(R.string.not_implemented_yet);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
                shouldClose = false;
        }
        if (shouldClose) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
