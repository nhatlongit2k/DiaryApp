package com.example.diaryapp.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager.widget.ViewPager
import com.example.diaryapp.R
import com.example.diaryapp.adapter.ViewPagerCalendarAdapter
import java.time.LocalDate

class FragmentMain : Fragment() {

    lateinit var viewPager: ViewPager
    lateinit var preSelectedDate: LocalDate
    lateinit var nextSelectedDate: LocalDate
    var selectedDate: LocalDate = LocalDate.now()
    var fragments: ArrayList<FragmentCalendar> = arrayListOf(
        FragmentCalendar.newInstance(selectedDate.minusMonths(1)),
        FragmentCalendar.newInstance(selectedDate),
        FragmentCalendar.newInstance(selectedDate.plusMonths(1))
    )
    lateinit var adapter: ViewPagerCalendarAdapter

    var focusPage = 1


    companion object{
        lateinit var intentLaunch: ActivityResultLauncher<Intent>
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        viewPager = view.findViewById(R.id.view_pager)

        preSelectedDate = selectedDate.minusMonths(1)
        nextSelectedDate = selectedDate.plusMonths(1)

        adapter = ViewPagerCalendarAdapter(fragments, childFragmentManager)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(1)


        intentLaunch = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null) {
                if(result.resultCode == Activity.RESULT_OK){
                    Log.d("TAG", "zô đây nè: $selectedDate")
                    adapter.notifyDataSetChanged()
//                    adapter.setCalendar(selectedDate)

//                    viewPager.setCurrentItem(1,false)
                }
            }
        }


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                focusPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (focusPage < 1) {
                        selectedDate = selectedDate.minusMonths(1)
                    } else if (focusPage > 1) {
                        selectedDate = selectedDate.plusMonths(1)
                    }
                    adapter.setCalendar(selectedDate)
                    viewPager.setCurrentItem(1,false)
                }
            }
        })

        return view
    }

}