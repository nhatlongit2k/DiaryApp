package com.example.diaryapp.fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.Database
import com.example.diaryapp.Diary
import com.example.diaryapp.InputDiaryActivity
import com.example.diaryapp.R
import com.example.diaryapp.adapter.AdapterDiaryRecycler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentDiary : Fragment() {

    lateinit var edtSearch: EditText
    lateinit var btSearch: Button
    lateinit var diaryRecyclerView: RecyclerView
    lateinit var diaryArrayList: ArrayList<Diary>
    lateinit var layoutManager: LinearLayoutManager
    lateinit var database: Database
    companion object{
        lateinit var intentLaunch: ActivityResultLauncher<Intent>
    }
//    lateinit var intentLaunch: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        edtSearch = view.findViewById(R.id.edt_fragdiary_search)
        btSearch = view.findViewById(R.id.bt_fragdiary_search)
        database = Database(context, "DatabaseDiary.sqlite", null, 1)
        diaryRecyclerView = view.findViewById(R.id.fragmentdiary_recyclerview)
        layoutManager = LinearLayoutManager(context)
        diaryRecyclerView.layoutManager=layoutManager
        diaryRecyclerView.setHasFixedSize(true)
        diaryArrayList = arrayListOf<Diary>()

        diaryRecyclerView.adapter = AdapterDiaryRecycler(diaryArrayList, activity)
        getData()

        intentLaunch = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null) {
                if(result.resultCode == Activity.RESULT_OK){
                    getData()
                }
            }
        }

        val btAdd : FloatingActionButton = view.findViewById(R.id.bt_fragmentdiary_ring)
        btAdd.setOnClickListener {
            var intent = Intent(activity, InputDiaryActivity::class.java)
            intent.putExtra("use_for", "add")
            intentLaunch.launch(intent)
        }

//        btSearch.setOnClickListener {
//            Log.d("TAG", "onCreateView: Tim kiem")
//            if(edtSearch.text.toString().equals("")){
//                diaryRecyclerView.adapter = AdapterDiaryRecycler(diaryArrayList, activity)
//                diaryRecyclerView.adapter?.notifyDataSetChanged()
//            }else{
//                var searchArrayList: ArrayList<Diary> = arrayListOf<Diary>()
//                for(i in 0..diaryArrayList.size-1){
//                    if(diaryArrayList[i].title.contains(edtSearch.text.toString())){
//                        searchArrayList.add(diaryArrayList[i])
//                    }
//                    diaryRecyclerView.adapter = AdapterDiaryRecycler(searchArrayList, activity)
//                    diaryRecyclerView.adapter?.notifyDataSetChanged()
//                }
//            }
//        }
        
        edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Toast.makeText(context, "Backup Restored...", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onTextChanged: Ok")
                if(edtSearch.text.toString().equals("")){
                    diaryRecyclerView.adapter = AdapterDiaryRecycler(diaryArrayList, activity)
                    diaryRecyclerView.adapter?.notifyDataSetChanged()
                }else{
                    var searchArrayList: ArrayList<Diary> = arrayListOf<Diary>()
                    for(i in 0..diaryArrayList.size-1){
                        if(diaryArrayList[i].title.contains(edtSearch.text.toString())){
                            searchArrayList.add(diaryArrayList[i])
                        }
                        diaryRecyclerView.adapter = AdapterDiaryRecycler(searchArrayList, activity)
                        diaryRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        return view
    }

    private fun getData() {
        diaryArrayList.clear()
        val cursorData: Cursor = database.GetData("SELECT * FROM DIARY")
        while (cursorData.moveToNext()){
            val id = cursorData.getInt(0)
            val title = cursorData.getString(1)
            val content = cursorData.getString(2)
            val date = cursorData.getString(3)
            val dateConvert: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
            diaryArrayList.add(Diary(id,title,content, dateConvert))
        }
        Collections.sort(diaryArrayList,
            Comparator<Diary> { o1, o2 -> o1.date.compareTo(o2.date) })
        diaryRecyclerView.adapter?.notifyDataSetChanged()
    }
}