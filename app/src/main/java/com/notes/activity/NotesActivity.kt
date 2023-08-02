package com.notes.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.notes.R
import com.notes.dao.DatabaseHelper
import com.notes.domain.Notes
import com.notes.ui.NotesImage
import com.notes.ui.NotesText
import com.notes.util.SpanUtil
import kotlinx.android.synthetic.main.activity_notes.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class NotesActivity : AppCompatActivity() {
    private val takePhoto = 1
    private val fromAlbum = 2
    private val db = DatabaseHelper(this, 1)
    lateinit var imageUri: Uri
    lateinit var outputImage: File
    lateinit var image: ImageView
    private val textList = ArrayList<EditText>()
    private val imageList = ArrayList<Bitmap>()
    private val orderList = StringBuilder("t")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        init()
        val linearLayout: LinearLayout = findViewById(R.id.container)
        val insert: ImageButton = findViewById(R.id.insert)
        val save: ImageButton = findViewById(R.id.save)
        //插入图片
        insert.setOnClickListener {
            image = NotesImage(this)
            val editText = NotesText(this)
            val dialogView: View = LayoutInflater.from(this).inflate(R.layout.modify_image, null, false)
            val dialog = this.let { AlertDialog.Builder(it, R.style.myCorDialog) }//设置布局
            val dialogBox = dialog.setView(dialogView)?.create() //添加任务窗口
            dialogBox?.window?.setGravity(Gravity.BOTTOM)
            dialogBox?.window?.attributes?.y = 160 // 对话框下边界
            dialogBox?.show()
            //事件监听器
            dialogView.findViewById<Button>(R.id.takeAPicture).setOnClickListener {//拍照
                turnOnTheCamera()
            }
            dialogView.findViewById<Button>(R.id.photoAlbum).setOnClickListener {//相册
                openAlbum()
            }
            linearLayout.addView(image)
            linearLayout.addView(editText)
            editText.background = null
            textList.add(editText)
            orderList.append("pt")
        }

        //保存错题
        save.setOnClickListener {
            val n = Notes()
            val st = StringBuilder()
            val sp = StringBuilder()
            for (i in 0 until textList.size) {
                val ret = db.insert(textList[i].text.toString())
                st.append("${ret}#")
            }
            for (i in 0 until imageList.size) {
                val ret = db.insert(imageList[i])
                sp.append("${ret}#")
            }
            n.notesTitle = titleText.text.toString()
            if (st.isNotEmpty()) {
                n.textList = st.substring(0, st.length - 1)
            }
            if (sp.isNotEmpty()) {
                n.photoList = sp.substring(0, sp.length - 1)
            }
            n.orderList = orderList.toString()
            n.classify = "数学" // 后续补上个分类
            n.updateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now())
            db.insert(n)
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            SpanUtil.notesList?.refresh()
            finish()
        }

    }

    //对返回的照片进行处理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) //Intent返回结果处理，显示照片
        when (requestCode) {//匹配Code
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) { //✔键
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //对bitmap进行格式转发成PNG
                    if (bitmap != null) { // 图片不为空
                        image.setImageBitmap(bitmap)
                        imageList.add(bitmap)
                    }
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->//将选择的图片显示
                        val bitmap = contentResolver.openFileDescriptor(uri, "r")?.use {
                            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                        }
                        val baos = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos) //对bitmap进行格式转发成PNG
                        if (bitmap != null) { // 图片不为空
                            image.setImageBitmap(bitmap)
                            imageList.add(bitmap)
                        }
                    }
                }
            }
        }
    }


    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE) //指定只显示图片
        intent.type = "image/*"
        startActivityForResult(intent, fromAlbum)
    }

    private fun turnOnTheCamera() {
        outputImage = File(this.externalCacheDir, "output_image.jpg") //存放照片
        //如果文件存在则进行删除
        if (outputImage.exists()) {
            outputImage.delete()
        }
        //创建新的对象
        outputImage.createNewFile()
        //获取图片uri
        imageUri = FileProvider.getUriForFile(this, "com.notes.fileprovider", outputImage)
        //启动相机并获取拍的照片作为活动返回对象
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //设置相片输出保存的Uri路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)  //指定图片的输出地址
        startActivityForResult(intent, takePhoto) //takePhoto为标记代码
    }

    private fun init() {
        textList.add(contentText)
    }
}