package com.notes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.notes.R
import com.notes.activity.DetailActivity
import com.notes.domain.Notes
import com.notes.util.SpanUtil


class NotesAdapter(private var notesList: ArrayList<Notes>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category:ImageView = view.findViewById(R.id.category)
        val notesTitle:TextView = view.findViewById(R.id.notesTitle)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //事件监听
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_item, parent, false)
        val viewHolder = ViewHolder(view)
        /*点击事件*/
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val notes = notesList[position]
            Toast.makeText(SpanUtil.main, "${notes.notesTitle}", Toast.LENGTH_SHORT).show()
            SpanUtil.main?.notesDetail(notes.notesId)
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notes = notesList[position]
        holder.notesTitle.text = notes.notesTitle
//        holder.category
    }

    override fun getItemCount() = notesList.size

}