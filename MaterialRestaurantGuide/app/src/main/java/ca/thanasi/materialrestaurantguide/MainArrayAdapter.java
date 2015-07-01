package ca.thanasi.materialrestaurantguide;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class MainArrayAdapter extends ArrayAdapter<Restaurant> {
    Context contenxt = null;
    List<Restaurant> restaurantList = null;
    List<Restaurant> restaurantListOrig = null;

    public MainArrayAdapter(Context context, List<Restaurant> restaurantList) {
        super(context, android.R.layout.simple_list_item_1, restaurantList);
        this.contenxt = context;
        this.restaurantList = restaurantList;
        this.restaurantListOrig = restaurantList;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= restaurantList.size()){
            return -1;
        }

        return restaurantList.get(position).id;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    //@Override
    //public Filter getFilter() {
    //    return filter;
    //}

    Filter filter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence query) {
            FilterResults filterResults = new FilterResults();

            if (query == null || query.length() == 0) {
                List<Restaurant> list = new ArrayList<Restaurant>(restaurantListOrig);
                filterResults.values = list;
                filterResults.count = list.size();
            } else {
                List<Restaurant> newValues = new ArrayList<Restaurant>();

                for (int i = 0; i < restaurantList.size(); i++) {
                    Restaurant value = restaurantList.get(i);
                    if (value.name.toLowerCase().contains(query) || (TextUtils.join(" ", value.tags)).toLowerCase().contains(query.toString().toLowerCase())) {
                        newValues.add(value);
                    }
                }
                filterResults.values = newValues;
                filterResults.count = newValues.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence query, FilterResults filterResults) {
            restaurantList = (List<Restaurant>)filterResults.values;
            notifyDataSetChanged();
        }
    };
}