package com.ugao.ugaomanagement.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.ugao.ugaomanagement.fragment.*

class InvoicePagerAdapter : FragmentStatePagerAdapter {

    constructor(fm: FragmentManager?) : super(fm)

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        when (position) {
            0 -> frag = InvoiceFragmentNew()
            1 -> frag = InvoiceFragmentDelivered()
            2 -> frag = InvoiceFragmentComplete()
        }
        return frag!!
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when (position) {
            0 -> title = "Chưa nhận"
            1 -> title = "Đang giao"
            2 -> title = "Hoàn thành"
        }

        return title
    }
}