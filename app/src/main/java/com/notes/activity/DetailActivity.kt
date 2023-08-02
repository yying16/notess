package com.notes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.notes.R
import com.notes.dao.DatabaseHelper
import com.notes.ui.NotesImage
import com.notes.ui.NotesText
import com.notes.util.BlobConverter
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity() : AppCompatActivity() {
    private val db = DatabaseHelper(this, 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init()
    }

    /*初始化*/
    private fun init() {
        val id = intent.getIntExtra("id",0)
        if(id>0){
            val notes = db.getDetail(id)
            val order = notes?.orderList
            titleText.text = notes?.notesTitle
            val texts = notes?.textList?.split("#")
            val photos = notes?.photoList?.split("#")
            contentText.text = texts?.get(0)?.toInt()?.let { db.getText(it) }
            var j = 1
            var k = 0
            for (i in 1 until notes?.orderList?.length!!){
                if(order?.get(i)=='t'){
                    val t = TextView(this)
                    t.text = texts?.get(j)?.toInt()?.let { db.getText(it) } ?: ""
                    j++
                    container.addView(t)
                }else{
                    val p = NotesImage(this)
                    val b = photos?.get(k)?.toInt()?.let { db.getPhoto(it) }
                    p.setImageBitmap(b)
                    k++
                    container.addView(p)
                }
            }
        }
    }
}