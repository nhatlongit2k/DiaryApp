package com.example.diaryapp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class InputDiaryActivity : AppCompatActivity() {
    lateinit var edtDate: EditText
    lateinit var edtTitle: EditText
    lateinit var edtContent: EditText
    lateinit var btInput : Button
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var useFor: String
    lateinit var database: Database
    lateinit var diary: Diary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_diary)
        database = Database(this, "DatabaseDiary.sqlite", null, 1)
        edtDate = findViewById(R.id.edt_input_date)
        edtTitle = findViewById(R.id.edt_input_title)
        edtContent = findViewById(R.id.edt_input_content)
        btInput = findViewById(R.id.bt_input_button)
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        useFor = intent.getStringExtra("use_for").toString()
        if(useFor.equals("add")){
            btInput.setText("Thêm")
        }
        if(useFor.equals("addFromCalendar")){
            val dateStr: String? = intent.getStringExtra("date_from_calendar")
            btInput.setText("Thêm")
            edtDate.setText(dateStr)
            edtDate.isEnabled = false
        }
        if(useFor.equals("edit")){
            diary = intent.getSerializableExtra("Diary") as Diary
            btInput.setText("sửa")
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val strDate = dateFormat.format(diary.date)
            edtDate.setText(strDate)
            edtDate.isEnabled = false
            edtTitle.setText(diary.title)
            edtContent.setText(diary.content)
        }
        if(useFor.equals("see")){
            diary = intent.getSerializableExtra("Diary") as Diary
            btInput.visibility = View.GONE
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val strDate = dateFormat.format(diary.date)
            edtDate.setText(strDate)
            edtDate.isEnabled = false
            edtTitle.setText(diary.title)
            edtTitle.isEnabled = false
            edtContent.setText(diary.content)
            edtContent.isEnabled = false
        }



        edtDate.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val date = calendar[Calendar.DATE]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]
                val datePickerDialog = DatePickerDialog(this@InputDiaryActivity,
                    { view, year, month, dayOfMonth ->
                        calendar[year, month] = dayOfMonth
                        edtDate.setText(simpleDateFormat.format(calendar.time))
                    }, year, month, date
                )
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis() + 1000
                datePickerDialog.show()
            }
            true
        })
        btInput.setOnClickListener {
            if(useFor.equals("add") || useFor.equals("addFromCalendar")){
                val cursor: Cursor = database.GetData("SELECT * FROM DIARY WHERE Date = '${edtDate.text.toString()} 00:00:00'")
                if(!cursor.moveToNext()){
                    database.QueryData("INSERT INTO DIARY VALUES(null, '"+edtTitle.text.toString()+"', '"+edtContent.text.toString()+"', '"+edtDate.text.toString()+" 00:00:00')")
                    Toast.makeText(this@InputDiaryActivity, "Thêm thành công", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }else{
                    Toast.makeText(this@InputDiaryActivity, "Bạn đã ghi nhật ký vào ngày này!", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(useFor.equals("edit")){
                    database.QueryData("UPDATE DIARY SET Title = '${edtTitle.text.toString()}', Content = '${edtContent.text.toString()}' WHERE ID = ${diary.id}")
                    Toast.makeText(this@InputDiaryActivity, "Sửa thành công", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}