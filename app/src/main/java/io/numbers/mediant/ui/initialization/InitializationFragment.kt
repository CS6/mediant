package io.numbers.mediant.ui.initialization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentInitializationBinding
import io.numbers.mediant.viewmodel.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class InitializationFragment : Fragment() {

    private val initializationViewModel: InitializationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInitializationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_initialization, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = initializationViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializationViewModel.navToOnboardingFragmentEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_initializationFragment_to_onboardingFragment)
            })
        initializationViewModel.navToMainFragmentEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_initializationFragment_to_mainFragment)
        })
    }
}