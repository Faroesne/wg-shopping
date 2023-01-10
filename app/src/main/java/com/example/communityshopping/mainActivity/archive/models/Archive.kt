package com.example.communityshopping.mainActivity.archive.models

class Archive(
    var fullPrice: Double,
    var title: String,
    var info: String,
    var index: String,
    var bmp: ByteArray,
    var username: String
) :
    java.io.Serializable {

}