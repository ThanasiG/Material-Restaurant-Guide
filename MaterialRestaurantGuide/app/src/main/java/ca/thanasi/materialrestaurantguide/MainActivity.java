package ca.thanasi.materialrestaurantguide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private CharSequence mTitle;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ShareActionProvider mShareActionProvider;
    private Activity context;
    private RestaurantGuideDataSource dataSource;
    private Restaurant restaurant;
    private Toolbar mToolbar;
    FloatingActionButton fab;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;
    private GoogleMap map;
    private Geocoder geocoder;
    private List<Address> addressList;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (Build.VERSION.SDK_INT >= 21) {// Could also use Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));

            mToolbar.setElevation(10.00f);
        }

        context = this;
        dataSource = new RestaurantGuideDataSource(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mFragmentContainerView = findViewById(R.id.navigation_drawer);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EditRestaurantActivity.class);
                startActivityForResult(intent, 50);
            }
        });


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        if (savedInstanceState != null) {
            int restId = savedInstanceState.getInt("restaurant_id", -1);
            if (restId != -1) {
                this.loadRestaurant(restId);
            }
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.openDrawer();

        (findViewById(R.id.card_view2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurant != null) {
                    String callUri = "tel:" + restaurant.phone;
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(callUri));
                    startActivity(callIntent);
                }
            }
        });
        ( findViewById(R.id.btnMap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurant != null) {
                        Intent intent = new Intent(context, MapActivity.class);
                        intent.putExtra("restaurant_id", restaurant.id);
                        startActivity(intent);
                }
            }
        });
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            fab.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (restaurant != null) {
            savedInstanceState.putInt("restaurant_id", restaurant.id);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void loadRestaurant(final int id) {
        restaurant = dataSource.getRestaurant(id);
        if (restaurant != null) {
            ((TextView) findViewById(R.id.txtRName)).setText(restaurant.name);
            ((TextView) findViewById(R.id.txtRAddress)).setText(restaurant.address);
            ((TextView) findViewById(R.id.txtRPhone)).setText(restaurant.phone);
            ((TextView) findViewById(R.id.txtRDesc)).setText(restaurant.desc);
            ((TextView) findViewById(R.id.txtRTags)).setText(TextUtils.join(", ", restaurant.tags));
            ((RatingBar) findViewById(R.id.ratingBar)).setRating(restaurant.rating);

            ((RelativeLayout) findViewById(R.id.details_layout)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtNoSelection)).setVisibility(View.GONE);

            map = ((CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            final ScrollView svDetails = (ScrollView)findViewById(R.id.scrollView_details);

            getSupportActionBar().setTitle(restaurant.name);
            if (restaurant.address != null) {
                AsyncTaskRunner fd = new AsyncTaskRunner();
                fd.execute();

                ((CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new CustomMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        svDetails.requestDisallowInterceptTouchEvent(true);
                    }
                });

            }



        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mFragmentContainerView)) { //replace this with actual function which returns if the drawer is open
            mDrawerLayout.closeDrawer(mFragmentContainerView);     // replace this with actual function which closes drawer
        } else {
            super.onBackPressed();
        }
    }

    public void onSectionAttached(int number) {
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        //if (restaurant.name.length() == 1)
        //actionBar.setTitle(restaurant.name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    MainArrayAdapter arrayAdapter = ((MainActivity) context).mNavigationDrawerFragment.arrayAdapter;
                    if (arrayAdapter != null) {
                        arrayAdapter.getFilter().filter(query);
                        ((MainActivity) context).mNavigationDrawerFragment.openDrawer();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            if (restaurant != null) {
                MenuItem item = menu.findItem(R.id.action_share);
                mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + restaurant.name + "! Heres the Address: " + restaurant.address + ", I give it " + restaurant.rating + "/5 stars!");
                shareIntent.setType("text/plain");
                mShareActionProvider.setShareIntent(shareIntent);

                restoreActionBar();
                return true;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                final Activity context2 = this;
                Intent intent2 = new Intent(context2, AboutActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_edit:
                if (restaurant != null) {
                    Intent intent = new Intent(context, EditRestaurantActivity.class);
                    intent.putExtra("restaurant_id", restaurant.id);
                    startActivityForResult(intent, 50);
                } else
                    Toast.makeText(this, "Please Create/Select a Restaurant.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete:
                if (restaurant != null) {
                    if (((TextView) findViewById(R.id.txtNoSelection)).getVisibility() == View.GONE) {
                        new AlertDialog.Builder(this)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.removeRestaurant(restaurant.id);
                                        ((RelativeLayout) findViewById(R.id.details_layout)).setVisibility(View.GONE);
                                        ((TextView) findViewById(R.id.txtNoSelection)).setVisibility(View.VISIBLE);

                                        Toast.makeText(getApplicationContext(), "Entry \"" + restaurant.name + "\" removed!", Toast.LENGTH_LONG).show();
                                        mNavigationDrawerFragment.updateItems();
                                        getSupportActionBar().setTitle("Restaurants");
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else
                        Toast.makeText(this, "Please Create/Select a Restaurant.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Please Create/Select a Restaurant.", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 50) {
            this.mNavigationDrawerFragment.updateItems();
            if (data != null && data.getIntExtra("restaurant_id", -1) != -1) {
                loadRestaurant(data.getIntExtra("restaurant_id", -1));
                getSupportActionBar().setTitle(restaurant.name);

            }
        }
    }

    void failure() {
        Toast.makeText(context, "Address not found!", Toast.LENGTH_LONG).show();
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Address> {
        @Override
        protected Address doInBackground(Void... params) {
           // map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            int restaurantId = restaurant.id;

            restaurant = dataSource.getRestaurant(restaurantId);


            try {
                geocoder = new Geocoder(context);
                addressList = geocoder.getFromLocationName(restaurant.address, 1);

            } catch (IOException ex) {

            }
            if(addressList != null && addressList.size() > 0) {
                address = addressList.get(0);
            }


            return address;
        }

        @Override
        protected void onPostExecute(Address address) {
            if(addressList != null && addressList.size() > 0) {
                map.clear();
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 13));

                map.addMarker(new MarkerOptions()
                        .title(restaurant.name)
                        .snippet(restaurant.address)
                        .position(new LatLng(address.getLatitude(), address.getLongitude())));
            }
            if(addressList == null || !(addressList.size() > 0)) {
                map.clear();
                failure();
            }

        }
    }

    ;


}
