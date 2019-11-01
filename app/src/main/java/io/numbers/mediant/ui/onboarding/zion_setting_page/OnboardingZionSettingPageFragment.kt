package io.numbers.mediant.ui.onboarding.zion_setting_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentOnboardingZionSettingPageBinding
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class OnboardingZionSettingPageFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: OnboardingZionSettingPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[OnboardingZionSettingPageViewModel::class.java]
    }

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
        binding.viewModel = viewModel
        return binding.root
    }
}