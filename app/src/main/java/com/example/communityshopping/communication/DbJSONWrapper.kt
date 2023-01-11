package com.example.communityshopping.communication

import android.content.Context
import com.example.communityshopping.communication.SocketStatus.SYNC_ALL
import com.example.communityshopping.database.ShoppingListDB
import org.json.JSONArray
import org.json.JSONObject

class DbJSONWrapper(private var context: Context) {

    fun createCompleteDbJSON(): JSONObject {
        val dbCompleteJSON = JSONObject()
        dbCompleteJSON.put("MessageType", SYNC_ALL.toString())
        dbCompleteJSON.put(ShoppingListDB.TABLE_SHOPPING_LIST, writeShoppingListDbJSON())
        dbCompleteJSON.put(ShoppingListDB.TABLE_ARCHIVE, writeArchiveListDbJSON())
        dbCompleteJSON.put(ShoppingListDB.TABLE_ARCHIVE_ITEM, writeArchiveItemListDbJSON())
        dbCompleteJSON.put(ShoppingListDB.TABLE_USER,writeUserListDbJSON())

        return dbCompleteJSON
    }

    private fun writeShoppingListDbJSON(): JSONArray {

        val db = ShoppingListDB(context, null)
        val cursor = db.getShoppingListData()
        val itemList = arrayListOf<JSONObject>()

        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val columnID =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_ID))
                val columnName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_NAME))
                val columnTimeStamp =
                    cursor.getLong(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_TIMESTAMP))
                val columnDeleteStatus =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_DELETED))

                val singleItemJSON = JSONObject()
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_ID, columnID)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_NAME, columnName)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_TIMESTAMP, columnTimeStamp)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_DELETED, columnDeleteStatus)

                itemList.add(singleItemJSON)
            }
            cursor.close()
        }
        return JSONArray(itemList)
    }

    private fun writeArchiveListDbJSON(): JSONArray {
        val db = ShoppingListDB(context, null)
        val cursor = db.getArchiveData()

        val dbArchiveListJSON = JSONObject()
        val itemList = arrayListOf<JSONObject>()

        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val columnID =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_ID))
                val columnPrice =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_FULL_PRICE))
                val columnUserName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_USERNAME))
                val columnArchiveDate =
                    cursor.getLong(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_DATE))
                val columnArchivePaid =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_PAID))

                val singleItemJSON = JSONObject()
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_ID, columnID)
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_FULL_PRICE, columnPrice)
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_USERNAME, columnUserName)
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_DATE, columnArchiveDate)
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_PAID, columnArchivePaid)

                itemList.add(singleItemJSON)
            }
            cursor.close()

            dbArchiveListJSON.put(ShoppingListDB.TABLE_ARCHIVE, JSONArray(itemList))
        }

        return JSONArray(itemList)
    }
    private fun writeUserListDbJSON(): JSONArray {
        val db = ShoppingListDB(context, null)
        val cursor = db.getUserFinancesData()

        val dbUserListJSON = JSONObject()
        val itemList = arrayListOf<JSONObject>()

        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val columnID =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_ID))
                val columnUserName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_NAME))


                val singleItemJSON = JSONObject()
                singleItemJSON.put(ShoppingListDB.COLUMN_USER_ID, columnID)
                singleItemJSON.put(ShoppingListDB.COLUMN_USER_NAME, columnUserName)

                itemList.add(singleItemJSON)
            }
            cursor.close()

            dbUserListJSON.put(ShoppingListDB.TABLE_USER, JSONArray(itemList))
        }

        return JSONArray(itemList)
    }

    private fun writeArchiveItemListDbJSON(): JSONArray {
        val db = ShoppingListDB(context, null)
        val cursor = db.getArchiveItemData()

        val dbArchiveItemListJSON = JSONObject()
        val itemList = arrayListOf<JSONObject>()

        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val columnArchiveItemID =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_ITEM_ID))
                val columnPrice =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_PRICE))
                val columnItemID =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_ID))
                val columnArchiveID =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_ID))

                val singleItemJSON = JSONObject()
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_ITEM_ID, columnArchiveItemID)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_PRICE, columnPrice)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_ID, columnItemID)
                singleItemJSON.put(ShoppingListDB.COLUMN_ARCHIVE_ID, columnArchiveID)

                itemList.add(singleItemJSON)
            }
            cursor.close()

            dbArchiveItemListJSON.put(ShoppingListDB.TABLE_ARCHIVE_ITEM, JSONArray(itemList))
        }

        return JSONArray(itemList)
    }

    fun synchronizeDataWithCurrentDB(jsonObject: JSONObject) {
        val db = ShoppingListDB(context, null)

        val shoppingList = jsonObject.getJSONArray(ShoppingListDB.TABLE_SHOPPING_LIST)
        var i = 0
        while (i < shoppingList.length()) {
            var uuid = (shoppingList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ITEM_ID)
            var name = (shoppingList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ITEM_NAME)
            var timeStamp =
                (shoppingList[i] as JSONObject).getLong(ShoppingListDB.COLUMN_ITEM_TIMESTAMP)
            var deleteStatus =
                (shoppingList[i] as JSONObject).getInt(ShoppingListDB.COLUMN_ITEM_DELETED)
            db.insertOrUpdateShoppingListItem(uuid, name, timeStamp, deleteStatus)
            i++
        }

        val archiveList = jsonObject.getJSONArray(ShoppingListDB.TABLE_ARCHIVE)
        i = 0
        while (i < archiveList.length()) {
            var archiveID =
                (archiveList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ARCHIVE_ID)
            var archiveFullPrice =
                (archiveList[i] as JSONObject).getDouble(ShoppingListDB.COLUMN_ARCHIVE_FULL_PRICE)
            var archiveUserName =
                (archiveList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ARCHIVE_USERNAME)
            var archiveDate =
                (archiveList[i] as JSONObject).getLong(ShoppingListDB.COLUMN_ARCHIVE_DATE)
            var archivePaid =
                (archiveList[i] as JSONObject).getInt(ShoppingListDB.COLUMN_ARCHIVE_PAID)
            db.insertOrUpdateArchiveListItem(
                archiveID,
                archiveFullPrice,
                archiveUserName,
                archiveDate,
                archivePaid
            )
            i++
        }

        val archiveItemList = jsonObject.getJSONArray(ShoppingListDB.TABLE_ARCHIVE_ITEM)
        i = 0
        while (i < archiveItemList.length()) {
            var archiveItemID =
                (archiveItemList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ARCHIVE_ITEM_ID)
            var archivePrice =
                (archiveItemList[i] as JSONObject).getDouble(ShoppingListDB.COLUMN_ITEM_PRICE)
            var itemID = (archiveItemList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ITEM_ID)
            var archiveID =
                (archiveItemList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ARCHIVE_ID)
            db.insertOrUpdateArchiveItemListItem(archiveItemID, archivePrice, itemID, archiveID)
            i++
        }

        val userList = jsonObject.getJSONArray(ShoppingListDB.TABLE_USER)
        i = 0
        while (i < userList.length()) {
            var userID =
                (userList[i] as JSONObject).getString(ShoppingListDB.COLUMN_USER_ID)
            var userName =
                (userList[i] as JSONObject).getString(ShoppingListDB.COLUMN_USER_NAME)
            db.insertOrUpdateUser(userID, userName)
            i++
        }

    }

}