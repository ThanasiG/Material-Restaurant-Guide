package ca.thanasi.materialrestaurantguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class EditRestaurantActivity extends Activity {
    Activity context;
    RestaurantGuideDataSource dataSource;
    int restaurantId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

        context = this;
        dataSource = new RestaurantGuideDataSource(this);

        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("restaurant_id", -1);
        if (restaurantId == -1) {
            this.setTitle("New Restaurant");
        } else {
            final Restaurant restaurant = dataSource.getRestaurant(restaurantId);

            if (restaurant == null) {
                finish();
                return;
            }

            this.setTitle("Edit " + restaurant.name);

            ((TextView) findViewById(R.id.txtCreateRestaurant)).setText(restaurant.name);
            ((TextView) findViewById(R.id.txtAddress)).setText(restaurant.address);
            ((TextView) findViewById(R.id.txtPhone)).setText(restaurant.phone);
            ((TextView) findViewById(R.id.txtDesc)).setText(restaurant.desc);
            String tagStr = TextUtils.join(", ", restaurant.tags);
            ((TextView) findViewById(R.id.txtTags)).setText(tagStr);
            ((RatingBar) findViewById(R.id.ratingBar)).setRating(restaurant.rating);
        }

        (findViewById(R.id.btnUpdateRestaurant)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String restaurantName = ((EditText) context.findViewById(R.id.txtCreateRestaurant)).getText().toString();
                String restaurantAddress = ((EditText) context.findViewById(R.id.txtAddress)).getText().toString();
                String restaurantPhone = ((EditText) context.findViewById(R.id.txtPhone)).getText().toString();
                String restaurantDesc = ((EditText) context.findViewById(R.id.txtDesc)).getText().toString();
                String restaurantTags = ((EditText) context.findViewById(R.id.txtTags)).getText().toString();
                float restaurantRating = ((RatingBar) findViewById(R.id.ratingBar)).getRating();



                String[] tags = restaurantTags.split(", ");

                if (!(restaurantName.isEmpty() || restaurantAddress.isEmpty() || restaurantPhone.isEmpty())) {
                    if (restaurantId == -1) {
                        restaurantId = dataSource.createRestaurant(restaurantName, restaurantAddress, restaurantPhone, restaurantDesc, tags, restaurantRating);
                    } else {
                        dataSource.updateRestaurant(
                                restaurantId, restaurantName, restaurantAddress, restaurantPhone, restaurantDesc, tags, restaurantRating
                        );
                    }

                    Toast.makeText(getApplicationContext(), "Entry \"" + restaurantName + "\" updated!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    intent.putExtra("restaurant_id", restaurantId);
                    context.setResult(Activity.RESULT_OK, intent);

                    context.finish();
                } else if (restaurantName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "The name can't be empty!", Toast.LENGTH_LONG).show();
                } else if (restaurantName.length() > 20) {
                    Toast.makeText(getApplicationContext(), "The name cant be longer than 20 characters", Toast.LENGTH_LONG).show();
                } else if (restaurantPhone.length() > 20) {
                    Toast.makeText(getApplicationContext(), "The phone number cant be longer than 20 characters", Toast.LENGTH_LONG).show();
                } else if (restaurantAddress.length() > 25) {
                    Toast.makeText(getApplicationContext(), "The address cant be longer than 40 characters", Toast.LENGTH_LONG).show();
                } else if (restaurantAddress.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "The address can't be empty!", Toast.LENGTH_LONG).show();
                } else if (restaurantPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "The phone number can't be empty!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Update was not successful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
