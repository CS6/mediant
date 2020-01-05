package io.numbers.mediant.ui.onboarding.name_setting_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentOnboardingNameSettingPageBinding
import io.numbers.mediant.util.PreferenceHelper
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class OnboardingNameSettingPageFragment : Fragment() {


    private val onboardingNameSettingPageViewModel: OnboardingNameSettingPageViewModel by viewModel()

    private val preferenceHelper: PreferenceHelper by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentOnboardingNameSettingPageBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_onboarding_name_setting_page,
                container,
                false
            )
        binding.lifecycleOwner = this
        binding.viewModel = onboardingNameSettingPageViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onboardingNameSettingPageViewModel.userName.observe(
            viewLifecycleOwner,
            Observer { preferenceHelper.userName = it })
    }
}