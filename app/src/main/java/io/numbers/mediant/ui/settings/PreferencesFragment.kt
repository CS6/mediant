package io.numbers.mediant.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.htc.htcwalletsdk.Export.RESULT
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.ui.snackbar.DefaultShowableSnackbar
import io.numbers.mediant.ui.snackbar.ShowableSnackbar
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.util.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat(),
    ShowableSnackbar by DefaultShowableSnackbar() {

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var zionService: ZionService

    private val summaryMaxLength = 100

    private lateinit var clipboard: ClipboardManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as DaggerAppCompatActivity).androidInjector().inject(this)
        clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        initTextilePreferences()
        initProofModePreferences()
        initZionPreferences()
    }

    private fun initTextilePreferences() {
        findPreference<Preference>(preferenceHelper.preferenceKeyWalletRecoveryPhrase)?.apply {
            summary = singleLineSummary(preferenceHelper.walletRecoveryPhrase)
            setOnPreferenceClickListener {
                copyToClipboard(
                    "Textile wallet recovery phrase",
                    preferenceHelper.walletRecoveryPhrase
                )
                true
            }
        }
        findPreference<Preference>(preferenceHelper.preferenceKeyPersonalThreadId)?.apply {
            summary = singleLineSummary(preferenceHelper.personalThreadId)
            setOnPreferenceClickListener {
                copyToClipboard(
                    "Textile personal thread ID",
                    preferenceHelper.personalThreadId
                )
                true
            }
        }
    }

    private fun initProofModePreferences() {
        findPreference<Preference>(preferenceHelper.preferenceKeyProofModePgpPassword)?.apply {
            summary = singleLineSummary(preferenceHelper.proofModePgpPassword)
            setOnPreferenceClickListener {
                copyToClipboard(
                    "ProofMode PGP password",
                    preferenceHelper.proofModePgpPassword
                )
                true
            }
        }
        findPreference<Preference>(preferenceHelper.preferenceKeyProofModePgpPublicKey)?.apply {
            summary = singleLineSummary(preferenceHelper.proofModePgpPublicKey)
            setOnPreferenceClickListener {
                copyToClipboard(
                    "ProofMode PGP public key",
                    preferenceHelper.proofModePgpPublicKey
                )
                true
            }
        }
    }

    private fun initZionPreferences() {
        findPreference<SwitchPreferenceCompat>(preferenceHelper.preferenceKeySignWithZion)?.apply {
            if (zionService.isSupported) isEnabled = true
            else {
                isEnabled = false
                isChecked = false
                summary =
                    "${resources.getString(R.string.unsupported_device)}: ${zionService.deviceName}"
            }

            setOnPreferenceChangeListener { preference, newValue ->
                if (preference == this && newValue == true) {
                    lifecycleScope.launch(Dispatchers.Default) {
                        val result = zionService.initZkma()
                        if (result != RESULT.SUCCESS) {
                            withContext(Dispatchers.Main) {
                                summary = "Error Code: $result"
                                isChecked = false
                            }
                        }
                    }
                }
                true
            }
        }
    }

    private fun singleLineSummary(summary: String?): String {
        if (summary != null) {
            if (summary.length > summaryMaxLength) return summary.take(summaryMaxLength) + "..."
            return summary
        }
        return ""
    }

    private fun copyToClipboard(label: String, text: String?) {
        ClipData.newPlainText(label, text).also { clipboard.setPrimaryClip(it) }
        view?.let { showSnackbar(it, SnackbarArgs(R.string.copied_to_clipboard)) }
    }
}