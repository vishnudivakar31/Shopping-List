package edu.njit.shoppinglist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.njit.shoppinglist.model.ShoppingItem;

import static edu.njit.shoppinglist.util.Constants.*;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ITEM_ID + " LONG PRIMARY KEY,"
                + KEY_ITEM_NAME + " TEXT,"
                + KEY_ITEM_COLOR + " TEXT,"
                + KEY_ITEM_QTY + " INTEGER,"
                + KEY_ITEM_SIZE + " INTEGER,"
                + KEY_ITEM_DATE_ITEM_ADDED + " LONG);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addItem(ShoppingItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentValuesFromShoppingItem(item);

        db.insert(TABLE_NAME, null, values);

        Log.d("DBHandler", "added item: " + item.toString());
    }

    public ShoppingItem getItem(Long id) {
        ShoppingItem item = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[] { KEY_ITEM_ID,
                        KEY_ITEM_NAME,
                        KEY_ITEM_COLOR,
                        KEY_ITEM_QTY,
                        KEY_ITEM_SIZE,
                        KEY_ITEM_DATE_ITEM_ADDED
                },
                KEY_ITEM_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null
        );

        if(cursor != null) {
            cursor.moveToFirst();
            item = createItemUsingCursor(cursor);
        }
        return item;
    }

    public List<ShoppingItem> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ShoppingItem> shoppingItems = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME,
                new String[] { KEY_ITEM_ID,
                        KEY_ITEM_NAME,
                        KEY_ITEM_COLOR,
                        KEY_ITEM_QTY,
                        KEY_ITEM_SIZE,
                        KEY_ITEM_DATE_ITEM_ADDED
                },
                null, null, null, null,
                KEY_ITEM_DATE_ITEM_ADDED + " DESC"
        );

        if(cursor != null) {
            cursor.moveToFirst();
            do {
                ShoppingItem item = createItemUsingCursor(cursor);
                shoppingItems.add(item);
            } while(cursor.moveToNext());
        }
        return shoppingItems;
    }

    public int updateItem(ShoppingItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentValuesFromShoppingItem(item);
        return db.update(TABLE_NAME, values, KEY_ITEM_ID + "=?",
                new String[] {String.valueOf(item.getId())});
    }

    public void deleteItem(ShoppingItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ITEM_ID + "=?", new String[] {String.valueOf(item.getId())});
        db.close();
    }

    public int getItemCount() {
        String COUNT_QUERY = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(COUNT_QUERY, null);

        return cursor.getCount();
    }


    private ShoppingItem createItemUsingCursor(Cursor cursor) {
        ShoppingItem item = new ShoppingItem();
        item.setId(cursor.getLong(cursor.getColumnIndex(KEY_ITEM_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(KEY_ITEM_NAME)));
        item.setQty(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_QTY)));
        item.setSize(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SIZE)));

        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(KEY_ITEM_DATE_ITEM_ADDED))).getTime());

        item.setDateItemAdded(formattedDate);
        return item;
    }

    private ContentValues createContentValuesFromShoppingItem(ShoppingItem item) {
        ContentValues values = new ContentValues();

        values.put(KEY_ITEM_NAME, item.getName());
        values.put(KEY_ITEM_COLOR, item.getColor());
        values.put(KEY_ITEM_QTY, item.getQty());
        values.put(KEY_ITEM_SIZE, item.getSize());
        values.put(KEY_ITEM_DATE_ITEM_ADDED, System.currentTimeMillis());

        return values;
    }
}
