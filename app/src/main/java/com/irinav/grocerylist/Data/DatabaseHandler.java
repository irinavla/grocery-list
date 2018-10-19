package com.irinav.grocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.irinav.grocerylist.Model.Grocery;
import com.irinav.grocerylist.Util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctx;

    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GROCERY_TABLE = "CREATE TABLE "
                + Constants.TABLE_NAME + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_GROCERY_ITEM + " TEXT," + Constants.KEY_GROCERY_QTY + " TEXT,"
                + Constants.KEY_DATE_NAME + " LONG);";

        db.execSQL(CREATE_GROCERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);
    }

    // CRUD OPERATIONS: Create, Read, Update, Delete methods

    // Add grocery
    public void addGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_GROCERY_QTY, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());

        // insert row in table
        db.insert(Constants.TABLE_NAME, null, values);

    }

    // Get a grocery
    private Grocery getGrocery(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]
                {Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_GROCERY_QTY, Constants.KEY_DATE_NAME},
                Constants.KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null );

        if (cursor != null) {
            cursor.moveToFirst();

        }
            Grocery grocery = new Grocery();
            grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
            grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_QTY)));

            // CONVERT TIMESTAMP TO SOMETHING READABLE
            java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
            String formattedDate = dateFormat.format( new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());

            grocery.setDateItemAdded(formattedDate);

            return grocery;
    }

    // Get all groceries
    public List<Grocery> getAllGroceries() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Grocery> groceryList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {
             Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_GROCERY_QTY, Constants.KEY_DATE_NAME},
                null, null, null, null, Constants.KEY_DATE_NAME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Grocery grocery = new Grocery();

                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_QTY)));

                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formattedDate = dateFormat.format( new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());

                grocery.setDateItemAdded(formattedDate);

                // add to the grocery list
                groceryList.add(grocery);

            } while (cursor.moveToNext());
        }

        return groceryList;
    }

    // Update Grocery

    public int updateGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_GROCERY_QTY, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());

        // return updated row
        return db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?", new String[] { String.valueOf(grocery.getId()) } );
    }

    // Delete Grocery

    public void deleteGrocery(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " =?", new String[] {String.valueOf(id)});
        db.close();
    }

    // Get count

    public int getGroceriesCount() {
        String countQuery =  "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
