package com.example.communityshopping.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.communityshopping.database.DbSettings.Companion.DATABASE_NAME
import com.example.communityshopping.database.DbSettings.Companion.DATABASE_VERSION
import java.math.RoundingMode
import java.util.*

class ShoppingListDB(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val queryShoppingList = "CREATE TABLE " + TABLE_SHOPPING_LIST +
                " (" + COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_TIMESTAMP + " LONG, " +
                COLUMN_DELETED + " INTEGER);"
        val queryArchive = "CREATE TABLE " + TABLE_ARCHIVE +
                " (" + COLUMN_ARCHIVE_ID + " TEXT PRIMARY KEY, " +
                COLUMN_ARCHIVE_FULL_PRICE + " REAL, " +
                COLUMN_ARCHIVE_USERNAME + " TEXT, " +
                COLUMN_ARCHIVE_DATE + " LONG, " +
                COLUMN_ARCHIVE_PAID + " INTEGER, " +
                COLUMN_ARCHIVE_IMAGE + " BLOB);"
        val queryArchiveItem = "CREATE TABLE " + TABLE_ARCHIVE_ITEM +
                " (" + COLUMN_ARCHIVE_ITEM_ID + " TEXT PRIMARY KEY, " +
                COLUMN_ITEM_PRICE + " REAL, " +
                COLUMN_ITEM_ID + " TEXT REFERENCES " +
                TABLE_SHOPPING_LIST + " (" + COLUMN_ITEM_ID + "), " +
                COLUMN_ARCHIVE_ID + " TEXT REFERENCES " +
                TABLE_ARCHIVE + " (" + COLUMN_ARCHIVE_ID + "));"
        val queryUserFinances = "CREATE TABLE " + TABLE_USER_FINANCES +
                " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_USER_FINANCES + " REAL);"
        val queryFillShoppingDB = "INSERT INTO " + TABLE_SHOPPING_LIST + " (" +
                COLUMN_ITEM_ID + ", " +
                COLUMN_ITEM_NAME + ", " + COLUMN_TIMESTAMP + ", " + COLUMN_DELETED +
                ") VALUES ('0', 'Karotten', 5, 1), " +
                "('1', 'Rinderkennzeichnungsfleischetikettierungsmaschine', 5, 1), " +
                "('2', 'Äpfel', 5, 1), " +
                "('3', 'Shampoo', 5, 1), " +
                "('4', 'Ziegenmilchkonzentrat', 5, 1);"
        val queryFillArchiveDB = "INSERT INTO " + TABLE_ARCHIVE + " (" +
                COLUMN_ARCHIVE_ID + ", " +
                COLUMN_ARCHIVE_FULL_PRICE + ", " + COLUMN_ARCHIVE_USERNAME + ", " +
                COLUMN_ARCHIVE_DATE + ", " + COLUMN_ARCHIVE_PAID + ", " + COLUMN_ARCHIVE_IMAGE +
                ") VALUES ('0', 8.97, 'Fabian', 1669724179533, 1, '1A'), " +
                "('1', 5.00, 'Alen', 1669824179533, 0,  '1A');"
        val queryFillArchiveItemDB = "INSERT INTO " + TABLE_ARCHIVE_ITEM + " (" +
                COLUMN_ARCHIVE_ITEM_ID + ", " +
                COLUMN_ITEM_PRICE + ", " + COLUMN_ITEM_ID + ", " +
                COLUMN_ARCHIVE_ID + ") VALUES ('0', 2.99, 1, '0'), " +
                "('1',1.99, 2, '0'), " +
                "('2',3.99, 3, '0'), " +
                "('3',null, 4, '1'), " +
                "('4',null, 5, '1');"
        val queryFillUserFinancesDB = "INSERT INTO " + TABLE_USER_FINANCES + " (" +
                COLUMN_USER_NAME + ", " + COLUMN_USER_FINANCES +
                ") VALUES ('Alen', 9.99), " +
                "('Fabian', 6.85);"
        db!!.execSQL(queryShoppingList)
        db!!.execSQL(queryArchive)
        db!!.execSQL(queryArchiveItem)
        db!!.execSQL(queryUserFinances)
        db!!.execSQL(queryFillShoppingDB)
        db!!.execSQL(queryFillArchiveDB)
        db!!.execSQL(queryFillArchiveItemDB)
        db!!.execSQL(queryFillUserFinancesDB)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST + ", " + TABLE_ARCHIVE + ", " + TABLE_ARCHIVE_ITEM)
        onCreate(db)
    }

    fun getShoppingListData(): Cursor? {
        val db = this.readableDatabase
        return db.query(
            TABLE_SHOPPING_LIST,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    fun getShoppingListDataByID(index: String): Cursor? {
        val db = this.readableDatabase
        val projection = arrayOf(COLUMN_ITEM_NAME)
        val selection = "${COLUMN_ITEM_ID} = '${index}'"
        return db.query(
            TABLE_SHOPPING_LIST,
            projection,
            selection,
            null,
            null,
            null,
            null
        )
    }

    fun getArchiveData(): Cursor? {
        val db = this.readableDatabase
        return db.query(
            TABLE_ARCHIVE,
            null,
            null,
            null,
            null,
            null,
            COLUMN_ARCHIVE_DATE
        )
    }

    fun getArchiveItemData(index: String): Cursor? {
        val db = this.readableDatabase
        val selection = "${COLUMN_ARCHIVE_ID} = '${index}'"

        return db.query(
            "${TABLE_ARCHIVE_ITEM} INNER JOIN ${TABLE_SHOPPING_LIST} USING (${COLUMN_ITEM_ID})",
            null,
            selection,
            null,
            null,
            null,
            null
        )
    }

    fun addShoppingListItem(name: String): String {
        val db = this.writableDatabase
        val values = ContentValues()
        val id = UUID.randomUUID().toString()
        values.put(COLUMN_ITEM_ID, id)
        values.put(COLUMN_ITEM_NAME, name)
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis())
        values.put(COLUMN_DELETED, 0)
        db.insert(TABLE_SHOPPING_LIST, null, values)
        db.close()
        return id
    }


    fun addArchiveListItem(price: Double, name: String, img: ByteArray): String {
        val db = this.writableDatabase
        val values = ContentValues()
        val id = UUID.randomUUID().toString()
        values.put(COLUMN_ARCHIVE_ID, id)
        values.put(COLUMN_ARCHIVE_FULL_PRICE, price)
        values.put(COLUMN_ARCHIVE_USERNAME, name)
        values.put(COLUMN_ARCHIVE_DATE, System.currentTimeMillis())
        values.put(COLUMN_ARCHIVE_PAID, 0)
        values.put(COLUMN_ARCHIVE_IMAGE, img)
        db.insert(TABLE_ARCHIVE, null, values)
        db.close()
        return id
    }

    fun addArchiveItem(price: Double?, itemFK: String, archiveFK: String): String {
        val db = this.writableDatabase
        val values = ContentValues()
        val id = UUID.randomUUID().toString()
        values.put(COLUMN_ARCHIVE_ITEM_ID, id)
        values.put(COLUMN_ITEM_PRICE, price)
        values.put(COLUMN_ITEM_ID, itemFK)
        values.put(COLUMN_ARCHIVE_ID, archiveFK)
        db.insert(TABLE_ARCHIVE_ITEM, null, values)
        db.close()
        return id
    }

    fun addUser(username: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, username)
        values.put(COLUMN_USER_FINANCES, 0)
        val id = db.insert(TABLE_USER_FINANCES, null, values)
        db.close()
        return id
    }


    fun getUserFinancesData(): Cursor? {
        val db = this.readableDatabase
        return db.query(
            TABLE_USER_FINANCES,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    fun addFinanceToUsers(name: String, price: Double): Double {
        val db = this.writableDatabase
        var financeDataCursor = getUserFinancesData()
        var personalPrice = price / financeDataCursor!!.count
        personalPrice = personalPrice.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        while (financeDataCursor.moveToNext()) {
            if (financeDataCursor.getString(financeDataCursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_NAME)) != name) {
                val values = ContentValues()
                var finance = financeDataCursor.getDouble(
                    financeDataCursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_FINANCES)
                )
                values.put(COLUMN_USER_FINANCES, finance.plus(personalPrice))
                db.update(
                    TABLE_USER_FINANCES, values, "$COLUMN_USER_ID LIKE ?",
                    arrayOf(
                        financeDataCursor.getInt(
                            financeDataCursor.getColumnIndexOrThrow(
                                ShoppingListDB.COLUMN_USER_ID
                            )
                        ).toString()
                    )
                )
            }
        }
        financeDataCursor.close()
        db.close()
        return personalPrice
    }

    fun clearFinance() {
        val db = this.writableDatabase
        var financeDataCursor = getUserFinancesData()
        while (financeDataCursor!!.moveToNext()) {
            val values = ContentValues()
            values.put(COLUMN_USER_FINANCES, 0)
            db.update(TABLE_USER_FINANCES, values, null, null)
        }
    }

    fun deleteShoppingListItem(id: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DELETED, 1)
        db.update(TABLE_SHOPPING_LIST, values, "$COLUMN_ITEM_ID = '$id'", null)
    }

    //add table column here
    companion object {
        const val TABLE_SHOPPING_LIST = "shopping_list"
        const val COLUMN_ITEM_ID = "shopping_list_id"
        const val COLUMN_ITEM_NAME = "shopping_item"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_DELETED = "deleteStatus"

        const val TABLE_ARCHIVE = "archive_list"
        const val COLUMN_ARCHIVE_USERNAME = "archive_name"
        const val COLUMN_ARCHIVE_DATE = "archive_date"
        const val COLUMN_ARCHIVE_FULL_PRICE = "archive_full_price"
        const val COLUMN_ARCHIVE_PAID = "archive_paid"
        const val COLUMN_ARCHIVE_IMAGE = "item_image"

        const val TABLE_ARCHIVE_ITEM = "archive_item"
        const val COLUMN_ARCHIVE_ITEM_ID = "archive_item_id"
        const val COLUMN_ARCHIVE_ID = "archive_id"
        const val COLUMN_ITEM_PRICE = "item_price"


        const val TABLE_USER_FINANCES = "user_finances"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_NAME = "user_name"
        const val COLUMN_USER_FINANCES = "user_finance"
    }
}