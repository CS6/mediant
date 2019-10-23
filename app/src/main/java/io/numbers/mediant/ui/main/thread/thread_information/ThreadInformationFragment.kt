package io.numbers.mediant.ui.main.thread.thread_information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentThreadInformationBinding
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadInformationFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadInformationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadInformationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentThreadInformationBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_thread_information, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initLiveData()
        return binding.root
    }

    private fun initLiveData() {
        arguments?.let {
            viewModel.threadId.value = ThreadInformationFragmentArgs.fromBundle(it).threadId
        }
    }
}