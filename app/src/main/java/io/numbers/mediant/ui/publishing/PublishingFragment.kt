package io.numbers.mediant.ui.publishing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentPublishingBinding
import io.numbers.mediant.ui.listeners.ItemClickListener
import org.koin.android.viewmodel.ext.android.viewModel

class PublishingFragment : Fragment(), ItemClickListener {

    private val publishingViewModel: PublishingViewModel by viewModel()

    private val adapter = PublishingRecyclerViewAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPublishingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_publishing, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = publishingViewModel
        binding.recyclerView.adapter = adapter

        arguments?.let {
            publishingViewModel.dataHash.value = PublishingFragmentArgs.fromBundle(it).dataHash
            publishingViewModel.fileHash.value = PublishingFragmentArgs.fromBundle(it).fileHash
            publishingViewModel.fileMeta.value = PublishingFragmentArgs.fromBundle(it).fileMeta
            publishingViewModel.userName.value = PublishingFragmentArgs.fromBundle(it).userName
            publishingViewModel.blockTimestamp.value =
                PublishingFragmentArgs.fromBundle(it).blockTimestamp
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publishingViewModel.threadList.observe(viewLifecycleOwner, Observer { adapter.data = it })
    }

    override fun onItemClick(position: Int) =
        publishingViewModel.publishFile(adapter.data[position].id)
}