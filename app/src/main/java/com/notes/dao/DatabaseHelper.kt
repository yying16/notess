package com.notes.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import com.notes.R
import com.notes.domain.Notes
import com.notes.util.BlobConverter
import com.notes.util.SpanUtil
import kotlin.collections.ArrayList

/**
 * 数据持久化
 * 主要用于持久化分组和错题
 * */
class DatabaseHelper(context: Context, version: Int) : SQLiteOpenHelper(context, "data.db", null, version) {

    private val sql1 = """
        create table t_photo
        (
            photo_id integer primary key autoincrement,
            photo_content blob not null ,
            photo_size int 
        )
    """.trimIndent()
    private val sql2 = """
        create index index_phone
                    on t_photo (photo_id)
    """.trimIndent()

    private val sql3 = """
        create table t_text
        (
            text_id integer primary key autoincrement,
            text_content text 
        )
    """.trimIndent()

    private val sql4 = """
         create index index_text
            on t_text (text_id)
    """.trimIndent()

    private val sql5 = """
        create table t_notes
        (
            notes_id    integer primary key autoincrement,
            notes_title nvarchar(200),
            text_list   text,
            photo_list  text,
            order_list  text,
            update_time datetime,
            classify nvarchar(4),
            deleted boolean default 0,
            word_number int
        )
    """.trimIndent()

    private val sql6 = """
        create index index_notes
            on t_notes (notes_id)
    """.trimIndent()


    override fun onCreate(dao: SQLiteDatabase?) {
        dao?.execSQL(sql1)
        dao?.execSQL(sql2)
        dao?.execSQL(sql3)
        dao?.execSQL(sql4)
        dao?.execSQL(sql5)
        dao?.execSQL(sql6)
    }

    override fun onUpgrade(dao: SQLiteDatabase?, v1: Int, v2: Int) {

    }

    fun initDataBase() {
        Log.d("TAG", "initData: ${SpanUtil.main == null}")
        if (SpanUtil.main != null) {
            val bitmap = BitmapFactory.decodeResource(SpanUtil.main!!.resources, R.drawable.ic_default_avatar, null)
            Log.d(TAG, "initDataBase: ${bitmap.javaClass.toString()}")
            val photo = BlobConverter.avatarEncoder(bitmap)

            val values = ContentValues().apply {
                put("photo_id", "1")
                put("photo_content", photo)
                put("photo_size", photo.size)
            }
            writableDatabase.insert("t_photo", null, values)
        }

    }


    fun changePhoto(photo_id: Int, bitmap: Bitmap) {
        val values = ContentValues()
        values.put("photo_id", photo_id)
        values.put("photo_content", BlobConverter.avatarEncoder(bitmap))
        writableDatabase.update("t_photo", values, "photo_id = ?", arrayOf(photo_id.toString()))
    }

    @SuppressLint("Range")
    fun getPhoto(photo_id: Int): Bitmap? {
        val cursor = writableDatabase.query("t_photo", null, "photo_id=?", arrayOf("$photo_id"), null, null, null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            val blob = cursor.getBlob(cursor.getColumnIndex("photo_content"))
            return BlobConverter.avatarDecoder(blob)
        }
        return null
    }

    /**
     * 插入数据
     * */
    fun insert(str: String): Int {
        val values = ContentValues().apply {
            put("text_content", str)
        }
        writableDatabase.insert("t_text", null, values)
        return readableDatabase.query("t_text", null, null, null, null, null, null, null).count
    }

    fun insert(bitmap: Bitmap): Int {
        val b = BlobConverter.avatarEncoder(bitmap)
        val values = ContentValues().apply {
            put("photo_content", b)
        }
        writableDatabase.insert("t_photo", null, values)
        return readableDatabase.query("t_photo", null, null, null, null, null, null, null).count
    }

    fun insert(notes: Notes): Int {
        val values = ContentValues().apply {
            put("notes_title", notes.notesTitle)
            put("text_list", notes.textList)
            put("photo_list", notes.photoList)
            put("order_list", notes.orderList)
            put("update_time", notes.updateTime)
            put("word_number", notes.wordNumber)
            put("classify", notes.classify)
        }
        writableDatabase.insert("t_notes", null, values)
        return readableDatabase.query("t_notes", null, null, null, null, null, null, null).count
    }

    @SuppressLint("Range")
    fun getNotes(): ArrayList<Notes> {
        val ret = ArrayList<Notes>()
        val cursor = readableDatabase.query("t_notes", null, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do { // 遍历Cursor对象，取出数据并打印
                val noteId = cursor.getInt(cursor.getColumnIndex("notes_id"))
                val notesTitle = cursor.getString(cursor.getColumnIndex("notes_title"))
                val textList = cursor.getString(cursor.getColumnIndex("text_list"))
                val photoList = cursor.getString(cursor.getColumnIndex("photo_list"))
                val orderList = cursor.getString(cursor.getColumnIndex("order_list"))
                val wordNumber = cursor.getInt(cursor.getColumnIndex("word_number"))
                val updateTime = cursor.getString(cursor.getColumnIndex("update_time"))
                val classify = cursor.getString(cursor.getColumnIndex("classify"))
                ret.add(Notes(noteId, notesTitle, textList, photoList, orderList, updateTime, classify, wordNumber))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ret
    }

    /**
     * 获得错题详情
     * */
    @SuppressLint("Range")
    fun getDetail(notesId: Int): Notes? {
        var ret: Notes? = null
        val cursor = readableDatabase.query("t_notes", null, "notes_id=?", arrayOf("$notesId"), null, null, null, null)
        if (cursor.moveToFirst()) {
            val noteId = cursor.getInt(cursor.getColumnIndex("notes_id"))
            val notesTitle = cursor.getString(cursor.getColumnIndex("notes_title"))
            val textList = cursor.getString(cursor.getColumnIndex("text_list"))
            val photoList = cursor.getString(cursor.getColumnIndex("photo_list"))
            val orderList = cursor.getString(cursor.getColumnIndex("order_list"))
            val wordNumber = cursor.getInt(cursor.getColumnIndex("word_number"))
            val updateTime = cursor.getString(cursor.getColumnIndex("update_time"))
            val classify = cursor.getString(cursor.getColumnIndex("classify"))
            ret = Notes(noteId, notesTitle, textList, photoList, orderList, updateTime, classify, wordNumber)
        }
        cursor.close()
        return ret
    }

    @SuppressLint("Range")
    fun getText(id:Int):String{
        val cursor = writableDatabase.query("t_text", null, "text_id=?", arrayOf("$id"), null, null, null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            return cursor.getString(cursor.getColumnIndex("text_content"))
        }
        return ""
    }

}
