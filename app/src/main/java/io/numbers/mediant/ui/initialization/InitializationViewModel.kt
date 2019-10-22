package io.numbers.mediant.ui.initialization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileInfoListener
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InitializationViewModel @Inject constructor(
    private val textileService: TextileService,
    zionService: ZionService,
    preferenceHelper: PreferenceHelper
) :
    ViewModel() {

    val loadingText = MutableLiveData(R.string.connect_to_ipfs)
    val navToMainFragmentEvent = MutableLiveData<Event<Unit>>()

    init {
        if (!textileService.hasLaunched.value!!) initializeTextile()

        if (zionService.isHtcExodus1 && preferenceHelper.signWithZion) {
            viewModelScope.launch(Dispatchers.IO) { zionService.initZkma() }
        }

        textileService.safelyInvokeIfNodeOnline {
            textileService.initPersonalThread()
            // fire the single live event by posting an arbitrary value at worker thread
            navToMainFragmentEvent.postValue(Event(Unit))
        }
    }

    private fun initializeTextile() {
        if (!textileService.hasInitialized()) {
            loadingText.value = R.string.create_wallet
            textileService.createNewWalletAndAccount()
        }
        loadingText.value = R.string.connect_to_ipfs
        textileService.launch()
        textileService.addEventListener(TextileInfoListener())
    }
}