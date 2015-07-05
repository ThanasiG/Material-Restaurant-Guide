package ca.thanasi.materialrestaurantguide;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class MainArrayAdapter extends ArrayAdapter<Restaurant> {
    Activity contenxt = null;
    List<Restaurant> restaurantList = null;
    List<Restaurant> restaurantListOrig = null;

    private final TextPaint mPaint = new TextPaint();
    private final Rect mBounds = new Rect();
    private final Canvas mCanvas = new Canvas();
    private final char[] mFirstChar = new char[1];
    private int mTileLetterFontSize;


    public MainArrayAdapter(Activity context, List<Restaurant> restaurantList) {
        super(context, R.layout.custom_list_view, R.id.text1, restaurantList);
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

    /*public View getView(int position, View view, ViewGroup parent) {
        final Resources res = contenxt.getResources();
        final String strName = restaurantList.get(position).toString();
        final int tileSize = res
                .getDimensionPixelSize(R.dimen.letter_tile_size);

        LayoutInflater inflater = contenxt.getLayoutInflater();

        if (view == null) {
            view = inflater.inflate(R.layout.custom_list_view, null, true);
        }

        TextView txtTitle = (TextView) view.findViewById(R.id.text1);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);

        txtTitle.setText(strName);

        imageView.setImageBitmap(getLetterTile(strName, tileSize,
                tileSize));

        view.setBackgroundColor(Color.alpha(0));


        return view;

    }*/

    public Bitmap getLetterTile(String displayName, int width, int height) {
        final Resources res = contenxt.getResources();
        mPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);

        mTileLetterFontSize = res
                .getDimensionPixelSize(R.dimen.tile_letter_font_size);

        final Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        final char firstChar = displayName.charAt(0);

        final Canvas c = mCanvas;
        c.setBitmap(bitmap);
        c.drawColor(getColor());

        mFirstChar[0] = Character.toUpperCase(firstChar);
        mPaint.setTextSize(mTileLetterFontSize);
        mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);
        c.drawText(mFirstChar, 0, 1, 0 + width / 2, 0 + height / 2
                + (mBounds.bottom - mBounds.top) / 2, mPaint);

        return bitmap;

    }

    public int getColor() {
        int[] androidColors = contenxt.getResources().getIntArray(
                R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random()
                .nextInt(androidColors.length)];
        return randomAndroidColor;
    }
}