package ca.thanasi.materialrestaurantguide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;


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
    //private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        dataSource = new RestaurantGuideDataSource(this);


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

        ((Button) findViewById(R.id.btnRemoveRestaurant)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurant != null) {
                    dataSource.removeRestaurant(restaurant.id);
                    ((LinearLayout) findViewById(R.id.details_layout)).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.txtNoSelection)).setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "Restaurant " + restaurant.name + " Removed!", Toast.LENGTH_LONG).show();
                    mNavigationDrawerFragment.updateItems();
                }
            }
        });

        ((Button) findViewById(R.id.btnEditRestaurant)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurant != null) {
                    Intent intent = new Intent(context, EditRestaurantActivity.class);
                    intent.putExtra("restaurant_id", restaurant.id);
                    startActivityForResult(intent, 50);
                }
            }
        });

        ((Button) findViewById(R.id.btnMap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurant != null) {
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("restaurant_id", restaurant.id);
                    startActivity(intent);
                }
            }
        });
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

            ((LinearLayout) findViewById(R.id.details_layout)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtNoSelection)).setVisibility(View.GONE);
        }
    }

    public void onSectionAttached(int number) {
       /* switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }*/
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
            case R.id.action_new_restaurant:
                final Activity context = this;
                Intent intent = new Intent(context, EditRestaurantActivity.class);
                startActivityForResult(intent, 50);
                break;
            case R.id.action_about:
                final Activity context2 = this;
                Intent intent2 = new Intent(context2, AboutActivity.class);
                startActivity(intent2);
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
            }
        }
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

}
