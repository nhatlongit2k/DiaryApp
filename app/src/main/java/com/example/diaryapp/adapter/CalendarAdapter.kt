package com.example.diaryapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.Database
import com.example.diaryapp.Diary
import com.example.diaryapp.InputDiaryActivity
import com.example.diaryapp.R
import com.example.diaryapp.fragment.FragmentCalendar
import com.example.diaryapp.fragment.FragmentMain
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarAdapter(var dayOfMonth: ArrayList<String>, var monthYear: String, var context: Activity?) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    var click_time =0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        Log.d("TAG", "position: $position")
        if (dayOfMonth[position].contains("*")){
            holder.dayOfMonth.isEnabled = false
            var textDay = dayOfMonth[position].substring(0, dayOfMonth[position].length-1)
            holder.dayOfMonth.setText(textDay)
        }else{
            holder.dayOfMonth.isEnabled = true
            holder.dayOfMonth.setText(dayOfMonth[position])
            var database = Database(context, "DatabaseDiary.sqlite", null, 1)
            var cursor: Cursor = database.GetData("SELECT * FROM DIARY WHERE Date = '$monthYear-${dayOfMonth[position]} 00:00:00'")
            if(cursor.moveToNext()){
//                Log.d("TAG", "Doi mau o day: ")
                holder.dayOfMonth.setBackgroundColor(Color.RED)
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val content = cursor.getString(2)
                val date = cursor.getString(3)
                val dateConvert: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
                val diary = Diary(id,title,content, dateConvert)
                holder.dayOfMonth.setOnClickListener{
                    click_time++
                    val handler = Handler()
                    handler.postDelayed({
                        if(click_time == 1){

                        }else if(click_time==2){
                            val intent = Intent(context, InputDiaryActivity::class.java)
                            intent.putExtra("use_for", "edit")
                            intent.putExtra("Diary",diary)
                            FragmentMain.intentLaunch.launch(intent)
                        }
                        click_time = 0
                    }, 300)
                }
            }else{
                holder.dayOfMonth.setOnClickListener{
                    click_time++
                    val handler = Handler()
                    handler.postDelayed({
                        if(click_time == 1){

                        }else if(click_time==2){
                            val intent = Intent(context, InputDiaryActivity::class.java)
                            intent.putExtra("use_for", "addFromCalendar")
                            intent.putExtra("date_from_calendar", "$monthYear-${dayOfMonth[position]}")
                            FragmentMain.intentLaunch.launch(intent)
                        }
                        click_time = 0
                    }, 200)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return dayOfMonth.size
    }
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dayOfMonth: TextView = itemView.findViewById(R.id.tv_date)
    }
}