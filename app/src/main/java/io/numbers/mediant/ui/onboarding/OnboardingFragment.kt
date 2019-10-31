package io.numbers.mediant.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import io.numbers.mediant.R
import io.numbers.mediant.ui.onboarding.end_page.OnboardingEndPageFragment
import io.numbers.mediant.ui.onboarding.name_setting_page.OnboardingNameSettingPageFragment
import io.numbers.mediant.ui.onboarding.zion_setting_page.OnboardingZionSettingPageFragment
import kotlinx.android.synthetic.main.fragment_onboarding.*

class OnboardingFragment : Fragment() {

    private val pageBuilders = listOf(
        { OnboardingZionSettingPageFragment() },
        { OnboardingNameSettingPageFragment() },
        { OnboardingEndPageFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        val adapter = OnboardingViewPagerAdapter(pageBuilders, this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
    }
}