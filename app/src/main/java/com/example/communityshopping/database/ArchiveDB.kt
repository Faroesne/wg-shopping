package com.example.communityshopping.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.communityshopping.database.ShoppingListDB.Companion.COLUMN_ITEM_NAME
import com.example.communityshopping.database.ShoppingListDB.Companion.TABLE_SHOPPING_LIST

class ArchiveDB(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DbSettings.DATABASE_NAME, factory, DbSettings.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val query = "CREATE TABLE " + TABLE_ARCHIVE +
                " (" + COLUMN_ARCHIVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ARCHIVE_USERNAME + " TEXT, " +
                COLUMN_ARCHIVE_DATE + " TEXT); " +
                "CREATE TABLE " + TABLE_ARCHIVE_ITEM +
                " (FOREIGN KEY(" + COLUMN_FK_ID +
                ") REFERENCES " + TABLE_ARCHIVE +
                "(" + COLUMN_ARCHIVE_ID + "), FOREIGN KEY(" +
                COLUMN_ITEM_OBJ + ") REFERENCES " +
                TABLE_SHOPPING_LIST + "(" + COLUMN_ITEM_NAME + "), " +
                COLUMN_ITEM_PRICE + " REAL);"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVE + ", " + TABLE_ARCHIVE_ITEM)
        onCreate(db)
    }

    fun getAllTableData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_ARCHIVE, null)
    }

    fun addItem(name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ARCHIVE_USERNAME, name)
        val id = db.insert(TABLE_ARCHIVE, null, values)
        db.close()
        return id
    }

    fun deleteItem(id: Long) {
    }

    //add table column here
    companion object {
        const val TABLE_ARCHIVE = "archive_list"
        const val COLUMN_ARCHIVE_ID = "_id"
        const val COLUMN_ARCHIVE_USERNAME = "archive_name"
        const val COLUMN_ARCHIVE_DATE = "archive_date"
        const val TABLE_ARCHIVE_ITEM = "archive_item"
        const val COLUMN_FK_ID = "archive_id"
        const val COLUMN_ITEM_OBJ = "item_object"
        const val COLUMN_ITEM_PRICE = "item_price"
    }
}