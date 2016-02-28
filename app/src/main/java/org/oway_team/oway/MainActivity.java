package org.oway_team.oway;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.oway_team.oway.json.JSONNavigationItem;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    AddItemsFragment mAddItemsFragment;
    MapsFragment mMapsFragment;
    private static final String TAG = "OWay-Main";
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

        mAddItemsFragment = new AddItemsFragment();
        mMapsFragment = new MapsFragment();


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_fragment_holder, mMapsFragment);
        fragmentTransaction.hide(mMapsFragment);
        fragmentTransaction.add(R.id.main_fragment_holder, mAddItemsFragment);
        fragmentTransaction.commit();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void showMap() {
        if(!mMapsFragment.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(mMapsFragment);
            ft.hide(mAddItemsFragment);
            ft.commit();
        }
    }

    public void showTaskList() {
        if(!mAddItemsFragment.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(mAddItemsFragment);
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
    public void onRouteRequested(List<JSONNavigationItem> items) {
        Log.d(TAG, "Route requested");
        //TODO: Some api interaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(mMapsFragment);
        ft.hide(mAddItemsFragment);
        ft.addToBackStack(null);
        ft.commit();
        mMapsFragment.postPoints(items);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
