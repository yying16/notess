package com.notes.util

import android.graphics.Bitmap

object User {
    var username:String = "用户名"
    var telephone = ""
    var email = ""
    var userImage = 1
    var userImageBitmap:Bitmap? = null

    fun changeUserImage(bitmap: Bitmap){
        this.userImageBitmap = bitmap
    }

}