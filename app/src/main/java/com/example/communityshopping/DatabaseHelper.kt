package com.example.communityshopping

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val query = "CREATE TABLE " + TABLE_SHOPPING_LIST +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT);"
        db!!.execSQL(query)

        //TODO Create other tables here (to add more tables use db.execSQL(customizedQuery))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST)
        onCreate(db)
    }
    fun getAllTableData(table: Table): Cursor? {
        if(table == Table.TABLE_SHOPPING_LIST){
            val db = this.readableDatabase
            return db.rawQuery("SELECT * FROM " + TABLE_SHOPPING_LIST, null)
        }
        else{
            return null
            // TODO add other tables here
        }
    }
    fun addItem(name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_NAME, name)
        val id =  db.insert(TABLE_SHOPPING_LIST, null, values)
        db.close()
        return id
        //TODO write different add methods for each screen
    }
    fun deleteItem(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_SHOPPING_LIST,"_id="+id,null)
    }

    companion object {
        private val DATABASE_NAME = "wgShopping.db"
        private val DATABASE_VERSION = 1

        val TABLE_SHOPPING_LIST = "shopping_list"
        val COLUMN_ID = "_id"
        val COLUMN_ITEM_NAME = "shopping_item"
    }
}