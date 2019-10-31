package io.numbers.mediant.ui.onboarding.name_setting_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentOnboardingNameSettingPageBinding
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class OnboardingNameSettingPageFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: OnboardingNameSettingPageViewModel

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[OnboardingNameSettingPageViewModel::class.java]
    }

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
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userName.observe(viewLifecycleOwner, Observer { preferenceHelper.userName = it })
    }
}