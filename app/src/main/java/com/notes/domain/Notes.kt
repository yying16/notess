package com.notes.domain

class Notes() {
    var notesId: Int = 0 // id
    var notesTitle: String = "" // 标题
    var textList: String = "" // 文本
    var photoList: String = "" // 图片
    var orderList: String = "" // 顺序
    var updateTime: String = "" // 更新时间
    var classify: String = "" // 分类
    var deleted: Boolean = false // 是否逻辑删除
    var wordNumber: Int = 0 // 总字数

    constructor(notesId:Int,notesTitle:String,classify:String) : this() {
        this.notesId = notesId
        this.notesTitle = notesTitle
        this.classify = classify
    }
    constructor(notesId: Int,notesTitle: String,textList:String,photoList:String,orderList:String,updateTime:String,classify: String,wordNumber:Int):this(){
        this.notesId = notesId
        this.notesTitle = notesTitle
        this.textList = textList
        this.photoList = photoList
        this.orderList = orderList
        this.updateTime = updateTime
        this.classify = classify
        this.wordNumber = wordNumber
    }
}