package ca.thanasi.materialrestaurantguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RestaurantGuideDataSource extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    final static String DB_NAME = "restaurantguide_db";
    final static String RESTAURANTGUIDE_TABLE = "restaurant";

    public RestaurantGuideDataSource(Context context) {
        super(context, DB_NAME, null, 1);

        try {
            database = getWritableDatabase();
        } catch (Exception e){
            Log.e("RestaurantGuide", "Error opening database");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            String CREATE_SQL =
                    "create table " + RESTAURANTGUIDE_TABLE +
                            " (id integer primary key autoincrement, " +
                            "restaurant text, " +
                            "address text, " +
                            "phone text, " +
                            "desc text, " +
                            "tags text, " +
                            "rating real);";
            database.execSQL(CREATE_SQL);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public int createRestaurant(String restaurant, String address, String phone, String desc, String tagsArr[], float rating) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("restaurant", restaurant);
            cv.put("address", address);
            cv.put("phone", phone);
            cv.put("desc", desc);

            String tags = TextUtils.join(", ", tagsArr);

            cv.put("tags", tags);
            cv.put("rating", rating);
            return (int)database.insert(RESTAURANTGUIDE_TABLE, null, cv);
        } catch (Exception e) {
            Log.e("RestaurantGuide", (e.getMessage() != null) ? e.getMessage() : "Error inserting");
        }
        return -1;
    }
    public void updateRestaurant(int id, String restaurant, String address, String phone, String desc, String tagsArr[], float rating) {
        ContentValues cv = new ContentValues();
        cv.put("restaurant", restaurant);
        cv.put("address", address);
        cv.put("phone", phone);
        cv.put("desc", desc);

        String tags = TextUtils.join(", ", tagsArr);

        cv.put("tags", tags);
        cv.put("rating", rating);
        database.update(RESTAURANTGUIDE_TABLE, cv, "id = ?", new String[] {String.valueOf(id)});
    }
    public void removeRestaurant(int id){
        database.delete(RESTAURANTGUIDE_TABLE,"id = ?", new String[] {String.valueOf(id)});
    }

    public List<Restaurant> getRestaurants(){
        List<Restaurant> res = new ArrayList<Restaurant>();
        try {
            Cursor cur = database.query(RESTAURANTGUIDE_TABLE, new String[] {"id", "restaurant", "address", "phone", "desc", "tags", "rating"}, null, null, null, null, null);
            cur.moveToFirst();
            while(!cur.isAfterLast()) {
                String tagsStr = cur.getString(5);
                String tagsArr[] = TextUtils.split(tagsStr, ", ");
                res.add(new Restaurant(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), cur.getString(4), tagsArr, cur.getFloat(6)));
                cur.moveToNext();
            }
            cur.close();
        }catch (Exception e) {
        }
        return res;
    }

    public int getRestaurantCount() {
        int count = 0;
        try {
            Cursor cur = database.query(RESTAURANTGUIDE_TABLE, new String[] {"id", "restaurant", "address", "phone", "desc", "tags", "rating"}, null, null, null, null, null);
            count = cur.getCount();
            cur.close();
        }catch (Exception e) {
        }
        return count;
    }
    public Restaurant getRestaurant(int id) {
        Restaurant restaurant = null;

        try {
            Cursor cur = database.query(RESTAURANTGUIDE_TABLE, new String[] {"id", "restaurant", "address", "phone", "desc", "tags", "rating"},"id = ?", new String[]{String.valueOf(id)}, null, null, null);
            cur.moveToFirst();
            if(!cur.isAfterLast()) {
                String tagsStr = cur.getString(5);
                String tagsArr[] = TextUtils.split(tagsStr, ", ");
                restaurant = new Restaurant(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), cur.getString(4), tagsArr, cur.getFloat(6));
            }
            cur.close();
        }catch (Exception e) {
        }
        return restaurant;
    }
}
