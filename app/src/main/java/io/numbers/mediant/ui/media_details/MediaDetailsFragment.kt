package io.numbers.mediant.ui.media_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.squareup.moshi.Moshi
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentMediaDetailsBinding
import io.numbers.mediant.model.Meta
import io.numbers.mediant.model.MetaJsonAdapter
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class MediaDetailsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: MediaDetailsViewModel

    @Inject
    lateinit var moshi: Moshi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[MediaDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMediaDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_media_details, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initLiveData()
        return binding.root
    }

    private fun initLiveData() = arguments?.also {
        viewModel.fileHash.value = MediaDetailsFragmentArgs.fromBundle(it).fileHash
        viewModel.userName.value = MediaDetailsFragmentArgs.fromBundle(it).userName
        viewModel.blockTimestamp.value = MediaDetailsFragmentArgs.fromBundle(it).blockTimestamp
        viewModel.blockHash.value = MediaDetailsFragmentArgs.fromBundle(it).blockHash

        viewModel.meta.value = try {
            MetaJsonAdapter(moshi).fromJson(MediaDetailsFragmentArgs.fromBundle(it).fileMeta)
        } catch (e: Exception) {
            Meta(Meta.MediaType.UNKNOWN, "N/A", "N/A", "N/A", Meta.SignatureProvider.UNKNOWN)
        }
    }
}