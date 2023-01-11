package com.example.communityshopping.communication

import android.content.Context
import com.example.communityshopping.communication.SocketStatus.*
import com.example.communityshopping.database.ShoppingListDB
import org.json.JSONArray
import org.json.JSONObject

class DbJSONWrapper(private var context: Context) {

    fun writeShoppingListDbJSON(): JSONObject {

        val db = ShoppingListDB(context, null)
        val cursor = db.getShoppingListData()

        val dbShoppingListJSON = JSONObject()
        dbShoppingListJSON.put("MessageType", SYNC_ALL.toString())
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
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_ID,columnID)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_NAME,columnName)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_TIMESTAMP,columnTimeStamp)
                singleItemJSON.put(ShoppingListDB.COLUMN_ITEM_DELETED,columnDeleteStatus)

                itemList.add(singleItemJSON)
            }
            cursor.close()

            dbShoppingListJSON.put(ShoppingListDB.TABLE_SHOPPING_LIST,JSONArray(itemList))
        }
        return dbShoppingListJSON
    }

    fun synchronizeDataWithCurrentDB(jsonObject: JSONObject) {
        val db = ShoppingListDB(context, null)
        val itemList = jsonObject.getJSONArray(ShoppingListDB.TABLE_SHOPPING_LIST)

        var i = 0
        while (i<itemList.length()){
            var uuid = (itemList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ITEM_ID)
            var name = (itemList[i] as JSONObject).getString(ShoppingListDB.COLUMN_ITEM_NAME)
            var timeStamp = (itemList[i] as JSONObject).getLong(ShoppingListDB.COLUMN_ITEM_TIMESTAMP)
            var deleteStatus = (itemList[i] as JSONObject).getInt(ShoppingListDB.COLUMN_ITEM_DELETED)
            db.insertOrUpdateShoppingListItem(uuid, name, timeStamp, deleteStatus)
            i++
        }

    }

}