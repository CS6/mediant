package io.numbers.mediant.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.util.PreferenceHelper
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat() {

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
            if (zionService.isHtcExodus1) isEnabled = true
            else {
                isEnabled = false
                preferenceHelper.signWithZion = false
                summary =
                    "${resources.getString(R.string.summary_not_support_zion)}: ${zionService.deviceName}"
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
        showCopyMessageSnackbar()
    }

    private fun showCopyMessageSnackbar() =
        view?.let { Snackbar.make(it, R.string.copied_to_clipboard, Snackbar.LENGTH_LONG).show() }
}