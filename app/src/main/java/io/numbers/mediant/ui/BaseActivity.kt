package io.numbers.mediant.ui

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in BaseActivity.onCreate() method.
@ExperimentalCoroutinesApi
class BaseActivity : DaggerAppCompatActivity(), CoroutineScope by MainScope() {

    @Inject
    lateinit var textileService: TextileService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.initializationFragment, R.id.mainFragment))
        toolbar.setupWithNavController(navController, appBarConfiguration)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.also {
                if (it.toString().startsWith("https://www.textile.photos/invites/new")) {
                    launch(Dispatchers.IO) {
                        try {
                            textileService.acceptExternalInvite(it)
                            showSnackbar(R.string.message_accepting_thread_invite)
                        } catch (e: Exception) {
                            showErrorSnackbar(e.message)
                        }
                    }
                } else Timber.e("Failed to parse invitation acceptance: $it")
            }
        }
    }

    private fun showSnackbar(@StringRes message: Int) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun showErrorSnackbar(errorMessage: String?) {
        if (errorMessage.isNullOrEmpty()) {
            showSnackbar(R.string.message_thread_invite_error)
        } else {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                errorMessage,
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }
            snackbar.show()
        }
    }
}