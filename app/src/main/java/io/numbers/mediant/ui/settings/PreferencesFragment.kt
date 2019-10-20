package io.numbers.mediant.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.numbers.mediant.R

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}