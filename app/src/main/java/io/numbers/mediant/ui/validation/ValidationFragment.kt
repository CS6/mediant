package io.numbers.mediant.ui.validation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.moshi.Moshi
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentValidationBinding
import io.numbers.mediant.model.Meta
import io.numbers.mediant.model.MetaJsonAdapter
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_validation.view.*
import timber.log.Timber
import javax.inject.Inject

class ValidationFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ValidationViewModel

    @Inject
    lateinit var moshi: Moshi

    lateinit var uploadButton: Button
    //lateinit var resultButton: Button
    lateinit var validateResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ValidationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentValidationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_validation, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initLiveData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadButton = view.findViewById(R.id.validateUploadButton)
        uploadButton.setOnClickListener {
            viewModel.onUpload()

            // TODO: Use LiveData to set result text.
            Timber.i("Hello, upload button")
            //validateResult = view.findViewById(R.id.validateResult)
            validateResult.text = "Upload is clicked"
            //validateResult.text = viewModel.onUpload()
        }

        /*
        resultButton = view.findViewById(R.id.validateResultButton)
        resultButton.setOnClickListener {
            viewModel.onResult()

            // TODO: Use LiveData to set result text.
            Timber.i("Hello, result button")
            //validateResult = view.findViewById(R.id.validateResult)
            validateResult.text = "Result is clicked"
            //validateResult.text = viewModel.onResult()
        }
        */

        validateResult = view.findViewById(R.id.validateResult)

        val debugObserver = Observer<String> {
                newResult -> validateResult.text = newResult
        }
        viewModel.debug.observe(this, debugObserver)
    }

    private fun initLiveData() = arguments?.also {
        viewModel.fileHash.value = ValidationFragmentArgs.fromBundle(it).fileHash
        viewModel.userName.value = ValidationFragmentArgs.fromBundle(it).userName
        viewModel.blockTimestamp.value = ValidationFragmentArgs.fromBundle(it).blockTimestamp
        viewModel.blockHash.value = ValidationFragmentArgs.fromBundle(it).blockHash

        viewModel.meta.value = try {
            MetaJsonAdapter(moshi).fromJson(ValidationFragmentArgs.fromBundle(it).fileMeta)
        } catch (e: Exception) {
            Meta(Meta.MediaType.UNKNOWN, "N/A", "N/A", "N/A", Meta.SignatureProvider.UNKNOWN)
        }
    }
}