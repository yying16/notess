package com.notes.ui

import android.content.Context
import android.graphics.Color
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
class NotesText(context: Context) :AppCompatEditText(context) {
    init {
        super.setWidth(200)
    }
}