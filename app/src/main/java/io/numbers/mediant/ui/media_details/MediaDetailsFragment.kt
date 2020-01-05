package io.numbers.mediant.ui.media_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.squareup.moshi.JsonAdapter
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentMediaDetailsBinding
import io.numbers.mediant.model.Meta
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MediaDetailsFragment : Fragment() {


    private val mediaDetailsViewModel: MediaDetailsViewModel by viewModel()

    private val metaJsonAdapter: JsonAdapter<Meta> by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMediaDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_media_details, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = mediaDetailsViewModel
        initLiveData()
        return binding.root
    }

    private fun initLiveData() = arguments?.also {
        mediaDetailsViewModel.fileHash.value = MediaDetailsFragmentArgs.fromBundle(it).fileHash
        mediaDetailsViewModel.userName.value = MediaDetailsFragmentArgs.fromBundle(it).userName
        mediaDetailsViewModel.blockTimestamp.value =
            MediaDetailsFragmentArgs.fromBundle(it).blockTimestamp
        mediaDetailsViewModel.blockHash.value = MediaDetailsFragmentArgs.fromBundle(it).blockHash

        mediaDetailsViewModel.meta.value = try {
            metaJsonAdapter.fromJson(MediaDetailsFragmentArgs.fromBundle(it).fileMeta)
        } catch (e: Exception) {
            Meta(Meta.MediaType.UNKNOWN, "N/A", "N/A", "N/A", Meta.SignatureProvider.UNKNOWN)
        }
    }
}