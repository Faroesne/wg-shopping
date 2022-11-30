package com.example.communityshopping.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.communityshopping.database.DbSettings.Companion.DATABASE_NAME
import com.example.communityshopping.database.DbSettings.Companion.DATABASE_VERSION

class ShoppingListDB(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val queryShoppingList = "CREATE TABLE " + TABLE_SHOPPING_LIST +
                " (" + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT);"
        val queryArchive = "CREATE TABLE " + TABLE_ARCHIVE +
                " (" + COLUMN_ARCHIVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ARCHIVE_USERNAME + " TEXT, " +
                COLUMN_ARCHIVE_DATE + " TEXT);"
        val queryArchiveItem = "CREATE TABLE " + TABLE_ARCHIVE_ITEM +
                " (" + COLUMN_ARCHIVE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_PRICE + " REAL, " +
                COLUMN_ITEM_ID + " INTEGER REFERENCES " +
                TABLE_SHOPPING_LIST + " (" + COLUMN_ITEM_ID + "), " +
                COLUMN_ARCHIVE_ID + " INTEGER REFERENCES " +
                TABLE_ARCHIVE + " (" + COLUMN_ARCHIVE_ID + "));"
        val queryFillShoppingDB = "INSERT INTO " + TABLE_SHOPPING_LIST + " (" +
                COLUMN_ITEM_NAME + ") VALUES ('Item1'), " +
                "('Item2'), ('Item3'), ('Item4'), ('Item5');"
        val queryFillArchiveDB = "INSERT INTO " + TABLE_ARCHIVE + " (" +
                COLUMN_ARCHIVE_USERNAME + ", " + COLUMN_ARCHIVE_DATE +
                ") VALUES ('Nutzer1', '29.11.2022'), " +
                "('Nutzer2', '30.11.2022');"
        val queryFillArchiveItemDB = "INSERT INTO " + TABLE_ARCHIVE_ITEM + " (" +
                COLUMN_ITEM_PRICE + ", " + COLUMN_ITEM_ID + ", " +
                COLUMN_ARCHIVE_ID + ") VALUES (2.99, 1, 1), " +
                "(1.99, 2, 1), " +
                "(3.99, 3, 1), " +
                "(4.99, 4, 2), " +
                "(2.99, 5, 2);"
        db!!.execSQL(queryShoppingList)
        db!!.execSQL(queryArchive)
        db!!.execSQL(queryArchiveItem)
        db!!.execSQL(queryFillShoppingDB)
        db!!.execSQL(queryFillArchiveDB)
        db!!.execSQL(queryFillArchiveItemDB)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST + ", " + TABLE_ARCHIVE + ", " + TABLE_ARCHIVE_ITEM)
        onCreate(db)
    }

    fun getShoppingListData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_SHOPPING_LIST, null)
    }

    fun getArchiveData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_ARCHIVE, null)
    }

    fun getArchiveItemData(index: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM " + TABLE_ARCHIVE_ITEM + " WHERE " + COLUMN_ARCHIVE_ID + " = " + index,
            null
        )
    }

    fun addShoppingListItem(name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_NAME, name)
        val id = db.insert(TABLE_SHOPPING_LIST, null, values)
        db.close()
        return id
    }

    fun addArchiveListItem(name: String, date: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ARCHIVE_USERNAME, name)
        values.put(COLUMN_ARCHIVE_DATE, date)
        val id = db.insert(TABLE_ARCHIVE, null, values)
        db.close()
        return id
    }

    fun addArchiveItem(price: Double, listFK: Int, itemFK: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_PRICE, price)
        values.put(COLUMN_ITEM_ID, itemFK)
        values.put(COLUMN_ARCHIVE_ID, listFK)
        val id = db.insert(TABLE_ARCHIVE_ITEM, null, values)
        db.close()
        return id
    }

    fun deleteShoppingListItem(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_SHOPPING_LIST, "_id=" + id, null)
    }

    //add table column here
    companion object {
        const val TABLE_SHOPPING_LIST = "shopping_list"
        const val COLUMN_ITEM_ID = "shopping_list_id"
        const val COLUMN_ITEM_NAME = "shopping_item"

        const val TABLE_ARCHIVE = "archive_list"
        const val COLUMN_ARCHIVE_USERNAME = "archive_name"
        const val COLUMN_ARCHIVE_DATE = "archive_date"

        const val TABLE_ARCHIVE_ITEM = "archive_item"
        const val COLUMN_ARCHIVE_ITEM_ID = "archive_item_id"
        const val COLUMN_ARCHIVE_ID = "archive_id"
        const val COLUMN_ITEM_PRICE = "item_price"
    }
}