package io.numbers.mediant.ui.onboarding.end_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentOnboardingEndPageBinding
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class OnboardingEndPageFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: OnboardingEndPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[OnboardingEndPageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentOnboardingEndPageBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_onboarding_end_page,
                container,
                false
            )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navToMainFragmentEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_onboardingFragment_to_mainFragment)
        })
    }
}