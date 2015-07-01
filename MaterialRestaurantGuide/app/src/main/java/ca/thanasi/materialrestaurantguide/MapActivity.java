package ca.thanasi.materialrestaurantguide;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapActivity extends ActionBarActivity {

    private GoogleMap map;
    private Restaurant restaurant;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        int restaurantId = intent.getIntExtra("restaurant_id", -1);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map == null || restaurantId == -1) {
            failure();
            return;
        }

        RestaurantGuideDataSource restaurantGuideDataSource = new RestaurantGuideDataSource(this);
        restaurant = restaurantGuideDataSource.getRestaurant(restaurantId);

        if (restaurant == null) {
            failure();
            return;
        }

        map.setMyLocationEnabled(true);
        List<Address> addressList;

        try {
            geocoder = new Geocoder(this);
            addressList = geocoder.getFromLocationName(restaurant.address, 1);
            if (addressList == null) {
                failure();
                return;
            }
        } catch (IOException ex) {
            failure();
            return;
        }
        Address address = addressList.get(0);
        if (address == null) {
            failure();
            return;
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 13));

        map.addMarker(new MarkerOptions()
                .title(restaurant.name)
                .snippet(restaurant.address)
                .position(new LatLng(address.getLatitude(), address.getLongitude())));
    }

    void failure() {
        Toast.makeText(this, "Maps unable to launch!", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_directions) {
            if (restaurant != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + restaurant.address));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to get directions!", Toast.LENGTH_LONG).show();
            }
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}