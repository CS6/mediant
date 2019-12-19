package io.numbers.mediant.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import dagger.android.support.DaggerFragment
import io.numbers.mediant.BuildConfig.APPLICATION_ID
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentMainBinding
import io.numbers.mediant.ui.main.thread.ThreadFragment
import io.numbers.mediant.ui.main.thread_list.ThreadListFragment
import io.numbers.mediant.ui.snackbar.DefaultShowableSnackbar
import io.numbers.mediant.ui.snackbar.ShowableSnackbar
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.util.ActivityRequestCodes
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PermissionRequestType
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber
import javax.inject.Inject

class MainFragment : DaggerFragment(), ShowableSnackbar by DefaultShowableSnackbar() {

    data class Tab(@StringRes val title: Int, val fragmentBuilder: () -> Fragment)

    private val tabs = listOf(
        Tab(R.string.feeds) { ThreadListFragment() },
        Tab(R.string.storage) { ThreadFragment() }
    )

    private lateinit var liveViewCardBehavior: BottomSheetBehavior<CardView>

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[MainViewModel::class.java]
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        viewModel.showSnackbar.observe(viewLifecycleOwner, EventObserver { showSnackbar(view, it) })
        viewModel.showErrorSnackbar.observe(
            viewLifecycleOwner, EventObserver { showErrorSnackbar(view, it) }
        )

        liveViewCardBehavior = BottomSheetBehavior.from(liveViewCardView).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) viewModel.stopLiveView()
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
        viewModel.liveViewCardState.observe(viewLifecycleOwner, Observer {
            liveViewCardBehavior.state = it
        })
    }

    private fun initViewPager() {
        val adapter = MainViewPagerAdapter(tabs, this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setText(tabs[position].title)
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navToSettings -> findNavController().navigate(R.id.action_mainFragment_to_preferencesFragment)
            R.id.captureImage -> prepareCapturingImage()
            R.id.captureVideo -> prepareCapturingVideo()
            R.id.navToPersonalThreadInformation -> preferenceHelper.personalThreadId?.let {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToThreadInformationFragment(it)
                )
            }
            R.id.navToOnboarding -> findNavController().navigate(R.id.action_mainFragment_to_onboardingFragment)
            R.id.openLiveView -> startLiveView()
        }
        return true
    }

    private fun prepareCapturingImage() {
        if (permissionManager.hasPermissions(PermissionRequestType.PROOFMODE_IMAGE)) {
            dispatchCaptureImageIntent()
        } else if (!permissionManager.askPermissions(PermissionRequestType.PROOFMODE_IMAGE, this)) {
            navigateToPermissionRationaleFragment(PermissionRequestType.PROOFMODE_IMAGE.value.rationale)
        }
    }

    private fun prepareCapturingVideo() {
        if (permissionManager.hasPermissions(PermissionRequestType.PROOFMODE_VIDEO)) {
            dispatchCaptureVideoIntent()
        } else if (!permissionManager.askPermissions(PermissionRequestType.PROOFMODE_VIDEO, this)) {
            navigateToPermissionRationaleFragment(PermissionRequestType.PROOFMODE_VIDEO.value.rationale)
        }
    }

    private fun startLiveView() {
        viewModel.liveViewCardState.value = BottomSheetBehavior.STATE_EXPANDED
        viewModel.startLiveView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ActivityRequestCodes.CAPTURE_IMAGE.value -> {
                when (resultCode) {
                    Activity.RESULT_OK -> viewModel.uploadImage()
                    Activity.RESULT_CANCELED -> Timber.i("Camera operation cancelled.")
                    else -> view?.let {
                        showErrorSnackbar(
                            it, RuntimeException("Unknown camera result: $resultCode")
                        )
                    }
                }
            }
            ActivityRequestCodes.CAPTURE_VIDEO.value -> {
                when (resultCode) {
                    Activity.RESULT_OK -> viewModel.uploadVideo()
                    Activity.RESULT_CANCELED -> Timber.i("Camera operation cancelled.")
                    else -> view?.let {
                        showErrorSnackbar(
                            it, RuntimeException("Unknown camera result: $resultCode")
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionRequestType.PROOFMODE_IMAGE.value.code -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    dispatchCaptureImageIntent()
                } else navigateToPermissionRationaleFragment(PermissionRequestType.PROOFMODE_IMAGE.value.rationale)
            }
        }
    }

    private fun dispatchCaptureImageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.also { activity ->
            val documents = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val outputFolder = if (documents != null) documents
            else {
                showSnackbar(requireView(), SnackbarArgs(R.string.message_external_dir_unavailable))
                activity.filesDir
            }

            // Ensure that there's a camera activity to handle the intent.
            intent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go.
                val imageFile = viewModel.createMediaFile(outputFolder, "media.jpg")
                val imageUri = FileProvider.getUriForFile(
                    activity, "$APPLICATION_ID.provider", imageFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, ActivityRequestCodes.CAPTURE_IMAGE.value)
            }
        }
    }

    private fun dispatchCaptureVideoIntent() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        activity?.also { activity ->
            val documents = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val outputFolder = if (documents != null) documents
            else {
                view?.let {
                    showSnackbar(it, SnackbarArgs(R.string.message_external_dir_unavailable))
                }
                activity.filesDir
            }

            intent.resolveActivity(activity.packageManager)?.also {
                val videoFile = viewModel.createMediaFile(outputFolder, "media.mp4")
                val videoUri = FileProvider.getUriForFile(
                    activity, "$APPLICATION_ID.provider", videoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                startActivityForResult(intent, ActivityRequestCodes.CAPTURE_VIDEO.value)
            }
        }
    }

    private fun navigateToPermissionRationaleFragment(@StringRes rationale: Int) {
        MainFragmentDirections.actionMainFragmentToPermissionRationaleFragment(rationale)
            .also { findNavController().navigate(it) }
    }
}