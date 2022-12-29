package com.example.communityshopping.mainActivity.archive.models

class Archive(
    var fullPrice: Double,
    var title: String,
    var info: String,
    var index: Int,
    var bmp: ByteArray,
    var username: String
) :
    java.io.Serializable {

}