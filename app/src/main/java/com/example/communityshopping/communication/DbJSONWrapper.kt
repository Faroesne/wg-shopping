package com.example.communityshopping.communication

import android.content.Context
import com.example.communityshopping.database.ShoppingListDB
import org.json.JSONArray
import org.json.JSONObject

class DbJSONWrapper(private var context: Context) {

    fun writeShoppingListDbJSON(): JSONObject {

        val db = ShoppingListDB(context, null)
        val cursor = db.getShoppingListData()

        val dbShoppingListJSON = JSONObject()
        dbShoppingListJSON.put("MessageType","SYNC_ALL")
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
                singleItemJSON.put("itemID",columnID)
                singleItemJSON.put("itemName",columnName)
                singleItemJSON.put("itemTimeStamp",columnTimeStamp)
                singleItemJSON.put("itemDeleteStatus",columnDeleteStatus)

                itemList.add(singleItemJSON)
            }
            cursor.close()

            dbShoppingListJSON.put("Data",JSONArray(itemList))
        }
        return dbShoppingListJSON
    }

    fun synchronizeDataWithCurrentDB(jsonObject: JSONObject) {
        val db = ShoppingListDB(context, null)
        val itemList = jsonObject.getJSONArray("Data")

        var i = 0
        while (i<itemList.length()){
            var uuid = (itemList[i] as JSONObject).getString("itemID")
            var name = (itemList[i] as JSONObject).getString("itemName")
            var timeStamp = (itemList[i] as JSONObject).getLong("itemTimeStamp")
            var deleteStatus = (itemList[i] as JSONObject).getInt("itemDeleteStatus")
            db.insertOrUpdateShoppingListItem(uuid, name, timeStamp, deleteStatus)
            i++
        }

    }

}