package io.numbers.mediant.ui.onboarding.zion_setting_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentOnboardingZionSettingPageBinding
import io.numbers.mediant.ui.snackbar.DefaultShowableSnackbar
import io.numbers.mediant.ui.snackbar.ShowableSnackbar
import io.numbers.mediant.viewmodel.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class OnboardingZionSettingPageFragment : Fragment(),
    ShowableSnackbar by DefaultShowableSnackbar() {

    private val onboardingZionSettingPageViewModel: OnboardingZionSettingPageViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentOnboardingZionSettingPageBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_onboarding_zion_setting_page,
                container,
                false
            )
        binding.lifecycleOwner = this
        binding.viewModel = onboardingZionSettingPageViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onboardingZionSettingPageViewModel.showSnackbar.observe(
            viewLifecycleOwner,
            EventObserver { showSnackbar(view, it) })
        onboardingZionSettingPageViewModel.showErrorSnackbar.observe(
            viewLifecycleOwner, EventObserver { showErrorSnackbar(view, it) }
        )
        onboardingZionSettingPageViewModel.zionEnabled.observe(
            viewLifecycleOwner,
            Observer { onboardingZionSettingPageViewModel.syncSignWithZionPreference() })
    }
}