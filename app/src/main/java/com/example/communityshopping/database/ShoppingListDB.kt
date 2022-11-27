package com.example.communityshopping.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.communityshopping.database.DbSettings.Companion.DATABASE_NAME
import com.example.communityshopping.database.DbSettings.Companion.DATABASE_VERSION

class ShoppingListDB(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val query = "CREATE TABLE " + TABLE_SHOPPING_LIST +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_TIMESTAMP + " LONG, " +
                COLUMN_DELETED + " INTEGER" + ")";
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST)
        onCreate(db)
    }

    fun getAllTableData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_SHOPPING_LIST, null)
    }

    fun addItem(name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_NAME, name)
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis())
        values.put(COLUMN_DELETED, 0)
        val id = db.insert(TABLE_SHOPPING_LIST, null, values)
        db.close()
        return id
    }

    fun deleteItem(id: Long) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DELETED, 1)
        db.update(TABLE_SHOPPING_LIST, values, "_id=" + id, null)
    }

    //add table column here
    companion object {
        const val TABLE_SHOPPING_LIST = "shopping_list"
        const val COLUMN_ID = "_id"
        const val COLUMN_ITEM_NAME = "shopping_item"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_DELETED = "deleteStatus"
    }
}