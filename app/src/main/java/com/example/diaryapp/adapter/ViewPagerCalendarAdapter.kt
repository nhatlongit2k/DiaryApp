package com.example.diaryapp.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.diaryapp.fragment.FragmentCalendar
import java.time.LocalDate

class ViewPagerCalendarAdapter(var fragmentList: MutableList<FragmentCalendar>, fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCalendar(selectedDate: LocalDate){
        fragmentList[0] = FragmentCalendar.newInstance(selectedDate.minusMonths(1))
        fragmentList[1] = FragmentCalendar.newInstance(selectedDate)
        fragmentList[2] = FragmentCalendar.newInstance(selectedDate.plusMonths(1))
        notifyDataSetChanged()
    }
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}