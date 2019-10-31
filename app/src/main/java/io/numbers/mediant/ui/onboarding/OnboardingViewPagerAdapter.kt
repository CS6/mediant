package io.numbers.mediant.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingViewPagerAdapter(
    private val pageBuilders: List<() -> Fragment>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = pageBuilders.size

    override fun createFragment(position: Int) = pageBuilders[position]()
}