package com.notes.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.notes.R
import com.notes.activity.MainActivity
import com.notes.activity.NotesActivity
import com.notes.adapter.NotesAdapter
import com.notes.dao.DatabaseHelper
import com.notes.domain.Notes
import com.notes.util.SpanUtil
import com.notes.util.Theme
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlin.concurrent.thread

class NotesFragment : Fragment(), View.OnClickListener,TabLayout.OnTabSelectedListener  {
    private lateinit var db: DatabaseHelper
    private var adapter: NotesAdapter? = null // 错题适配器
    private lateinit var classify: TabLayout.Tab  // 顶部导航栏————分类
    private lateinit var time: TabLayout.Tab  // 顶部导航栏————时间
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            swipeRefresh.isRefreshing=false
            //进行消息处理
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener { // 下拉查找
            search(view)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }



    override fun onTabSelected(tab: TabLayout.Tab?) {
        when(tab?.position){
            0 ->{ // 点击分类按钮
//                Toast.makeText(activity, "分类", Toast.LENGTH_SHORT).show()
            }
            1 ->{ // 点击时间按钮
//                Toast.makeText(activity, "时间", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when(tab?.position){
            0 ->{ // 点击分类按钮
//                Toast.makeText(activity, "选中分类", Toast.LENGTH_SHORT).show()
            }
            1 ->{ // 点击时间按钮
//                Toast.makeText(activity, "选中时间", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        when(tab?.position){
            0 ->{ // 点击分类按钮
//                Toast.makeText(activity, "没选分类", Toast.LENGTH_SHORT).show()
            }
            1 ->{ // 点击时间按钮
//                Toast.makeText(activity, "没选时间", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.fab) {// 悬浮球
            Toast.makeText(activity, "添加错题", Toast.LENGTH_SHORT).show()
            SpanUtil.main?.addNotes()
        }
    }

    private fun init() { // 初始化
        //顶部导航栏
        SpanUtil.notesList = this
        classify = tabLayout.newTab().setText("所有科目")
        time = tabLayout.newTab().setText("时间")
        tabLayout.addTab(classify)
        tabLayout.addTab(time)
        tabLayout.addOnTabSelectedListener(this)
        db = context?.let { DatabaseHelper(it,1) }!!
        fab.setOnClickListener(this)
        NotesRecyclerView.layoutManager = LinearLayoutManager(activity)
        refresh()

    }

    private fun search(v:View){
        val dialogView: View = LayoutInflater.from(v.context).inflate(R.layout.task_search, null, false)
        val dialog = this.let { AlertDialog.Builder(v.context, Theme.getSearchStyle()) }//设置布局(可用于实现主题转换
        val frame = dialog.setView(dialogView).create() //添加任务窗口
        frame.show() // 显示对话框
        swipeRefresh.isRefreshing=false
        dialogView.findViewById<Button>(R.id.search).setOnClickListener { // 查询
            val content = dialogView.findViewById<EditText>(R.id.content).text.toString() // 关键字
            thread {
                Thread.sleep(500)
                val msg = Message()
                msg.what = 0 //刷新标志
            }
            tabLayout.getTabAt(0)?.select() // 跳转到综合
            frame.dismiss()
        }
    }
    fun refresh(){
        adapter = NotesAdapter(db.getNotes())
        NotesRecyclerView.adapter = adapter
    }
}