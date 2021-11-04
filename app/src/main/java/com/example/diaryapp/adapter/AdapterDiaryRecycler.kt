package com.example.diaryapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.Database
import com.example.diaryapp.Diary
import com.example.diaryapp.InputDiaryActivity
import com.example.diaryapp.R
import com.example.diaryapp.fragment.FragmentDiary
import java.text.DateFormat
import java.text.SimpleDateFormat

class AdapterDiaryRecycler(val diaryList: ArrayList<Diary>, val activity: Activity?): RecyclerView.Adapter<AdapterDiaryRecycler.DiaryViewHolder>(){


    val UPDATE_CODE = 111

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_diary, parent, false)
        return DiaryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val currentItem = diaryList[position]
        holder.tvTitle.setText(currentItem.title)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val strDate = dateFormat.format(currentItem.date)
        holder.tvDate.setText(strDate)
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }
    inner class DiaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvDate: TextView = itemView.findViewById(R.id.tv_listdiary_date)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_listdiary_title)
        val btDelete: ImageView = itemView.findViewById(R.id.ImageDeleteJob)
        val btEdit: ImageView = itemView.findViewById(R.id.ImageEditJob)
        val database = Database(activity, "DatabaseDiary.sqlite", null, 1)
        init {
            btDelete.setOnClickListener {
                val diary = diaryList[layoutPosition]
                database.QueryData("DELETE FROM DIARY WHERE ID = ${diary.id}")
                Toast.makeText(activity, "Xóa thành công", Toast.LENGTH_LONG).show()
                diaryList.removeAt(layoutPosition)
                notifyDataSetChanged()
            }

            btEdit.setOnClickListener {
                val diary = diaryList[layoutPosition]
                val intent = Intent(activity, InputDiaryActivity::class.java)
                intent.putExtra("use_for", "edit")
                intent.putExtra("Diary", diary)
                FragmentDiary.intentLaunch.launch(intent)
            }

            itemView.setOnClickListener {
                val diary = diaryList[layoutPosition]
                val intent = Intent(activity, InputDiaryActivity::class.java)
                intent.putExtra("use_for", "see")
                intent.putExtra("Diary", diary)
                activity?.startActivity(intent)
            }
        }
    }
}