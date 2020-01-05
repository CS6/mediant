package io.numbers.mediant.ui.onboarding.end_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentOnboardingEndPageBinding
import io.numbers.mediant.viewmodel.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class OnboardingEndPageFragment : Fragment() {

    private val onboardingEndPageViewModel: OnboardingEndPageViewModel by viewModel()

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
        binding.viewModel = onboardingEndPageViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onboardingEndPageViewModel.navToMainFragmentEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_onboardingFragment_to_mainFragment)
            })
    }
}