package com.notes.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.notes.activity.DetailActivity
import com.notes.R
import com.notes.adapter.NotesAdapter
import com.notes.dao.DatabaseHelper
import com.notes.fragment.NotesFragment
import com.notes.util.SpanUtil
import com.notes.util.Theme
import com.notes.util.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*

class MainActivity : AppCompatActivity(), DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {
    private var isExit = false // 双击退出程序
    private val db = DatabaseHelper(this, 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        //传递对象
        SpanUtil.main = this
        //初始化APP
        initApp()
        setTheme(Theme.presentTheme) // 设置主题
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onBackPressed() { // 重写返回键
        exitBy2Click() // 双击退出程序
    }

    override fun recreate() {
        super.recreate()
        refreshUserData()
    }

    override fun onRestart() {
        super.onRestart()
        refreshUserData()
    }


    /**
     * 修改标题栏
     * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // 设置标题栏样式
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    /**
     * 导航栏
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    /**
     * 抽屉监听器
     * */
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if (slideOffset > 0) { //打开抽屉时则让主界面往后退（完成过渡）
            fragment_layout.elevation = drawerLayout.elevation - 100
        } else {
            fragment_layout.elevation = drawerLayout.elevation + 100
        }
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawers() // 关闭抽屉栏
        when (item.itemId) {
            R.id.notes -> { // 任务界面
            }

            R.id.recycleBin -> {

            }

            R.id.navCountdown -> { // 倒计时
            }

            R.id.statistics -> { // 数据统计

            }

            R.id.theme -> { // 更换主题
                if (Theme.presentTheme == Theme.lightTheme) {
                    Theme.setTheme(Theme.darkTheme)
                } else {
                    Theme.setTheme(Theme.lightTheme)
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()//关闭当前活动
            }
        }
        return true
    }

    private fun init() { // 初始化
        // 设置导航栏
        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_drawerlist)
        }
        navView.setCheckedItem(R.id.notes)
        navView.setNavigationItemSelectedListener { // 抽屉栏设置
            drawerLayout.closeDrawers()
            true
        }
        val headerLayout = navView.getHeaderView(0)//设置抽屉按钮
        headerLayout.userImage.setOnClickListener {  // 用户头像
            startActivity(Intent(this, IndividualActivity::class.java))
        }
        navView.setNavigationItemSelectedListener(this)//抽屉选项添加事件监听
        drawerLayout.addDrawerListener(this)
        //切换碎片
        replaceFragment(NotesFragment())
        //初始化侧边栏信息
        refreshUserData()
    }


    private fun initApp() { // app初始化
        val preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val temp = preferences.getInt("userImage", -1)
        if (temp == -1) {//未初始化
            // /初始化数据库
            db.initDataBase()
            val editor = getSharedPreferences("user", Context.MODE_PRIVATE).edit()
            editor.putString("username", "黄宏杰")
            editor.putString("telephone", "14789632500")
            editor.putString("email", "14789632500@tele.com")
            editor.putInt("userImage", 1)
            editor.apply()
            User.username = "黄宏杰"
            User.telephone = "13128438703"
            User.email = "14789632500@tele.com"
            User.userImage = 1
            User.userImageBitmap = db.getPhoto(User.userImage)
        }else{ // 已初始化
            val preferences = getSharedPreferences("user",Context.MODE_PRIVATE)
            User.username = preferences.getString("username","用户名")!!
            User.telephone = preferences.getString("telephone","")!!
            User.email = preferences.getString("email","")!!
            User.userImage = preferences.getInt("userImage",1)
            User.userImageBitmap = db.getPhoto(User.userImage)
        }

    }
    /**
     * 初始化用户信息
     * */
    private fun refreshUserData() {
        val headerLayout = navView.getHeaderView(0)//设置抽屉按钮
        //初始化侧边框用户信息
        headerLayout.userName.text = User.username // 用户名
        headerLayout.userImage.setImageBitmap(User.userImageBitmap)
        headerLayout.userName.setTextColor(Theme.getFontColor())
        headerLayout.telephone.text = User.telephone
        headerLayout.email.text = User.email
    }


    /**
     * 跟换子窗口
     * */
    private fun replaceFragment(fragment: Fragment) { // 切换碎片
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.commit()
    }

    /**
     * 点击两次返回键才能退出程序（防止误触）
     * */
    private fun exitBy2Click() {
        var tExit: Timer? = null
        if (!isExit) {
            isExit = true // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            tExit = Timer()
            tExit.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false // 取消退出
                }
            }, 2000) // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish()
        }
    }

    /**
     * 打开添加错题界面
     * */
     fun addNotes() {
        startActivity(Intent(this, NotesActivity::class.java))
    }

    /**
     * 打开添加错题界面
     * */
    fun notesDetail(id:Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }


}