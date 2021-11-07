package com.affable.smartbills.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.affable.smartbills.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DatabaseAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    //Private constructor to avoid object creation from outside classes.
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }


    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseAccess(context);
        }

        return instance;
    }

    //Open the database connection.
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    //Close the database connection
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }


    //login
    public boolean loginUser(String id, String password) {

        @SuppressLint("Recycle")
        Cursor result = database.rawQuery("SELECT * FROM 'users' WHERE user_email ='" + id + "' AND user_password = '" + password + "'", null);

        boolean chk = result.getCount() > 0;
        database.close();
        return chk;

    }

    public boolean checkPassword(String password) {

        Cursor result = database.rawQuery("SELECT * FROM 'users' WHERE user_password = '" + password + "'", null);

        boolean chk = result.getCount() > 0;
        database.close();
        return chk;

    }

    public boolean changePassword(String id, String pass) {

        ContentValues values = new ContentValues();
        values.put("user_password", pass);

        long check = database.update("users", values, "user_id=?", new String[]{"1"});
        database.close();

        return check != -1;

    }

    public String getUserId() {

        String userId = "1";
        Cursor cursor = database.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {

            userId = cursor.getString(0);

        }
        cursor.close();
        database.close();
        return userId;

    }


    //get all clients
    public ArrayList<HashMap<String, String>> getClients() {
        ArrayList<HashMap<String, String>> client = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM clients ORDER BY client_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("client_id", cursor.getString(0));
                map.put("client_phone", cursor.getString(1));
                map.put("client_name", cursor.getString(2));
                map.put("client_email", cursor.getString(3));
                map.put("client_address", cursor.getString(4));


                client.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return client;
    }


    //get customer name list
    public ArrayList<String> getClientsName() {
        ArrayList<String> clientNames = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT client_name FROM clients ORDER BY client_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);

                clientNames.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return clientNames;
    }

    //search clients
    public ArrayList<HashMap<String, String>> searchClients(String s) {
        ArrayList<HashMap<String, String>> client = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM clients WHERE client_name LIKE '%" + s + "%' ORDER BY client_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("client_id", cursor.getString(0));
                map.put("client_phone", cursor.getString(1));
                map.put("client_name", cursor.getString(2));
                map.put("client_email", cursor.getString(3));
                map.put("client_address", cursor.getString(4));


                client.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return client;
    }

    //get single client by client name
    public HashMap<String, String> getClientByName(String name) {

        HashMap<String, String> map = new HashMap<String, String>();
        Cursor cursor = database.rawQuery("SELECT * FROM clients WHERE client_name LIKE '%" + name + "%' ORDER BY client_id DESC", null);

        if (cursor.moveToFirst()) {

            map.put("client_id", cursor.getString(0));
            map.put("client_phone", cursor.getString(1));
            map.put("client_name", cursor.getString(2));
            map.put("client_email", cursor.getString(3));
            map.put("client_address", cursor.getString(4));

        }
        cursor.close();
        database.close();
        return map;
    }

    //insert clients
    public boolean addClients(String client_name, String client_phone, String client_email, String client_address) {

        ContentValues values = new ContentValues();

        values.put("client_phone", client_phone);
        values.put("client_name", client_name);
        values.put("client_email", client_email);
        values.put("client_address", client_address);

        long check = database.insert("clients", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //update client
    public boolean updateClient(String client_id, String client_name, String client_phone, String client_email, String client_address) {

        ContentValues values = new ContentValues();

        values.put("client_phone", client_phone);
        values.put("client_name", client_name);
        values.put("client_email", client_email);
        values.put("client_address", client_address);

        long check = database.update("clients", values, "client_id=?", new String[]{client_id});
        database.close();

        //if data update success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //delete Client
    public boolean deleteClient(String client_id) {

        long check = database.delete("clients", "client_id=?", new String[]{client_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //get All expense data
    public ArrayList<HashMap<String, String>> getAllExpense() {
        ArrayList<HashMap<String, String>> expenses = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expenses ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));

                String cDate = cursor.getString(cursor.getColumnIndex("date"));
                String cMonth = cursor.getString(cursor.getColumnIndex("month"));
                String cYear = cursor.getString(cursor.getColumnIndex("year"));

                map.put("expense_date", cYear + "-" + cMonth + "-" + cDate);

                expenses.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return expenses;
    }


    //search expense
    public ArrayList<HashMap<String, String>> searchExpense(String s) {
        ArrayList<HashMap<String, String>> expense = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expenses WHERE expense_name LIKE '%" + s + "%' ORDER BY expense_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));

                String cDate = cursor.getString(cursor.getColumnIndex("date"));
                String cMonth = cursor.getString(cursor.getColumnIndex("month"));
                String cYear = cursor.getString(cursor.getColumnIndex("year"));

                map.put("expense_date", cYear + "-" + cMonth + "-" + cDate);

                expense.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return expense;
    }

    //delete expense
    public boolean deleteExpense(String expense_id) {

        long check = database.delete("expenses", "expense_id=?", new String[]{expense_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //insert expense
    public boolean addExpense(String expense_name, String expense_note, String expense_amount, String expense_time, String date, String month, String year) {

        ContentValues values = new ContentValues();

        values.put("expense_name", expense_name);
        values.put("expense_note", expense_note);
        values.put("expense_amount", expense_amount);
        values.put("expense_time", expense_time);
        values.put("date", date);
        values.put("month", month);
        values.put("year", year);

        long check = database.insert("expenses", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update client
    public boolean updateExpense(String expense_id, String expense_name, String expense_note, String expense_amount, String expense_time, String date, String month, String year) {

        ContentValues values = new ContentValues();

        values.put("expense_name", expense_name);
        values.put("expense_note", expense_note);
        values.put("expense_amount", expense_amount);
        values.put("expense_time", expense_time);
        values.put("date", date);
        values.put("month", month);
        values.put("year", year);

        long check = database.update("expenses", values, "expense_id=?", new String[]{expense_id});
        database.close();

        //if data update success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //add new Item
    public boolean addItem(String item_name, String item_code, String item_category, String item_buy_price, String item_sell_price, //String item_supplier,
                           String item_stock, String item_image, String item_weight_unit_id, String item_weight, String item_description) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("item_name", item_name);
        contentValues.put("item_code", item_code);
        contentValues.put("item_category", item_category);
        contentValues.put("item_description", item_description);
        contentValues.put("item_buy_price", item_buy_price);
        contentValues.put("item_sell_price", item_sell_price);
        contentValues.put("item_image", item_image);
        contentValues.put("item_stock", item_stock);
        contentValues.put("item_weight_unit_id", item_weight_unit_id);
        contentValues.put("item_weight", item_weight);

        long check = database.insert("items", null, contentValues);
        database.close();

        if (check == -1) {
            return false;
        } else {
            return true;
        }

    }


    //update items
    public boolean updateItem(String item_id, String item_name, String item_code, String item_category, String item_buy_price, String item_sell_price, //String item_supplier,
                              String item_stock, String item_image, String item_weight_unit_id, String item_weight, String item_description) {

        ContentValues values = new ContentValues();

        values.put("item_name", item_name);
        values.put("item_code", item_code);
        values.put("item_category", item_category);
        values.put("item_description", item_description);
        values.put("item_buy_price", item_buy_price);
        values.put("item_sell_price", item_sell_price);
        values.put("item_image", item_image);
        values.put("item_stock", item_stock);
        values.put("item_weight_unit_id", item_weight_unit_id);
        values.put("item_weight", item_weight);

        long check = database.update("items", values, "item_id=?", new String[]{item_id});
        database.close();

        //if data update success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //get all items
    public ArrayList<HashMap<String, String>> getItems() {
        ArrayList<HashMap<String, String>> item = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM items ORDER BY item_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("item_id", cursor.getString(0));
                map.put("item_name", cursor.getString(1));
                map.put("item_code", cursor.getString(2));
                map.put("item_category", cursor.getString(3));
                map.put("item_description", cursor.getString(4));
                map.put("item_buy_price", cursor.getString(5));
                map.put("item_sell_price", cursor.getString(6));
                map.put("item_supplier", cursor.getString(7));
                map.put("item_image", cursor.getString(8));
                map.put("item_stock", cursor.getString(9));
                map.put("item_weight_unit_id", cursor.getString(10));
                map.put("item_weight", cursor.getString(11));


                item.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return item;
    }


    //get item info
    public HashMap<String, String> getItemInfo(String itemID) {

        HashMap<String, String> map = new HashMap<String, String>();

        Cursor cursor = database.rawQuery("SELECT * FROM items WHERE item_id='" + itemID + "'", null);
        if (cursor.moveToFirst()) {

            map.put("item_id", cursor.getString(0));
            map.put("item_name", cursor.getString(1));
            map.put("item_code", cursor.getString(2));
            map.put("item_category", cursor.getString(3));
            map.put("item_description", cursor.getString(4));
            map.put("item_buy_price", cursor.getString(5));
            map.put("item_sell_price", cursor.getString(6));
            map.put("item_supplier", cursor.getString(7));
            map.put("item_image", cursor.getString(8));
            map.put("item_stock", cursor.getString(9));
            map.put("item_weight_unit_id", cursor.getString(10));
            map.put("item_weight", cursor.getString(11));

        }
        cursor.close();
        database.close();
        return map;
    }


    //get item image
    public String getItemImage(String itemId) {

        String itemImage = "n/a";

        Cursor cursor = database.rawQuery("SELECT * FROM items WHERE item_id ='" + itemId + "'", null);

        if (cursor.moveToFirst()) {
            do {

                itemImage = cursor.getString(8);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return itemImage;


    }


    //search items
    public ArrayList<HashMap<String, String>> searchItems(String s) {
        ArrayList<HashMap<String, String>> client = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM items WHERE item_name LIKE '%" + s + "%' ORDER BY item_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("item_id", cursor.getString(0));
                map.put("item_name", cursor.getString(1));
                map.put("item_code", cursor.getString(2));
                map.put("item_category", cursor.getString(3));
                map.put("item_description", cursor.getString(4));
                map.put("item_buy_price", cursor.getString(5));
                map.put("item_sell_price", cursor.getString(6));
                map.put("item_supplier", cursor.getString(7));
                map.put("item_image", cursor.getString(8));
                map.put("item_stock", cursor.getString(9));
                map.put("item_weight_unit_id", cursor.getString(10));
                map.put("item_weight", cursor.getString(11));


                client.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return client;
    }

    //delete item
    public boolean deleteItem(String item_id) {

        long check = database.delete("Items", "item_id=?", new String[]{item_id});
        database.delete("items_cart", "item_id=?", new String[]{item_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //get items according to category
    public ArrayList<HashMap<String, String>> getItemsByCategory(String categoryId) {

        ArrayList<HashMap<String, String>> item = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM items WHERE item_category = '" + categoryId + "' ORDER BY item_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("item_id", cursor.getString(0));
                map.put("item_name", cursor.getString(1));
                map.put("item_code", cursor.getString(2));
                map.put("item_category", cursor.getString(3));
                map.put("item_description", cursor.getString(4));
                map.put("item_buy_price", cursor.getString(5));
                map.put("item_sell_price", cursor.getString(6));
                map.put("item_supplier", cursor.getString(7));
                map.put("item_image", cursor.getString(8));
                map.put("item_stock", cursor.getString(9));
                map.put("item_weight_unit_id", cursor.getString(10));
                map.put("item_weight", cursor.getString(11));

                item.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return item;

    }

    //get item stock using item ID
    public String getItemStock(String itemID) {

        String stock = "0";
        Cursor cursor = database.rawQuery("SELECT * FROM items WHERE item_id ='" + itemID + "'", null);

        if (cursor.moveToFirst()) {
            do {

                stock = cursor.getString(9);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return stock;
    }

    //update client
    public boolean updateItemStock(String itemID, String item_stock) {

        ContentValues values = new ContentValues();

        values.put("item_stock", item_stock);


        long check = database.update("items", values, "item_id=?", new String[]{itemID});

        //if data update success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //get currency name
    public String getCurrency(String username) {

        String currency = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE user_name='" + username + "'", null);

        if (cursor.moveToFirst()) {
            do {

                currency = cursor.getString(8);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return currency;
    }

    //get currency name
    public String getCurrency() {

        String currency = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {
            do {

                currency = cursor.getString(8);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return currency;
    }

    //get Item Weight Unit
    public List<HashMap<String, String>> getItemWeightUnit() {

        ArrayList<HashMap<String, String>> item_weightUnit = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM item_weight_unit ORDER BY unit_id DESC", null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> map = new HashMap<>();
                map.put("unit_id", cursor.getString(0));
                map.put("unit_name", cursor.getString(1));

                item_weightUnit.add(map);

            } while (cursor.moveToNext());

        }

        cursor.close();
        database.close();

        return item_weightUnit;
    }


    //get item Categories
    public List<HashMap<String, String>> getItemCategories() {

        ArrayList<HashMap<String, String>> item_category = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM item_category ORDER BY category_id DESC", null);

        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<>();
                map.put("category_id", cursor.getString(0));
                map.put("category_name", cursor.getString(1));

                item_category.add(map);

            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();

        return item_category;
    }

    //search clients
    public List<HashMap<String, String>> searchCategories(String s) {

        List<HashMap<String, String>> category = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM item_category WHERE category_name LIKE '%" + s + "%' ORDER BY category_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("category_id", cursor.getString(0));
                map.put("category_name", cursor.getString(1));
                map.put("category_image", cursor.getString(2));

                category.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return category;
    }

    //delete category
    public boolean deleteCategory(String id) {

        long check = database.delete("item_category", "category_id=?", new String[]{id});

        database.close();
        return check == 1;

    }

    //get category using category ID
    public String getCategory(String categoryID) {

        String category = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM item_category WHERE category_id ='" + categoryID + "'", null);

        if (cursor.moveToFirst()) {
            do {

                category = cursor.getString(1);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return category;
    }

    //add new category
    public boolean addCategory(String category_name, String category_image) {

        ContentValues values = new ContentValues();


        values.put("category_name", category_name);
        values.put("category_image", category_image);

        long check = database.insert("item_category", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        return check != -1;
    }

    //update category name
    public boolean updateCategory(String category_id, String category_name) {

        ContentValues values = new ContentValues();

        values.put("category_id", category_id);
        values.put("category_name", category_name);

        long check = database.update("item_category", values, "category_id=?", new String[]{category_id});
        database.close();

        //if data update success, its return 1, if failed return -1
        return check != -1;
    }


    //get Weight Unit using weight_unit ID
    public String getWeightUnit(String weight_unitID) {

        String weightUnit = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM item_weight_unit WHERE unit_id ='" + weight_unitID + "'", null);

        if (cursor.moveToFirst()) {
            do {

                weightUnit = cursor.getString(1);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return weightUnit;
    }

    //Add product into cart
    public int addToCart(String items_id, String weight, String weight_unit, String price, int qty, String stock) {

        Cursor result = database.rawQuery("SELECT * FROM items_cart WHERE item_id='" + items_id + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put("item_id", items_id);
            values.put("item_weight", weight);
            values.put("item_weight_unit", weight_unit);
            values.put("item_price", price);
            values.put("item_qty", qty);
            values.put("stock", stock);

            long check = database.insert("items_cart", null, values);

            database.close();

            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    //get Cart Items
    public ArrayList<HashMap<String, String>> getCartItems() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM items_cart", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("cart_id", cursor.getString(0));
                map.put("item_id", cursor.getString(1));
                map.put("item_weight", cursor.getString(2));
                map.put("item_weight_unit", cursor.getString(3));
                map.put("item_price", cursor.getString(4));
                map.put("item_qty", cursor.getString(5));
                map.put("stock", cursor.getString(6));

                product.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return product;
    }

    //delete product from cart
    public boolean deleteItemFromCart(String id) {

        long check = database.delete("items_cart", "cart_id=?", new String[]{id});
        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //get category using category ID
    public String getItemName(String itemID) {

        String itemName = "n/a";
        Cursor cursor = database.rawQuery("SELECT item_name FROM items WHERE item_id ='" + itemID + "'", null);

        if (cursor.moveToFirst()) {

            itemName = cursor.getString(0);

        }

        cursor.close();
        database.close();
        return itemName;
    }

    //get cart item count
    public int getCartItemCount() {

        Cursor cursor = database.rawQuery("SELECT * FROM items_cart", null);
        int itemCount = cursor.getCount();

        cursor.close();
        database.close();
        return itemCount;
    }

    //calculate total price
    //calculate total price of product
    public double getTotalPrice() {

        double total_price = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM items_cart", null);
        if (cursor.moveToFirst()) {

            do {

                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                double sub_total = price * qty;
                total_price = total_price + sub_total;

            } while (cursor.moveToNext());

        } else {
            total_price = 0;
        }
        cursor.close();
        database.close();
        return total_price;
    }

    public boolean updateProductQty(String id, String qty) {

        ContentValues values = new ContentValues();

        values.put("item_qty", qty);

        long check = database.update("items_cart", values, "cart_id=?", new String[]{id});
        database.close();

        if (check == -1) {
            return false;
        } else {
            return true;
        }

    }

    //get shop information
    public HashMap<String, String> getUserInformation() {
        HashMap<String, String> map = new HashMap<String, String>();
        Cursor cursor = database.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {

            map.put("user_id", cursor.getString(0));
            map.put("user_type", cursor.getString(1));
            map.put("user_name", cursor.getString(2));
            map.put("user_email", cursor.getString(3));
            map.put("user_phone", cursor.getString(4));
            map.put("user_password", cursor.getString(5));
            map.put("user_image", cursor.getString(6));
            map.put("user_address", cursor.getString(7));
            map.put("currency", cursor.getString(8));
            map.put("tax", cursor.getString(9));

        }
        cursor.close();
        database.close();
        return map;
    }



    public boolean updateUserInformation(String id, String name, String phone, String email, String address, String currency, String tax) {

        ContentValues values = new ContentValues();

        values.put("user_name", name);
        values.put("user_email", email);
        values.put("user_phone", phone);
        values.put("user_address", address);
        values.put("currency", currency);
        values.put("tax", tax);

        long check = database.update("users", values, "user_id=?", new String[]{id});
        database.close();

        return check != -1;

    }


    //get payment Method
    public ArrayList<HashMap<String, String>> getPaymentMethod() {
        ArrayList<HashMap<String, String>> payment_method = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method ORDER BY payment_method_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("payment_method_id", cursor.getString(0));
                map.put("payment_method_name", cursor.getString(1));


                payment_method.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return payment_method;
    }

    //get payment Method Names
    public ArrayList<String> getPaymentMethodNames() {

        ArrayList<String> paymentMethods = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT payment_method_name FROM payment_method ORDER BY payment_method_id DESC", null);

        if (cursor.moveToFirst()) {

            do {
                paymentMethods.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
        return paymentMethods;
    }

    //search payment Method
    public List<HashMap<String, String>> searchPaymentMethods(String s) {

        List<HashMap<String, String>> category = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method WHERE payment_method_name LIKE '%" + s + "%' ORDER BY payment_method_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("payment_method_id", cursor.getString(0));
                map.put("payment_method_name", cursor.getString(1));

                category.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return category;
    }

    //delete payment Method
    public boolean deletePaymentMethod(String id) {

        long check = database.delete("payment_method", "payment_method_id=?", new String[]{id});

        database.close();
        return check == 1;

    }

    //add new payment Method
    public boolean addPaymentMethod(String name) {

        ContentValues values = new ContentValues();

        values.put("payment_method_name", name);

        long check = database.insert("payment_method", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        return check != -1;
    }

    //update payment Method
    public boolean updatePaymentMethod(String id, String name) {

        ContentValues values = new ContentValues();

        values.put("payment_method_id", id);
        values.put("payment_method_name", name);

        long check = database.update("payment_method", values, "payment_method_id=?", new String[]{id});
        database.close();

        //if data update success, its return 1, if failed return -1
        return check != -1;
    }


    //get order type data
    public ArrayList<HashMap<String, String>> getOrderType() {
        ArrayList<HashMap<String, String>> order_type = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_type ORDER BY order_type_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("order_type_id", cursor.getString(0));
                map.put("order_type_name", cursor.getString(1));


                order_type.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return order_type;
    }

    //get customer name list
    public ArrayList<String> getOrderTypeNames() {

        ArrayList<String> orderTypes = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT order_type_name FROM order_type ORDER BY order_type_id DESC", null);

        if (cursor.moveToFirst()) {

            do {
                orderTypes.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
        return orderTypes;
    }


    //add orderList & Order Details
    public boolean insertOrder(String invoiceId, JSONObject jsonObject) {

        boolean isAddedOrderList = false, isAddedOrderDetails = false, isPaymentHistory = false;

        //order list
        ContentValues orderListValues = new ContentValues();

        try {

            //get the values from json object
            String order_type = jsonObject.getString("order_type");
            String payment_method = jsonObject.getString("payment_method");
            String customer_name = jsonObject.getString("customer_name");
            String tax = jsonObject.getString("tax");
            String discount = jsonObject.getString("discount");
            String sub_total = jsonObject.getString("sub_total");
            String total_price = jsonObject.getString("total_price");
            String paid_money = jsonObject.getString("paid_money");
            String due_money = jsonObject.getString("due_money");
            String order_time = jsonObject.getString("order_time");

            String date = jsonObject.getString("day");
            String month = jsonObject.getString("month");
            String year = jsonObject.getString("year");

            //add the values in contentValues
            orderListValues.put("invoice_id", invoiceId);
            orderListValues.put("order_type", order_type);
            orderListValues.put("payment_method", payment_method);
            orderListValues.put("customer_name", customer_name);
            orderListValues.put("order_status", Constant.PENDING);
            orderListValues.put("sub_total", sub_total);
            orderListValues.put("discount", discount);
            orderListValues.put("tax", tax);
            orderListValues.put("total_price", total_price);
            orderListValues.put("order_time", order_time);
            orderListValues.put("date", date);
            orderListValues.put("month", month);
            orderListValues.put("year", year);

            //insert values in order details
            long chk = database.insert("order_list", null, orderListValues);

            if (chk != -1) {
                isAddedOrderList = true;
            }

            //clear cart after added in order
            database.delete("items_cart", null, null);

            //TODO: remove first_payment
            //add payment History
            isPaymentHistory = addFirstPayment(invoiceId, total_price, paid_money, due_money,
                    order_time, date, month, year);


            //add order Details
            JSONArray array = jsonObject.getJSONArray("orderDetails");
            isAddedOrderDetails = addToOrderDetails(invoiceId, array);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        database.close();
        return isAddedOrderDetails && isAddedOrderList && isPaymentHistory;

    }

    //add payment history called by insertOrder method
    private boolean addFirstPayment(String invoiceId, String total_price, String paid_money, String due_money,
                                    String order_time, String date, String month, String year) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("invoice_id", invoiceId);
        contentValues.put("payment_no", "1");
        contentValues.put("total_money", total_price);
        contentValues.put("total_payed", paid_money);
        contentValues.put("now_payed", paid_money);
        contentValues.put("due_money", due_money);
        contentValues.put("payment_time", order_time);
        contentValues.put("date", date);
        contentValues.put("month", month);
        contentValues.put("year", year);

        //insert values in paymentHistory table
        long chk = database.insert("payment_list", null, contentValues);

        if (chk != -1) {
            return true;
        }

        return false;
    }

    //add order details called by insertOrder method
    private boolean addToOrderDetails(String orderId, JSONArray array) {

        boolean isAdded = false;

        try {

            //get products values from json array
            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                String item_id = object.getString("item_id");
                String item_weight = object.getString("item_weight");
                String item_qty = object.getString("item_qty");
                String item_price = object.getString("item_price");

                String stock = object.getString("stock");
                int updated_stock = Integer.parseInt(stock) - Integer.parseInt(item_qty);

                ContentValues contentValues = new ContentValues();
                contentValues.put("invoice_id", orderId);
                contentValues.put("item_id", item_id);
                contentValues.put("item_weight", item_weight);
                contentValues.put("item_qty", item_qty);
                contentValues.put("stock", updated_stock);
                contentValues.put("item_price", item_price);

                long chk = database.insert("order_details", null, contentValues);

                if (chk != -1) {

                    //update stock value
                    isAdded = updateItemStock(item_id, String.valueOf(updated_stock));
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isAdded;

    }


    public ArrayList<HashMap<String, String>> getOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("invoice_id", cursor.getString(1));
                map.put("order_type", cursor.getString(2));
                map.put("payment_method", cursor.getString(3));
                map.put("customer_name", cursor.getString(4));
                map.put("sub_total", cursor.getString(5));
                map.put("discount", cursor.getString(6));
                map.put("tax", cursor.getString(7));
                map.put("total_price", cursor.getString(8));
                map.put("order_status", cursor.getString(9));
                map.put("order_time", cursor.getString(10));

                String currentDate = cursor.getString(13) + "-" + cursor.getString(12) + "-" + cursor.getString(11);

                map.put("order_date", currentDate);

                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }


    public ArrayList<HashMap<String, String>> getOrderListByStatus(String status) {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list WHERE order_status = '" + status + "' ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("invoice_id", cursor.getString(1));
                map.put("order_type", cursor.getString(2));
                map.put("payment_method", cursor.getString(3));
                map.put("customer_name", cursor.getString(4));
                map.put("sub_total", cursor.getString(5));
                map.put("discount", cursor.getString(6));
                map.put("tax", cursor.getString(7));
                map.put("total_price", cursor.getString(8));
                map.put("order_status", cursor.getString(9));
                map.put("order_time", cursor.getString(10));

                String currentDate = cursor.getString(13) + "-" + cursor.getString(12) + "-" + cursor.getString(11);

                map.put("order_date", currentDate);

                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }

    public HashMap<String, String> getSingleOrderList(String id) {

        Cursor cursor = database.rawQuery("SELECT * FROM order_list WHERE invoice_id = '" + id + "' ORDER BY order_id DESC", null);

        HashMap<String, String> map = new HashMap<String, String>();

        if (cursor.moveToFirst()) {

            map.put("invoice_id", cursor.getString(1));
            map.put("order_type", cursor.getString(2));
            map.put("payment_method", cursor.getString(3));
            map.put("customer_name", cursor.getString(4));
            map.put("sub_total", cursor.getString(5));
            map.put("discount", cursor.getString(6));
            map.put("tax", cursor.getString(7));
            map.put("total_price", cursor.getString(8));
            map.put("order_status", cursor.getString(9));
            map.put("order_time", cursor.getString(10));

            String currentDate = cursor.getString(13) + "-" + cursor.getString(12) + "-" + cursor.getString(11);

            map.put("order_date", currentDate);

        }
        cursor.close();
        database.close();
        return map;

    }

    public boolean updateOrderStatus(String id, String status) {

        ContentValues values = new ContentValues();

        values.put("order_status", status);

        long check = database.update("order_list", values, "invoice_id=?", new String[]{id});
        database.close();

        return check != -1;

    }

    //search orders
    //search clients
    public List<HashMap<String, String>> searchOrder(String invoiceId, String orderStatus) {
        List<HashMap<String, String>> client = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list WHERE order_status = '" + orderStatus + "' AND invoice_id LIKE '%" + invoiceId + "%' ORDER BY order_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("invoice_id", cursor.getString(1));
                map.put("order_type", cursor.getString(2));
                map.put("payment_method", cursor.getString(3));
                map.put("customer_name", cursor.getString(4));
                map.put("sub_total", cursor.getString(5));
                map.put("discount", cursor.getString(6));
                map.put("tax", cursor.getString(7));
                map.put("total_price", cursor.getString(8));
                map.put("order_status", cursor.getString(9));
                map.put("order_time", cursor.getString(10));

                String currentDate = cursor.getString(13) + "-" + cursor.getString(12) + "-" + cursor.getString(11);

                map.put("order_date", currentDate);

                client.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return client;
    }

    //get order history data
    public ArrayList<HashMap<String, String>> getOrderDetailsList(String invoiceID) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + invoiceID + "' ORDER BY order_details_id ASC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("invoice_id", cursor.getString(1));
                map.put("item_id", cursor.getString(2));
                map.put("item_weight", cursor.getString(3));
                map.put("item_qty", cursor.getString(4));
                map.put("stock", cursor.getString(5));
                map.put("item_price", cursor.getString(6));

                orderDetailsList.add(map);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }

    //add payments
    public boolean addPaymentIntoList(String invoicID, String paymentNo, String total_money, String total_payed, String now_payed, String due_money) {

        String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cDay = calendar.get(Calendar.DATE);

        String day = String.valueOf(cDay);
        String month = String.valueOf(cMonth);

        if (cDay < 10)
            day = "0" + day;

        if (cMonth < 10)
            month = "0" + month;

        ContentValues contentValues = new ContentValues();

        contentValues.put("invoice_id", invoicID);
        contentValues.put("payment_no", paymentNo);
        contentValues.put("total_money", total_money);
        contentValues.put("total_payed", total_payed);
        contentValues.put("now_payed", now_payed);
        contentValues.put("due_money", due_money);
        contentValues.put("payment_time", currentTime);
        contentValues.put("date", day);
        contentValues.put("month", month);
        contentValues.put("year", year);

        long check = database.insert("payment_list", null, contentValues);
        database.close();

        return check != -1;
    }

    //count payments
    public int getPaymentCount(String invoiceID) {

        Cursor cursor = database.rawQuery("SELECT * FROM payment_list WHERE invoice_id='" + invoiceID + "'", null);
        int paymentCount = cursor.getCount();

        cursor.close();
        database.close();
        return paymentCount;
    }

    //get payment list
    public List<HashMap<String, String>> getPaymentList(String invoiceID) {

        ArrayList<HashMap<String, String>> paymentList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_list WHERE invoice_id='" + invoiceID + "' ORDER BY payment_no DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("payment_no", cursor.getString(2));
                map.put("total_money", cursor.getString(3));
                map.put("total_payed", cursor.getString(4));
                map.put("now_payed", cursor.getString(5));
                map.put("due_money", cursor.getString(6));
                map.put("payment_time", cursor.getString(7));

                String currentDate = cursor.getString(10) + "-" + cursor.getString(9) + "-" + cursor.getString(8);
                map.put("payment_date", currentDate);

                paymentList.add(map);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return paymentList;

    }

    //get Single payment List
    public HashMap<String, String> getSinglePayment(String invoiceId, String paymentNo) {

        Cursor cursor = database.rawQuery("SELECT * FROM payment_list WHERE invoice_id='" + invoiceId + "' AND payment_no = '" + paymentNo + "'", null);

        HashMap<String, String> map = new HashMap<String, String>();

        if (cursor.moveToFirst()) {

            map.put("payment_no", cursor.getString(2));
            map.put("total_money", cursor.getString(3));
            map.put("total_payed", cursor.getString(4));
            map.put("now_payed", cursor.getString(5));
            map.put("due_money", cursor.getString(6));
            map.put("payment_time", cursor.getString(7));

            String currentDate = cursor.getString(10) + "-" + cursor.getString(9) + "-" + cursor.getString(8);
            map.put("payment_date", currentDate);
            map.put("year", cursor.getString(10));

        }

        cursor.close();
        database.close();
        return map;

    }

    //calculate total monthly expense
    public float getMonthlyExpenseAmount(String month, String year) {

        float expenseAmount = 0;

        String sql = "SELECT * FROM expenses WHERE month = '" + month + "' AND year = '" + year + "'";

        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    float cost = Float.parseFloat(cursor.getString(3));
                    expenseAmount = expenseAmount + cost;

                } while (cursor.moveToNext());

            } else {
                expenseAmount = 0;
            }

            cursor.close();
        }
        database.close();

        return expenseAmount;
    }

    //calculate total monthly sale
    public float getMonthlySalesAmount(String month, String year) {

        float salesAmount = 0;

        String sql = "SELECT * FROM order_list WHERE order_status = '" + Constant.COMPLETED + "' AND month = '" + month + "' AND year = '" + year + "'";

        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    float amount = Float.parseFloat(cursor.getString(8));
                    salesAmount = salesAmount + amount;

                } while (cursor.moveToNext());

            } else {
                salesAmount = 0;
            }

            cursor.close();
        }
        database.close();

        return salesAmount;
    }

    public float getYearlySaleAmount() {

        float totalSale = 0;

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);

        String sql = "SELECT total_price FROM order_list WHERE order_status = '" + Constant.COMPLETED + "' AND year = '" + mYear + "'";

        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {
                    float amount = Float.parseFloat(cursor.getString(0));
                    totalSale = totalSale + amount;

                } while (cursor.moveToNext());

            } else
                totalSale = 0;

            cursor.close();
        }
        database.close();

        return totalSale;

    }

    public float getYearlyExpenseAmount() {

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);

        float expenseAmount = 0;

        String sql = "SELECT expense_amount FROM expenses WHERE year = '" + mYear + "'";

        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    float cost = Float.parseFloat(cursor.getString(0));
                    expenseAmount = expenseAmount + cost;

                } while (cursor.moveToNext());

            } else {
                expenseAmount = 0;
            }

            cursor.close();
        }
        database.close();

        return expenseAmount;

    }

    public int totalItemsCount() {

        Cursor cursor = database.rawQuery("SELECT * FROM items", null);
        int itemCount = cursor.getCount();

        cursor.close();
        database.close();
        return itemCount;

    }

    public float getYearlyTotalPaidAmount(List<HashMap<String, String>> paymentMapList) {

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);

        float paid_amount = 0;

        for (HashMap<String, String> map : paymentMapList) {

            String invoiceId = map.get("invoice_id");
            String paymentId = map.get("payment_id");

            if (invoiceId != null && paymentId != null) {

                open();
                HashMap<String, String> singlePayment = getSinglePayment(invoiceId, paymentId);

                if (singlePayment != null) {

                    float paid = Float.parseFloat(Objects.requireNonNull(singlePayment.get("total_payed")));
                    int year = Integer.parseInt(Objects.requireNonNull(singlePayment.get("year")));

                    if (year == mYear) {
                        paid_amount = paid + paid_amount;
                    }

                }
            }

        }

        return paid_amount;

    }

    public float getYearlyTotalDueAmount(List<HashMap<String, String>> paymentMapList) {

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);

        float due_amount = 0;

        for (HashMap<String, String> map : paymentMapList) {

            String invoiceId = map.get("invoice_id");
            String paymentId = map.get("payment_id");

            if (invoiceId != null && paymentId != null) {

                open();
                HashMap<String, String> singlePayment = getSinglePayment(invoiceId, paymentId);

                if (singlePayment != null) {
                    float due = Float.parseFloat(Objects.requireNonNull(singlePayment.get("due_money")));
                    int year = Integer.parseInt(Objects.requireNonNull(singlePayment.get("year")));

                    if (year == mYear) {
                        due_amount = due + due_amount;
                    }
                }
            }

        }

        return due_amount;

    }


}
