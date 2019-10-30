package io.numbers.mediant.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.numbers.mediant.ui.tab.Tab

class MainViewPagerAdapter(
    private val tabs: List<Tab>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int) = tabs[position].fragment as Fragment
}