package io.numbers.mediant.ui.media_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.databinding.FragmentMediaDetailsBinding
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class MediaDetailsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: MediaDetailsViewModel

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

    private fun initLiveData() = arguments?.let {
        viewModel.imageIpfsPath.value = MediaDetailsFragmentArgs.fromBundle(it).imageIpfsPath
        viewModel.userName.value = MediaDetailsFragmentArgs.fromBundle(it).userName
        viewModel.blockTimestamp.value = MediaDetailsFragmentArgs.fromBundle(it).blockTimestamp
        viewModel.blockHash.value = MediaDetailsFragmentArgs.fromBundle(it).blockHash

        val proofSignatureBundle = try {
            Gson().fromJson(
                MediaDetailsFragmentArgs.fromBundle(it).proofSignatureJson,
                ProofSignatureBundle::class.java
            )
        } catch (e: Exception) {
            ProofSignatureBundle("Not Available", "Not Available", "Not Available")
        }
        viewModel.proof.value = proofSignatureBundle.proof
        viewModel.proofSignature.value = proofSignatureBundle.proofSignature
        viewModel.mediaSignature.value = proofSignatureBundle.mediaSignature
    }
}