package com.example.communityshopping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    private val TABLE_SHOPPING_LIST = "shopping_list"
    private val COLUMN_ID = "_id"
    private val COLUMN_ITEM = "shopping_item"

    override fun onCreate(db: SQLiteDatabase?) {

        val query = "CREATE TABLE " + TABLE_SHOPPING_LIST +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM + " TEXT);"
        db!!.execSQL(query)

        //TODO Create other tables here (to add more tables use db.execSQL(customizedQuery))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST)
        onCreate(db)
    }
    private fun add(){
        val db = this.writableDatabase
        //TODO debate how to structure database add method e.g. write different add methods for each screen
    }
    private fun delete(){
        //TODO debate how to structure database delete method
    }
}