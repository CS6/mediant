package io.numbers.mediant.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.EXTERNAL_INVITE_LINK_HOST
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.ui.snackbar.DefaultShowableSnackbar
import io.numbers.mediant.ui.snackbar.ShowableSnackbar
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in BaseActivity.onCreate() method.
class BaseActivity : DaggerAppCompatActivity(), ShowableSnackbar by DefaultShowableSnackbar() {

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
        val view: View = findViewById(android.R.id.content)
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.also {
                if (it.toString().startsWith(EXTERNAL_INVITE_LINK_HOST)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            textileService.acceptExternalInvite(it)
                            showSnackbar(
                                view, SnackbarArgs(R.string.message_accepting_thread_invite)
                            )
                        } catch (e: Exception) {
                            showErrorSnackbar(view, e)
                        }
                    }
                } else showSnackbar(view, SnackbarArgs(R.string.message_invite_parsing_error))
            }
        }
    }
}